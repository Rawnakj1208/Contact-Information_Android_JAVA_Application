package edu.bd.contactform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private EditText Name,Email,PhoneH,PhoneH1,PhoneO;
    String name,email,phoneH,phoneH1, phoneO;

    private Button CancelBtn,SaveBtn;

        String errorMsg = "";
        private final int GALLERY_REQ_CODE = 1000;
        ImageView imgGallery;
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        String enc_img;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            sharedPref = getSharedPreferences("App", Context.MODE_PRIVATE);
            editor = sharedPref.edit();

            Name = findViewById(R.id.Name);
            Email = findViewById(R.id.Email);
            PhoneH = findViewById(R.id.PhoneH);
            PhoneH1= findViewById(R.id.PhoneH1);
            PhoneO= findViewById(R.id.PhoneO);
            CancelBtn = findViewById(R.id.CancelBtn);
            SaveBtn = findViewById(R.id.SaveBtn);
            imgGallery= findViewById(R.id.img);
            Button btnGallery = findViewById(R.id.galleryBtn);

            btnGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iGallery = new Intent(Intent.ACTION_PICK);
                    iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(iGallery,GALLERY_REQ_CODE);
                }
            });



            SaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = Name.getText().toString();
                    email = Email.getText().toString();
                    phoneH = PhoneH.getText().toString();
                    phoneH1 = PhoneH1.getText().toString();
                    phoneO = PhoneO.getText().toString();


                    if (Name.length() < 3) {
                        errorMsg += "Invalid Name";
                    }

                    if(Email.length()> 0)  {
                        emailValidator(Email);
                    }
                    int s = 880;
                    if(phoneH.length() == 0 || Integer.parseInt(phoneH)!=s) {
                        errorMsg+="Invalid country code";

                    }
                    if(phoneH1.length() == 0 || phoneH1.length() < 10){
                        errorMsg+= "Invalid Phone Home number";

                    }

                    if(PhoneO.length()> 0){
                        if(PhoneO.length() < 4 ) {

                            errorMsg += "Invalid Office phone";
                        }
                    }
                    if(errorMsg.length() ==0) {
                        showDialog("Do you want to save event Information?","Info","Yes","No");
                    }
                    else{
                        showDialog(errorMsg,"Error","Ok","Back");

                    }



                }
            });
            CancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();

                }
            });
        }

        private void showDialog(String message, String title,String btn1,String btn2)
        {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage(message);
            builder.setTitle(title);
            builder.setCancelable(false)
                    .setPositiveButton(btn1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Util.getInstance().deleteByKey(MainActivity.this,key);
                            dialog.cancel();
                            if(btn1 == "Yes"){
                                editor.putString("NKey", name);
                                editor.putString("EKey", email);
                                editor.putString("PhoneHKey", phoneH);
                                editor.putString("PhoneH1Key", phoneH1);
                                editor.putString("PhoneOKey", phoneO);
                                editor.putString("ImageKey", enc_img);

                                editor.commit();
                                Toast.makeText(MainActivity.this, "Data successfully saved", Toast.LENGTH_SHORT).show();
                            }

                            // loadData();
                            //adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(btn2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode==RESULT_OK){
                if(requestCode==GALLERY_REQ_CODE){
                    imgGallery.setImageURI(data.getData());
                    Uri uploadAbleImg = data.getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(uploadAbleImg);
                        Bitmap image = BitmapFactory.decodeStream(is);
                        enc_img = encodedImage(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private String encodedImage(Bitmap image) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,bos);
            byte[] byt = bos.toByteArray();
            String viewimg = Base64.encodeToString(byt,Base64.DEFAULT);
            return viewimg;
        }

        public void emailValidator(EditText eMail) {

            String emailToText = eMail.getText().toString();

            if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
                Toast.makeText(this, "Email Verified !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Enter valid Email address !", Toast.LENGTH_SHORT).show();
                errorMsg+= "Invalid email";
            }
        }

    }








