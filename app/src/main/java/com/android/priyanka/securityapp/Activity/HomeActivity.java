package com.android.priyanka.securityapp.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.priyanka.securityapp.BroadcastReceiver.PhoneReceiver;
import com.android.priyanka.securityapp.Database.DatabaseHelper;
import com.android.priyanka.securityapp.R;
import com.android.priyanka.securityapp.Service.CallDetectService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {


    @BindView(R.id.person_image)
    ImageView personImage;
    @BindView(R.id.add_photo)
    Button addPhoto;
    @BindView(R.id.edNumber)
    EditText edNumber;
    @BindView(R.id.call)
    ImageButton call;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.name)
    EditText edtname;
    @BindView(R.id.gender)
    RadioGroup gender;
    @BindView(R.id.other)
    RadioButton other;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;

    private String persongender;

    private final int requestCode = 20;

    DatabaseHelper databaseHelper;
    String image_str, phnum, personname;
    Bitmap bitmap;
    Locale locale;
    String language = "en";
    String lang;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = preferences.getString("language", "en");
        locale = new Locale(language);
        saveLocale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setTitle(getString(R.string.app_name));



        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        language = preferences.getString("language", "en");
        locale = new Locale(language);
        saveLocale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/




        databaseHelper = new DatabaseHelper(this);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);

                    if (btn.getId() == checkedId) {

                        persongender = btn.getText().toString();

                    }

                }

                Log.e("Gender", persongender);
                System.out.println("gender::" + persongender);
            }
        });
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    @OnClick({R.id.add_photo, R.id.call, R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_photo:
                requestStoragePermission();
                break;

            case R.id.call:
                requestCallPermission();
                break;

            case R.id.save:
                saveData();
                break;

        }
    }


    private void saveData() {

        personname = edtname.getText().toString().trim();
        phnum = edNumber.getText().toString();

        if (!checkValidation(personname, persongender, phnum, bitmap)) {
            System.out.println("if....." + persongender);


        } else {
            System.out.println("else....");

            //current date
            String date_n = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());

            //Display current time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm a");
            String cur_time = df.format(c.getTime());

            System.out.println("personname:  " + personname);
            System.out.println("persongender:  " + persongender);
            System.out.println("phnum:  " + phnum);
            System.out.println("date_n:  " + date_n);
            System.out.println("cur_time:  " + cur_time);

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();


            databaseHelper.insertStudent(personname, persongender, phnum, date_n, cur_time);
            databaseHelper.close();


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HH.mm.ss").format(new Date());

            //current date
            String date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());

            File f = new File(Environment.getExternalStorageDirectory() + "/BldgImages" + "_" + date);
            if (f.exists() && f.isDirectory()) {
                System.out.println("directory exists");
                FileOutputStream outStream = null;
                // String fileName = String.format("%d.jpg", System.currentTimeMillis());
                String fileName = personname + "_" + phnum + "_" + timeStamp + ".jpg";

                File outFile = new File(f, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                refreshGallery(outFile);

            } else {
                System.out.println("directory doesnt exists");

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/BldgImages" + "_" + date);
                dir.mkdirs();
                String fileName = personname + "_" + phnum + "_" + timeStamp + ".jpg";

              //  String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                refreshGallery(outFile);

            }

            finish();
            startActivity(getIntent());
        }
            /*edtname.getText().clear();
            edNumber.getText().clear();
            gender.clearCheck();
            personImage.setImageResource(R.drawable.ic_account_circle_black);
            addPhoto.setVisibility(View.VISIBLE);*/



    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }


    private boolean checkValidation(String personname, String persongender, String phnum, Bitmap bitmap) {

        if (personname.isEmpty() || persongender == null || phnum.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Please enter all the data", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            snackbar.show();
            return false;
        } else if (phnum.length() != 10) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Please enter valid phone number", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            snackbar.show();
            return false;

        } else if (bitmap == null) {
           // Toast.makeText(getApplicationContext(), "Please click image", Toast.LENGTH_LONG).show();
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Please click image", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            snackbar.show();
            return false;
        }
        return true;

    }


    public void requestCallPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CALL_PHONE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                callPerson();
                // callPerson1();
                // Toast.makeText(HomeActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                // check for permanent denial of permission
                if (response.isPermanentlyDenied()) {
                    showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();

    }

    public void requestStoragePermission() {

        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            openCamera();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {

            }
        }).onSameThread().check();

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }

    private void callPerson() {
        phnum = edNumber.getText().toString();

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phnum));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (this.requestCode == requestCode && resultCode == RESULT_OK) {

            bitmap = (Bitmap) data.getExtras().get("data");
            personImage.setVisibility(View.VISIBLE);
            personImage.setImageBitmap(bitmap);
            addPhoto.setVisibility(View.INVISIBLE);

            personImage.setDrawingCacheEnabled(true);
            personImage.buildDrawingCache();
            bitmap = personImage.getDrawingCache();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();


            System.out.println("array" + byteArray);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.changelanguage:

                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.radio_btn_dialog);
                dialog.setTitle(getString(R.string.choose_language));
                dialog.setCancelable(true);
                RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                final RadioButton en = (RadioButton) dialog.findViewById(R.id.en);
                final RadioButton hi = (RadioButton) dialog.findViewById(R.id.hi);
                if (language.equals("en")) {
                    en.setChecked(true);
                    lang = "en";
                } else if (language.equals("hi")) {
                    hi.setChecked(true);
                    lang = "hi";
                }
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if (en.isChecked()) {
                            lang = "en";
                        } else if (hi.isChecked()) {
                            lang = "hi";
                        }
                    }
                });

                Button done = (Button) dialog.findViewById(R.id.done);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setLanguage(lang);
                        dialog.dismiss();
                    }
                });
                dialog.show();

                break;





            default:
                break;
        }

       return super.onOptionsItemSelected(item);
    }

    public void setLanguage(String languageToLoad) {
        locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageToLoad);
        editor.commit();
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }
}
