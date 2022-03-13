package com.ucc.csbsafety;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class accountSetup extends AppCompatActivity {
    private FirebaseAuth user;
    private DatabaseReference mDatabase;
    TextView txtFname, txtLname, txtEmail, txtAge, txtAddress, txtEstablishment,txtboxStreet,txtboxBarangay,txtboxCity;
    Button btnSave,mUpload;
    CircleImageView profilePic;
    Spinner txtGender;
    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    final int CODE_GALLERY_REQUEST = 999;
    Bitmap bitmap;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        txtFname = findViewById(R.id.settAccFname);
        txtLname = findViewById(R.id.settAccLname);
        txtEmail = findViewById(R.id.settAccEmail);
        txtAge = findViewById(R.id.settAccAge);
        txtGender = findViewById(R.id.settAccGender);
        txtAddress = findViewById(R.id.settAccAddress);
        txtEstablishment = findViewById(R.id.settAccEstablishment);
        btnSave = findViewById(R.id.btnSaveAcc);
        profilePic = findViewById(R.id.settAccImg);
        mUpload = findViewById(R.id.btnUploadAcc);
        txtboxStreet = findViewById(R.id.txtboxStreet);
        txtboxBarangay = findViewById(R.id.txtboxBarangay);
        txtboxCity = findViewById(R.id.txtboxCity);


        user = FirebaseAuth.getInstance();// Initialize Firebase Auth

        mDatabase = FirebaseDatabase.getInstance().getReference();//Realtime Database
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        progressDialog = new ProgressDialog(this);

        btnSave.setOnClickListener(view -> textboxValidation());


        mUpload.setOnClickListener(view -> ActivityCompat.requestPermissions(
                accountSetup.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                CODE_GALLERY_REQUEST

        ));

        String userId = user.getCurrentUser().getUid();
        getuserInfo(userId);

    }

    private void getuserInfo(String uid) {
        final DatabaseReference table_user = FirebaseDatabase.getInstance().getReference("users/"+uid);


        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String FName = dataSnapshot.child("FName").getValue().toString();
                    String LName = dataSnapshot.child("LName").getValue().toString();

                    String Age = null,Address = null, userimg = null, Street = null,Barangay = null, City = null;
                    if (dataSnapshot.hasChild("LotNum")) {
                        Address = dataSnapshot.child("LotNum").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("Age")) {
                        Age = dataSnapshot.child("Age").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("UserImg")) {
                        userimg = dataSnapshot.child("UserImg").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("Street")) {
                        Street = dataSnapshot.child("Street").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("Barangay")) {
                        Barangay = dataSnapshot.child("Barangay").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("City")) {
                        City = dataSnapshot.child("City").getValue().toString();
                    }




                    if (Age != null){
                        txtAge.setText(Age);
                    }
                    if (Address != null){
                        txtAddress.setText(Address);
                    }
                    if (Street != null){
                        txtboxStreet.setText(Street);
                    }
                    if (Barangay != null){
                        txtboxBarangay.setText(Barangay);
                    }
                    if (City != null){
                        txtboxCity.setText(City);
                    }
                    if (userimg != null){
                        try {
                            Glide.with(accountSetup.this)
                                    .load(userimg)
                                    .into(profilePic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    txtFname.setText(FName);
                    txtLname.setText(LName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void updateUserAccount(String image) {
        String userId = user.getCurrentUser().getUid();
        String FName = txtFname.getText().toString();
        String LName = txtLname.getText().toString();
        //String email = txtEmail.getText().toString();
        int age = Integer.parseInt(txtAge.getText().toString());
        String gender = txtGender.getSelectedItem().toString();
        String lotNum = txtAddress.getText().toString();
        String street = txtboxStreet.getText().toString();
        String barangay = txtboxBarangay.getText().toString();
        String city = txtboxCity.getText().toString();

        //String establishment = txtEstablishment.getText().toString();

//        Post post = new Post(FName, LName, age, gender, address,image);
//        Map<String, Object> postValues = post.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/users/" + userId, postValues);

        //mDatabase.updateChildren(childUpdates);

        mDatabase.child("users").child(userId).child("FName").setValue(FName);
        mDatabase.child("users").child(userId).child("LName").setValue(LName);
        mDatabase.child("users").child(userId).child("Age").setValue(age);
        mDatabase.child("users").child(userId).child("Gender").setValue(gender);
        mDatabase.child("users").child(userId).child("LotNum").setValue(lotNum);
        mDatabase.child("users").child(userId).child("Street").setValue(street);
        mDatabase.child("users").child(userId).child("Barangay").setValue(barangay);
        mDatabase.child("users").child(userId).child("City").setValue(city);

        mDatabase.child("users").child(userId).child("UserImg").setValue(image);

        progressDialog.dismiss();
        Toast.makeText(this, "Your information has been saved.", Toast.LENGTH_SHORT).show();
    }
    private void uploadImage() {
        if (filePath != null) {

            progressDialog.setTitle("Uploading");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box

            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {

                                // Image uploaded successfully
                                // Dismiss dialog
                                ref.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                    //do something with downloadurl
                                    updateUserAccount(downloadUrl.toString());
                                });
//                                Toast.makeText(accountSetup.this,"Image Uploaded!!",Toast.LENGTH_SHORT).show();
                            })

                    .addOnFailureListener(e -> {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(accountSetup.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_GALLERY_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Image"),CODE_GALLERY_REQUEST);

            }
            else{
                Toast.makeText(this,"You don't have permission to gallery!", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri filepath = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                profilePic.setImageBitmap(bitmap);
                filePath = getImageUri(this.getApplicationContext(),bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void textboxValidation(){
        String FName = txtFname.getText().toString();
        String LName = txtLname.getText().toString();
        String email = txtEmail.getText().toString();
        int gender = txtGender.getSelectedItemPosition();
        String address = txtAddress.getText().toString();
        String establishment = txtEstablishment.getText().toString();
        if (FName.isEmpty()){
            txtFname.setError("Enter your first name");
            txtFname.requestFocus();
            return;
        }
        if (LName.length() <= 2){
            txtLname.setError("Enter your last name");
            txtLname.requestFocus();
            return;
        }
        if (txtAge.getText().toString().isEmpty()){
            txtAge.setError("Enter your age");
            txtAge.requestFocus();
            return;
        }
        if (gender == 0){
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if (address.isEmpty()){
            txtAddress.setError("Enter your address");
            txtAddress.requestFocus();
            return;
        }
        else if (bitmap == null){
            String img = "";
            if (txtGender.getSelectedItemPosition() == 1){
                img = "https://www.pngkit.com/png/detail/799-7998601_profile-placeholder-person-icon.png";
            }
            else {
                img = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQgC-t75ZGjQywFhCH_TDgulVGETYOh6uxtqOsANf7zeJI1pEprvjl1m84v6J6dD5jJ2U&usqp=CAU";
            }
            updateUserAccount(img);
        }
        else{
            uploadImage();
        }


    }

    public void btnBack(View view) {
        super.onBackPressed();
    }
}