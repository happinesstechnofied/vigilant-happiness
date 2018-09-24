package adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.apt360.vendor.R;
import com.apt360.vendor.ViewServicesActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import model.ServicesData;
import util.AppConstants;
import util.ImageLoadTask;

/**
 * Created by Piyush on 11/1/2017.
 */

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.MyViewHolder> {

    private static final int READ_STORAGE_CODE = 1001;
    private List<ServicesData> notifyList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        public TextView txtVendorStatus, txtRating, txtCategoryTitle, txtCateTitle, txtSubCateTitle, txtVendorName, txtMRP, txtSellingPrice, txtSavePrice;
        public TextView txtVendorNumber;
        public SimpleDraweeView vendorBanner;
        CircularImageView imgCateImage, imgSubCateImage;
        ImageView imgContact;
        RelativeLayout layoutVendorStatus;
        public TextView txtAverageRate;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            txtCategoryTitle = (TextView) view.findViewById(R.id.txtCategoryTitle);
            vendorBanner = (SimpleDraweeView) view.findViewById(R.id.vendorBanner);
            txtVendorStatus = (TextView) view.findViewById(R.id.txtVendorStatus);
            txtRating = (TextView) view.findViewById(R.id.txtRating);
            txtCategoryTitle = (TextView) view.findViewById(R.id.txtCategoryTitle);
            txtCateTitle = (TextView) view.findViewById(R.id.txtCateTitle);
            txtSubCateTitle = (TextView) view.findViewById(R.id.txtSubCateTitle);
            txtVendorNumber = (TextView) view.findViewById(R.id.txtVendorNumber);
            txtVendorName = (TextView) view.findViewById(R.id.txtVendorName);
            txtMRP = (TextView) view.findViewById(R.id.txtMRP);
            txtSellingPrice = (TextView) view.findViewById(R.id.txtSellingPrice);
            txtSavePrice = (TextView) view.findViewById(R.id.txtSavePrice);
            layoutVendorStatus = (RelativeLayout) view.findViewById(R.id.layoutVendorStatus);
            imgCateImage = (CircularImageView) view.findViewById(R.id.imgCateImage);
            imgSubCateImage = (CircularImageView) view.findViewById(R.id.imgSubCateImage);
            imgContact = (ImageView) view.findViewById(R.id.imgContact);
            txtAverageRate = (TextView) view.findViewById(R.id.txtAverageRate);
        }
    }

    public HomeFragmentAdapter(Context context,List<ServicesData> moviesList) {
        this.notifyList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_home_fragment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            ServicesData nData = notifyList.get(position);
            holder.txtCategoryTitle.setText(nData.getTitle());
            if (nData.getImage() == null || nData.getImage().equals("")) {
                holder.vendorBanner.setImageResource(R.drawable.ic_placeholder);
            } else {
                Uri uri = Uri.parse(nData.getImage());
                holder.vendorBanner.setImageURI(uri);
            }

            if (nData.getServiceStatus().equalsIgnoreCase("publish")) {
                holder.layoutVendorStatus.setVisibility(View.INVISIBLE);
            } else {
                holder.txtVendorStatus.setText(nData.getServiceStatus());
            }
            holder.txtRating.setText(nData.getTotalRattings());
            holder.txtAverageRate.setText(String.valueOf(nData.getTotalReviews()));
            holder.txtCateTitle.setText(nData.getParentCatName());
            holder.txtSubCateTitle.setText(nData.getSubCatName());
            holder.txtVendorNumber.setText(nData.getMobileNo());
            holder.txtVendorName.setText(nData.getContactName());
            holder.txtMRP.setText("₹" + nData.getMaximumRetailPrice());
            holder.txtSellingPrice.setText("₹" + nData.getSalePrice());
            int saveAmount = Integer.valueOf(nData.getMaximumRetailPrice()) - Integer.valueOf(nData.getSalePrice());
            holder.txtSavePrice.setText("You Save : ₹" + String.valueOf(saveAmount));

            if (!nData.getParentImageSmall().equalsIgnoreCase("")) {
                URL url = new URL(nData.getParentImageSmall());
                new ImageLoadTask(url, holder.imgCateImage).execute();
            }

            if (!nData.getSubImageSmall().equalsIgnoreCase("")) {
                URL url = new URL(nData.getParentImageSmall());
                //imgUserImage.setImageBitmap(getBitmapFromURL(url));
                new ImageLoadTask(url, holder.imgSubCateImage).execute();
            }

            holder.imgContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppConstants.isPermissionGranted(Manifest.permission.CALL_PHONE,context)) {
                        String callNumber = holder.txtVendorNumber.getText().toString();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + callNumber));
                        context.startActivity(callIntent);
                    } else {

                    }
                }
            });

            holder.txtVendorNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppConstants.isPermissionGranted(Manifest.permission.CALL_PHONE,context)) {
                        String callNumber = holder.txtVendorNumber.getText().toString();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + callNumber));
                        context.startActivity(callIntent);
                    } else {
                        requestPermission(Manifest.permission.CALL_PHONE);
                    }
                }
            });


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ViewServicesActivity.class);
                    intent.putExtra("position",position);
                    context.startActivity(intent);
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notifyList.size();
    }
    //Requesting permission
    private  void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
        }
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, READ_STORAGE_CODE);
    }
}

