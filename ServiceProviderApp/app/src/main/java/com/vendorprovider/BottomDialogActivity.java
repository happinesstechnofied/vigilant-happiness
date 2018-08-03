package com.vendorprovider;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class BottomDialogActivity extends BottomSheetDialogFragment {

    private int STORAGE_PERMISSION_CODE = 23;
    int SELECT_FILE = 1;
    private static final int CAMERA_REQUEST = 1888;
    ImageView btnGallery, btnCamera;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_layout, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        btnGallery = (ImageView) v.findViewById(R.id.btnGallery);
        btnCamera = (ImageView) v.findViewById(R.id.btnCamera);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //      requestStoragePermission();
                int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    mIntent.setType("image/*");
                    startActivityForResult(mIntent, SELECT_FILE);
                } else {
                    Toast.makeText(getContext(), "Storage Permission Not Granted.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(getContext(), "Camera Permission Not Granted.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_FILE && data != null) {
            Uri selectImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(selectImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String mPicUrl = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bmp;
            bmp = BitmapFactory.decodeFile(mPicUrl);
            RegisterActivity.userPhotoPath = bmp;
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 100, 100, true);
            Bitmap conv_bm = getMyCircleBitmap(resized);
            if (preferences.getString("sp_from", "").equalsIgnoreCase("profile")) {
                ProfileFragment.imgUserImage.setImageBitmap(conv_bm);
                ProfileFragment.isImageUpload = true;
                ProfileFragment.checkPhoto++;
            } else {
                RegisterActivity.imgUserImage.setImageBitmap(conv_bm);
                RegisterActivity.isImageUpload = true;
                RegisterActivity.checkPhoto++;
                RegisterActivity.txtUploadPhoto.setText("Profile picture uploaded Successfully");
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap mphoto = (Bitmap) data.getExtras().get("data");

            if (preferences.getString("sp_from", "").equalsIgnoreCase("profile")) {
                ProfileFragment.imgUserImage.setImageBitmap(mphoto);
                ProfileFragment.isImageUpload = true;
                ProfileFragment.checkPhoto++;
            } else {
                RegisterActivity.imgUserImage.setImageBitmap(mphoto);
                RegisterActivity.isImageUpload = true;
                RegisterActivity.checkPhoto++;
                RegisterActivity.txtUploadPhoto.setText("Profile picture uploaded Successfully");
            }

        }
    }

    /*user upload picture*/
    public static Bitmap getMyCircleBitmap(Bitmap bitmap) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
    }

}