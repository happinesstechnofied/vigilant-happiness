package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.subscriber.R;
import com.subscriber.ViewServicesActivity;

import java.util.List;

import model.ServicesData;

/**
 * Created by Piyush on 11/1/2017.
 */

public class SearchServiceAdapter extends RecyclerView.Adapter<SearchServiceAdapter.MyViewHolder> {

    private List<ServicesData> notifyList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView vendorBanner;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            vendorBanner = (SimpleDraweeView) view.findViewById(R.id.vendorBanner);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }

    public SearchServiceAdapter(Context context,List<ServicesData> moviesList) {
        this.notifyList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_recent_service_fragment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ServicesData nData = notifyList.get(position);

        if (nData.getImage() == null && nData.getImage().equalsIgnoreCase("")) {
            holder.vendorBanner.setImageResource(R.drawable.ic_placeholder);
        } else {
            Uri uri = Uri.parse(nData.getImage());
            holder.vendorBanner.setImageURI(uri);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewServicesActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifyList.size();
    }
}

