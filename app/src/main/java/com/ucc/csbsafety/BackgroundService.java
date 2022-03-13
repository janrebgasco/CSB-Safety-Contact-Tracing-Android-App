package com.ucc.csbsafety;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BackgroundService  extends Service {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();// Get a reference to your user
    List<String> exposureDates = new ArrayList<>();
    int id = 1;
    DatabaseReference mDatabase;
    FirebaseAuth user;
    String myUid;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        exposureDates = new ArrayList<>();
        initializeDays();
        user = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = user.getUid();
        myUid = user.getUid();

        checkUserStatus(uid);
        getContactsNotif(uid);

        return START_STICKY;
    }

    private void getContactsNotif(String uid) {
        DatabaseReference ref = database.getReference("ClosedContact/"+uid);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final int[] i = {0};
                for (int j = 0;j <= exposureDates.size()-1;j++) {

                    DatabaseReference keyRef = database.getReference("ClosedContact/" + uid + "/" + exposureDates.get(j));


                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String dateOfExposure = String.valueOf(dataSnapshot.child("date").getValue());

                            boolean childExist = !dateOfExposure.equals("null");
                            if (childExist) {

                                Iterable<DataSnapshot> ds = dataSnapshot.child("ccList").getChildren();
                                for( DataSnapshot ccListChild : ds){
                                    i[0]++;
                                    String uid = String.valueOf(ccListChild.child("uid").getValue());
                                    String date = String.valueOf(ccListChild.child("date").getValue());
                                    String time = String.valueOf(ccListChild.child("time").getValue());
                                    String room = String.valueOf(ccListChild.child("department").getValue());

                                    checkIfPositive(uid,date,room,time);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    keyRef.addListenerForSingleValueEvent(eventListener);
                }



//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        ref.addValueEventListener(listener);
    }
    private String getTime() {
        String delegate = "hh:mm aaa";
        return (String) android.text.format.DateFormat.format(delegate,Calendar.getInstance().getTime());
    }
    private void checkUserStatus(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userStatus = String.valueOf(dataSnapshot.child("Status").getValue());
                String userSymptoms = String.valueOf(dataSnapshot.child("userSymptoms").getValue());


                if(userStatus.equals("positive")){
                    String fullDate = new SimpleDateFormat("MMM dd, y", Locale.getDefault()).format(new Date());
                    String time = getTime();
                    String desc = "You would be marked as positive\nall your close contacts would be notified";
                    String header = "Test result verified";
                    String moreInfo = "Your test Result had been verified on "+fullDate+" at "+time;

                    Notifs notifs = new Notifs(header,desc,moreInfo,time,fullDate);
                    mDatabase.child("Notifications").child(uid).push().setValue(notifs);

                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

                    b.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.logo)
                            .setTicker("CSB Safety")
                            .setContentTitle(header)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                            .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent)
//                            .setOngoing(true)
                            .setContentInfo("Info");

                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    b.setSound(alarmSound)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(id, b.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkIfPositive(String uid,String date,String room,String time){


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("users/"+uid);
        ref.child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String Status = snapshot.getValue().toString();
                if (Status.equals("positive")){
                    id++;
                    String fullDate = new SimpleDateFormat("MMM dd, y", Locale.getDefault()).format(new Date());
                    String time = getTime();
                    String title = "Positive contact warning!!";
                    String desc = "Positive contact detected at Room: "+room;
                    String moreInfo = "Uid:"+uid+" \nDate: "+date+" \nTime:"+time+" \nRoom:"+room;

                    Notifs notifs = new Notifs(title,desc,moreInfo,time,fullDate);
                    mDatabase.child("Notifications").child(myUid).push().setValue(notifs);

                    Intent intent = new Intent(getApplicationContext(), closeContacts.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

                    b.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.logo)
                            .setTicker("CSB Safety")
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                            .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent)
                            .setContentInfo("Info");

                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    b.setSound(alarmSound)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(id, b.build());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);
    }

    private void dispUserName(FirebaseDatabase database) {
        FirebaseAuth user = FirebaseAuth.getInstance();// Initialize Firebase Auth


        String id = user.getUid();
        //displaying Fullname after getting it to realtime db
        DatabaseReference ref = database.getReference("users/"+id);

        // Attach a listener to read the data at your profile reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                assert profile != null;
                Toast.makeText(getApplicationContext(), ""+profile.getFullname(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    private void initializeDays() {
        SimpleDateFormat sd = new SimpleDateFormat("MMM dd");
        SimpleDateFormat formatter = new SimpleDateFormat("y-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            Date date = Calendar.getInstance().getTime();
            String formattedDate = formatter.format(date);

            startDate = formatter.parse(formattedDate);
            endDate = formatter.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.add(Calendar.DAY_OF_YEAR, -13);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.add(Calendar.DAY_OF_YEAR, 1);


        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

            exposureDates.add(formatter.format(date));

            //Toast.makeText(getApplicationContext(), ""+formatter.format(date), Toast.LENGTH_SHORT).show();
        }
    }
}
