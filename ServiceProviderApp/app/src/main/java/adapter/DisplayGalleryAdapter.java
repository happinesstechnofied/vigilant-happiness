package adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import com.photopicker.model.ImageModel;
import com.squareup.picasso.Picasso;
import com.apt360.vendor.R;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import fragment.ServiceFragment;
import imagezoom.TouchImageView;
import model.GalleryImage;

/**
 * Created by rajgandhi on 26/07/18.
 */

public class DisplayGalleryAdapter extends RecyclerView.Adapter<DisplayGalleryAdapter.MyViewHolder> {

    private List<GalleryImage> saveList;
    public static ArrayList<String> removeItemPic = new ArrayList<>();
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageItem;
        public ImageView btnDelete;

        public MyViewHolder(View view) {
            super(view);
            imageItem = (ImageView) view.findViewById(R.id.imageItem);
            btnDelete = (ImageView) view.findViewById(R.id.btnDelete);
        }
    }
    public DisplayGalleryAdapter(Context context, List<GalleryImage> sList) {
        this.saveList = sList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_gallery_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GalleryImage nData = saveList.get(position);
        holder.imageItem.buildDrawingCache(true);
        Picasso.with(context).load(nData.getImageMedium()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(holder.imageItem);
        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProduct(nData.getImageMedium());
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveList.remove(position);
                removeItemPic.add(""+nData.getImageId());
                notifyDataSetChanged();
                ServiceFragment.txtImageCount.setText(""+saveList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
    private void showProduct(String imgUrl) {
        final Dialog zDialog = new Dialog(context);
        zDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        zDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        zDialog.setContentView(R.layout.zoom_product_dialog);
        Button btnClose = (Button)zDialog.findViewById(R.id.btnClose);
        TouchImageView imageView = (TouchImageView)zDialog.findViewById(R.id.imageView);
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(imageView);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zDialog.dismiss();
            }
        });
        zDialog.show();
    }
}
