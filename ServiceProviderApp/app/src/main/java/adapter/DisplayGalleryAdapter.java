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
import android.widget.ImageView;
import com.photopicker.model.ImageModel;
import com.squareup.picasso.Picasso;
import com.vendorprovider.R;
import java.io.ByteArrayOutputStream;
import java.util.List;
import imagezoom.TouchImageView;
import model.GalleryImage;

/**
 * Created by rajgandhi on 26/07/18.
 */

public class DisplayGalleryAdapter extends RecyclerView.Adapter<DisplayGalleryAdapter.MyViewHolder> {

    private List<GalleryImage> saveList;
    Context context;
    Bitmap bitmap;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageItem;

        public MyViewHolder(View view) {
            super(view);
            imageItem = (ImageView) view.findViewById(R.id.imageItem);
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
        bitmap = BitmapFactory.decodeFile(nData.getImageMedium());
        getEncoded64ImageStringFromBitmap(bitmap);
        holder.imageItem.buildDrawingCache(true);
        bitmap = holder.imageItem.getDrawingCache(true);
        Picasso.with(context).load(getEncoded64ImageStringFromBitmap(bitmap).toString()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(holder.imageItem);
        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showProduct(uri);
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
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }
}
