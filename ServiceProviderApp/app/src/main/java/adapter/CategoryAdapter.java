package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.vendorprovider.R;

import java.util.ArrayList;

import model.Categories;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<Categories> categoryList;

    public CategoryAdapter(Context mContext, ArrayList<Categories> appStructure) {
        this.categoryList = appStructure;
        this.context = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_main_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Categories appManagerData = categoryList.get(position);

        holder.txtCategoryName.setText(appManagerData.getParentCatName());
        if(appManagerData.getImageMedium().equalsIgnoreCase("")){

        }else{
            Picasso.with(context).load(appManagerData.getImageMedium()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(holder.imgCateImage);
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtCategoryName;
        protected CircularImageView imgCateImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txtCategoryName);
            imgCateImage = (CircularImageView) itemView.findViewById(R.id.imgCateImage);

        }
    }
}