package fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.photopicker.activity.PickImageActivity;
import com.vendorprovider.R;
import com.vendorprovider.ServiceCreationActivity;
import com.vendorprovider.ViewServicesActivity;

import java.util.ArrayList;

import adapter.DisplayGalleryAdapter;
import adapter.GalleryAdapter;
import model.GalleryImage;

import static android.app.Activity.RESULT_OK;

public class ServiceFragment extends Fragment {
    public static ServiceFragment newInstance() {
        ServiceFragment fragment = new ServiceFragment();
        return fragment;
    }

    private static final int READ_STORAGE_CODE = 1001;
    private static final int WRITE_STORAGE_CODE = 1002;
    private ArrayList<String> pathList = new ArrayList<>();
    Button btnNext;
    Button btnBack;
    ImageView imgGalleryAd;
    TextView txtImageCount;
    TextInputEditText txtServiceTitle, txtTagLine;
    String serviceTitle, serviceTagLine, serviceImages;
    SharedPreferences preferences;
    String checkEditMode;
    int position;
    private GridLayoutManager lLayout;
    RecyclerView galleryListView;
    ArrayList<GalleryImage> galleryImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.services_details_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        checkEditMode = preferences.getString("EditService", "");
        position = preferences.getInt("Position",0);

        btnNext = (Button) view.findViewById(R.id.btnNext);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        txtServiceTitle = (TextInputEditText) view.findViewById(R.id.txtServiceTitle);
        txtTagLine = (TextInputEditText) view.findViewById(R.id.txtTagLine);
        txtImageCount = (TextView) view.findViewById(R.id.txtImageCount);
        imgGalleryAd = (ImageView) view.findViewById(R.id.imgGalleryAd);
        galleryListView = (RecyclerView) view.findViewById(R.id.galleryRecyclerView);


        if (checkEditMode.equals("Edit")) {
            txtServiceTitle.setText(ViewServicesActivity.arrayList.get(position).getTitle());
            txtTagLine.setText(ViewServicesActivity.arrayList.get(position).getTagLine());
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceTitle = txtServiceTitle.getText().toString();
                serviceTagLine = txtTagLine.getText().toString();
                serviceImages = txtImageCount.getText().toString();
                if (TextUtils.isEmpty(serviceTitle)) {
                    txtServiceTitle.setError("Please enter service title!");
                    txtServiceTitle.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(serviceTagLine)) {
                    txtTagLine.setError("Please enter tag line!");
                    txtTagLine.requestFocus();
                    return;
                }else if(serviceImages.equals("0")){
                    Toast.makeText(getContext(),"Please select images!",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("service_title", serviceTitle);
                    editor.putString("tag_line", serviceTagLine);
                    //editor.putString("images", pathList.toString());
                    editor.commit();
                    ServiceCreationActivity.mViewPager.setCurrentItem(2, true);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceCreationActivity.mViewPager.setCurrentItem(0, true);
            }
        });
        imgGalleryAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePickerIntent();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_more).setVisible(true);
        menu.findItem(R.id.action_logout).setVisible(false);
    }
    private void openImagePickerIntent() {
        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent mIntent = new Intent(getContext(), PickImageActivity.class);
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 50);
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1);
            startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE);
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE);
        }
    }
    private boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(getContext(), permission);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }
    //Requesting permission
    private void requestPermission(String permission, int code) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, code);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePickerIntent();
            } else {
            }
        } else if (requestCode == WRITE_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (resultCode == -1 && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            this.pathList = intent.getExtras().getStringArrayList(PickImageActivity.KEY_DATA_RESULT);
            if (this.pathList != null && !this.pathList.isEmpty()) {
                StringBuilder sb=new StringBuilder("");
                for(int i=0;i<pathList.size();i++) {
                    sb.append("Photo"+(i+1)+":"+pathList.get(i));
                    sb.append("\n");
                    GalleryImage image=new GalleryImage();
                    image.setImageMedium(pathList.get(i));
                    galleryImages.add(image);
                }
                txtImageCount.setVisibility(View.VISIBLE);
                txtImageCount.setText(""+pathList.size());

            }
        }
        if (galleryImages.size() == 0) {
            galleryListView.setVisibility(View.GONE);
        }else{
            galleryListView.setVisibility(View.VISIBLE);
            DisplayGalleryAdapter rcAdapter = new DisplayGalleryAdapter(getContext(), galleryImages);
            lLayout = new GridLayoutManager(getContext(), 4);
            galleryListView.setHasFixedSize(true);
            galleryListView.setLayoutManager(lLayout);
            galleryListView.setAdapter(rcAdapter);
        }
    }
}