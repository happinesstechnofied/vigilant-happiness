package adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.RequestQueue;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.subscriber.R;

import java.util.List;

import imagezoom.TouchImageView;
import model.GalleryImage;
import model.ServicesData;


/**
 * Created by Piyush on 11/13/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<GalleryImage> saveList;
    RequestQueue requestQueue;
    Context context;
    SharedPreferences preferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView saveItemsIcon;

        public MyViewHolder(View view) {
            super(view);
            saveItemsIcon = (SimpleDraweeView) view.findViewById(R.id.saveItemsIcon);
        }
    }
    public GalleryAdapter(Context context, List<GalleryImage> sList) {
        this.saveList = sList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_gallery, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GalleryImage nData = saveList.get(position);
        final Uri uri = Uri.parse(nData.getImageSmall());
        holder.saveItemsIcon.setImageURI(uri);
        holder.saveItemsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProduct(uri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
    private void showProduct(Uri i) {
        final Dialog zDialog = new Dialog(context);
        zDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        zDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        zDialog.setContentView(R.layout.zoom_product_dialog);
        AppCompatButton btnClose = (AppCompatButton)zDialog.findViewById(R.id.btnClose);
        TouchImageView imageView = (TouchImageView)zDialog.findViewById(R.id.imageView);
        Picasso.with(context).load(i).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(imageView);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zDialog.dismiss();
            }
        });
        zDialog.show();
    }
}

