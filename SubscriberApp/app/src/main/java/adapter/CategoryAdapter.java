package adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.subscriber.R;

import java.util.List;
import model.Categories;


/**
 * Created by Piyush on 11/13/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Categories> saveList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView saveItemsIcon;
        public TextView txtCateName;

        public MyViewHolder(View view) {
            super(view);
            saveItemsIcon = (SimpleDraweeView) view.findViewById(R.id.saveItemsIcon);
            txtCateName = (TextView) view.findViewById(R.id.txtCateName);
        }
    }
    public CategoryAdapter(Context context, List<Categories> sList) {
        this.saveList = sList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_category_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Categories nData = saveList.get(position);
        holder.txtCateName.setText(nData.getParentCatName());
        final Uri uri = Uri.parse(nData.getImageSmall());
        if(nData.getImageSmall().equalsIgnoreCase("")){
            holder.saveItemsIcon.setImageResource(R.drawable.ic_placeholder);
        }else{
            holder.saveItemsIcon.setImageURI(uri);
        }
        holder.saveItemsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
}

