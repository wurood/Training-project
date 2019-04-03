package com.example.wurood.myapplication;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewActivity extends AppCompatActivity {
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private AwesomeValidation awesomeValidation;
    private int GALLERY = 1, CAMERA = 2;
    private String PATTERN, name_pat;
    private Pattern pattern;
    private Matcher matcher;
    private Pattern name_test;
    boolean flag;
    String tmp;
    DBHelper mDBHelper;
    String  nameEdit, phoneEdit, emailEdit;
    @BindView(R.id.name_text)
    EditText nameText;
    @BindView(R.id.email_text)
    EditText emailText;
    @BindView(R.id.phone_text)
    EditText phoneText;
    @BindView(R.id.img_set)
    ImageView profileImage;
    @BindView(R.id.btn_lod)
    ImageView loadImage;
    @BindView(R.id.btnUpdate)
    Button update;
    Bitmap bmp;
    String image,name,email,number;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @OnClick({R.id.btn_lod, R.id.btnUpdate})
    public void onClick(View v) {

        nameEdit = nameText.getText().toString();
        phoneEdit = phoneText.getText().toString();
        emailEdit = emailText.getText().toString();
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        byte[] photoNew = getBytes(bitmap);
        if (v == loadImage) {
            showPictureDialog();
        } else if (v == update) {
            if (awesomeValidation.validate()) {
                mDBHelper.updateContact(tmp, nameEdit, emailEdit, phoneEdit, photoNew);
                hideSoftKeyboard(this, v);
                clearData();
                Toast.makeText(this, "Contact Updated Successfully ", Toast.LENGTH_LONG).show();

            } else
                Toast.makeText(this, "Pleas Enter Valid Information", Toast.LENGTH_LONG).show();
        }

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.edit));
        name=getIntent().getStringExtra("contactName");
        email=getIntent().getStringExtra("contactEmail");
        number=getIntent().getStringExtra("contactNumber");
        byte[] array=getIntent().getByteArrayExtra("contactPhoto");
        bmp= BitmapFactory.decodeByteArray(array,0,array.length);
        profileImage.setImageBitmap(bmp);
        nameText.setText(name);
        emailText.setText(email);
        phoneText.setText(number);

        mDBHelper = new DBHelper(getApplicationContext());

        PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        name_pat = "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
        pattern = Pattern.compile(PATTERN);
        name_test = name_test.compile(name_pat);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        tmp = String.valueOf(nameText.getText());
        phoneText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            update.performClick();


                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    private void onValidate() {
        awesomeValidation.addValidation(this, R.id.name_text, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.invalid_name);
        String est = emailText.getText().toString();
        if (!est.matches("")) {
            awesomeValidation.addValidation(this, R.id.email_text, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        }
        awesomeValidation.addValidation(this, R.id.phone_text, Patterns.PHONE, R.string.invalid_phone);
        nameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (nameText.getText().length() == 0) {
                    nameText.setError("please enter name ");
                } else if (!isValidName(nameText.getText())) {
                    nameText.setError("please enter a valid Name ");
                } else {
                    nameText.setError(null);
                }
            }
        });

        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!isValidMail(String.valueOf(emailText.getText()))) {
                    emailText.setError("please enter a valid email ");
                } else {
                    emailText.setError(null);
                }
            }
        });

        phoneText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (phoneText.getText().length() < 10) {
                    phoneText.setError("number length must not be smaller than 10 ");
                } else if (!isValidMobile(String.valueOf(phoneText.getText()))) {
                    phoneText.setError("please enter a valid number ");
                } else {
                    phoneText.setError(null);
                }
            }
        });
    }

    public void setDatabase(DBHelper d) {
        mDBHelper = d;
    }
    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {

        return Patterns.PHONE.matcher(phone).matches();
    }
    public boolean isValidName(final Editable hex) {
        matcher = name_test.matcher(hex);
        return matcher.matches();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(ViewActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(ViewActivity.this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(ViewActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    profileImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ViewActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(ViewActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void clearData() {
        flag = true;
        nameText.getText().clear();
        phoneText.getText().clear();
        emailText.getText().clear();
        profileImage.setImageBitmap(null);
        profileImage.setImageResource(R.mipmap.ic_user);


    }
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();  // optional depending on your needs
        // startActivity(new Intent(this, MainActivity.class));    }
    }
}