package com.sonal.meettheteam.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.sonal.meettheteam.Base.BaseActivity;
import com.sonal.meettheteam.Commons.Constant;
import com.sonal.meettheteam.Model.PeopleModel;
import com.sonal.meettheteam.Preference.PrefConst;
import com.sonal.meettheteam.Preference.Preference;
import com.sonal.meettheteam.R;
import com.sonal.meettheteam.Utils.CustomBitmapUtils;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    ImageView imvAvatar, imvCamera, imvBack;
    EditText edtFirstName, edtLastName, edtTitle, edtPhone, edtBio;
    Button btnAdd;
    TextView txvBirthday;

    boolean isAdding = true;
    PeopleModel selectedPeople;

    Calendar calendar;
    int thisYear = 0, thisMonth = 0, thisDay = 0;
    int mYear = 0, mMonth = 0, mDay = 0;

    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;
    private static final String[] PERMISSIONS = {
        Manifest.permission.MEDIA_CONTENT_CONTROL,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private Uri _imageCaptureUri;
    String _photoPath = "";

    ArrayList<PeopleModel> peopleList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        selectedPeople = (PeopleModel) intent.getSerializableExtra(Constant.PEOPLE);
        if (selectedPeople != null) {
            isAdding = false;
        }

        peopleList = Preference.getInstance().getPeopleList(this, PrefConst.PREF_PARAM_PEOPLE_LIST);

        Constant.PHOTO_STRING = "user_logo";

        loadLayout();
        getCurrentDate();
        checkPermissions();
    }

    private void loadLayout() {

        edtFirstName = (EditText) findViewById(R.id.edt_f_name);
        if (!isAdding) {
            edtFirstName.setText(selectedPeople.getFirstName());
        }

        edtLastName = (EditText) findViewById(R.id.edt_l_name);
        if (!isAdding) {
            edtLastName.setText(selectedPeople.getLastName());
        }

        edtTitle = (EditText) findViewById(R.id.edt_title);
        if (!isAdding) {
            edtTitle.setText(selectedPeople.getTitle());
        }

        edtPhone = (EditText) findViewById(R.id.edt_phone);
        if (!isAdding) {
            edtPhone.setText(selectedPeople.getPhone());
        }

        edtBio = (EditText) findViewById(R.id.edt_bio);
        if (!isAdding) {
            edtBio.setText(selectedPeople.getBio());
        }

        txvBirthday = (TextView) findViewById(R.id.txv_birthday);
        if (!isAdding) {
            setBirthday(selectedPeople.getBirthday());
        }
        txvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalenderView();
            }
        });

        btnAdd = (Button) findViewById(R.id.btn_add);
        if (isAdding) {
            btnAdd.setText("ADD");
        } else {
            btnAdd.setText("SAVE");
        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidUpdate()) {
                    savePeople();
                }
            }
        });

        imvBack = (ImageView) findViewById(R.id.imv_back);
        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imvAvatar = (ImageView) findViewById(R.id.imv_avatar);
        if (!isAdding) {
            if (selectedPeople.getImageUrl().substring(0, 4).equals(Constant.WEB_URL_HEAD)) {
                Picasso.with(this).load(selectedPeople.getImageUrl())
                    .placeholder(R.mipmap.p0).error(R.mipmap.p0).into(imvAvatar);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(selectedPeople.getImageUrl());
                imvAvatar.setImageBitmap(bitmap);
            }
        }

        imvCamera = (ImageView) findViewById(R.id.imv_camera);
        imvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

    }

    private void savePeople() {

        if (isAdding) {
            PeopleModel people = new PeopleModel();

            people.setId(String.valueOf(peopleList.size()));
            people.setFirstName(edtFirstName.getText().toString());
            people.setLastName(edtLastName.getText().toString());
            people.setTitle(edtTitle.getText().toString());
            people.setPhone(edtPhone.getText().toString());
            people.setBio(edtBio.getText().toString());
            people.setImageUrl(_photoPath);

            mMonth++;
            String d = mDay > 9 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            String m = mMonth > 9 ? String.valueOf(mMonth) : "0" + String.valueOf(mMonth);
            String birthday = d + "/" + m + "/" + String.valueOf(mYear);

            people.setBirthday(birthday);

            peopleList.add(people);

            Constant.P_LIST_ACTIVITY.putPeopleListToLocalStorage(peopleList);
        } else {
            int itemIndex = Integer.valueOf(selectedPeople.getId());

            selectedPeople.setFirstName(edtFirstName.getText().toString());
            selectedPeople.setLastName(edtLastName.getText().toString());
            selectedPeople.setTitle(edtTitle.getText().toString());
            selectedPeople.setPhone(edtPhone.getText().toString());
            selectedPeople.setBio(edtBio.getText().toString());
            if (_photoPath.length() > 0) {
                selectedPeople.setImageUrl(_photoPath);
            }

            mMonth++;
            String d = mDay > 9 ? String.valueOf(mDay) : "0" + String.valueOf(mDay);
            String m = mMonth > 9 ? String.valueOf(mMonth) : "0" + String.valueOf(mMonth);
            String birthday = d + "/" + m + "/" + String.valueOf(mYear);

            selectedPeople.setBirthday(birthday);

            peopleList.set(itemIndex, selectedPeople);

            Constant.P_LIST_ACTIVITY.putPeopleListToLocalStorage(peopleList);
        }

        finish();
    }

    private void getCurrentDate() {
        calendar = Calendar.getInstance();

        thisYear = calendar.get(Calendar.YEAR);
        thisMonth = calendar.get(Calendar.MONTH);
        thisDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void showCalenderView() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(_context, android.app.AlertDialog.THEME_HOLO_LIGHT, this, 1980, 0, 1);

        calendar.set(thisYear, thisMonth, thisDay);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        calendar.set(1950, 0, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setBirthday(String origin) {
        String[] MONTH_LIST = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        String[] ORIGIN_STRINGS = origin.split("/");
        int m = Integer.valueOf(ORIGIN_STRINGS[1]) - 1;

        txvBirthday.setText(ORIGIN_STRINGS[0] + ", " + MONTH_LIST[m] + ", " + ORIGIN_STRINGS[2]);
    }

    @Override
    public void onDateSet(DatePicker view, int y, int m, int d) {
        String[] MONTH_LIST = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        String day = d > 9 ? String.valueOf(d) : "0" + String.valueOf(d);

        txvBirthday.setText(day + ", " + MONTH_LIST[m] + ", " + String.valueOf(y));
        mYear = y;
        mMonth = m;
        mDay = d;
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    private boolean isValidUpdate() {

        if (edtFirstName.getText().toString().length() == 0) {
            showToast("Input first name");
            focusObj(edtFirstName);
            return false;
        } else if (edtLastName.getText().toString().length() == 0) {
            showToast("Input last name");
            focusObj(edtLastName);
            return false;
        } else if (edtTitle.getText().toString().length() == 0) {
            showToast("Input title");
            focusObj(edtTitle);
            return false;
        } else if (txvBirthday.getText().toString().equals(getString(R.string.birthday))) {
            showToast("Set birthday");
            return false;
        } else if (edtBio.getText().toString().length() == 0) {
            showToast("Input BIO");
            focusObj(edtBio);
            return false;
        } else if (edtPhone.getText().toString().length() == 0) {
            showToast("Input phone number");
            focusObj(edtPhone);
            return false;
        } else if (isAdding && _photoPath.length() == 0) {
            showToast("Set the avatar image");
            return false;
        }

        return true;
    }

    private void focusObj(EditText editText) {
        editText.setText(editText.getText());
        editText.requestFocus();
    }

    //=========================== PermissionCheck=============================
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    public void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //====================== Add Photo=================================
    public void selectPhoto() {
        final String[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    ActivityCompat.requestPermissions(DetailsActivity.this, PERMISSIONS, 101);
                    doTakePhoto();
                } else {
                    doTakeGallery();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void doTakePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String picturePath = CustomBitmapUtils.getTempFolderPath() + "photo_temp.jpg";

        if(Build.VERSION.SDK_INT > 23){
            _imageCaptureUri = FileProvider.getUriForFile(_context, "com.sonal.meettheteam.provider", new File(picturePath));
        } else {
            _imageCaptureUri = Uri.fromFile(new File(picturePath));
        }

        _photoPath=picturePath;

        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
        startActivityForResult(intent, Constant.PICK_FROM_CAMERA);

    }

    private void doTakeGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constant.PICK_FROM_ALBUM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch (requestCode){
            case Crop.REQUEST_CROP: {
                if (resultCode == RESULT_OK){
                    try {
                        File outFile = CustomBitmapUtils.getOutputMediaFile(this, "temp.png");

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(outFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
                        in.close();

                        ExifInterface ei = new ExifInterface(outFile.getAbsolutePath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Bitmap returnedBitmap = bitmap;

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                returnedBitmap = CustomBitmapUtils.rotateImage(bitmap, 90);
                                bitmap.recycle();
                                bitmap = null;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                returnedBitmap = CustomBitmapUtils.rotateImage(bitmap, 180);
                                bitmap.recycle();
                                bitmap = null;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                returnedBitmap = CustomBitmapUtils.rotateImage(bitmap, 270);
                                bitmap.recycle();
                                bitmap = null;
                                break;
                            default:
                                break;
                        }

                        Bitmap w_bmpSizeLimited = Bitmap.createScaledBitmap(returnedBitmap, 900, 900, true);
                        File newFile = null;
                        if (isAdding){
                            newFile = CustomBitmapUtils.getOutputMediaFile(this, "avatar_"+String.valueOf(peopleList.size()) + ".png");
                        } else {
                            newFile = CustomBitmapUtils.getOutputMediaFile(this, "avatar_"+String.valueOf(selectedPeople.getId()) + ".png");
                        }
                        CustomBitmapUtils.saveOutput(newFile, w_bmpSizeLimited);
                        _photoPath = newFile.getAbsolutePath();
                        imvAvatar.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constant.PICK_FROM_ALBUM:
                if (resultCode == RESULT_OK){
                    _imageCaptureUri = data.getData();
                    beginCrop(_imageCaptureUri);
                }
                break;
            case Constant.PICK_FROM_CAMERA: {

                if (resultCode == RESULT_OK){
                    try {
                        String filename="photo_temp.jpg";
                        Bitmap bitmap = CustomBitmapUtils.loadOrientationAdjustedBitmap(_photoPath);

                        String w_strFilePath = "";
                        String w_strLimitedImageFilePath = CustomBitmapUtils.getUploadImageFilePath(bitmap, filename);
                        if (w_strLimitedImageFilePath != null) {
                            w_strFilePath = w_strLimitedImageFilePath;
                        }

                        _photoPath = w_strFilePath;
                        _imageCaptureUri = Uri.fromFile(new File(_photoPath));
                        _imageCaptureUri = FileProvider.getUriForFile(this, "com.sonal.meettheteam.provider", new File(_photoPath));

                        beginCrop(_imageCaptureUri);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
                break;
            }

        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(CustomBitmapUtils.getOutputMediaFile(this, "temp.png"));
        Crop.of(source, destination).asSquare().start(this);
    }
}
