package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.vendorprovider.R;

import java.util.List;

import model.ReviewRatting;

/**
 * Created by rajgandhi on 13/08/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private List<ReviewRatting> saveList;
    Context context;
    SharedPreferences preferences;
    String checkOrder;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView rateUserIcon;
        public TextView txtRateUserName,txtRateUserDate,txtReviewUser;
        public RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            rateUserIcon = (CircularImageView) view.findViewById(R.id.rateUserIcon);
            txtRateUserName = (TextView) view.findViewById(R.id.txtRateUserName);
            txtRateUserDate = (TextView) view.findViewById(R.id.txtRateUserDate);
            txtReviewUser = (TextView) view.findViewById(R.id.txtReviewUser);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        }
    }

    public ReviewAdapter(Context context, List<ReviewRatting> sList) {
        this.saveList = sList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_review_rating, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        ReviewRatting nData = saveList.get(position);
        Picasso.with(context).load(nData.getUserAvatar()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).into(holder.rateUserIcon);
        holder.txtRateUserName.setText(nData.getUserName());
        String date = nData.getDate();
        String[] separated = date.split(" ");
        holder.txtRateUserDate.setText(separated[0]);
        holder.txtReviewUser.setText(nData.getReview());
        holder.ratingBar.setRating(Float.parseFloat(nData.getRattings()));

    }

    @Override
    public int getItemCount() {
        return saveList.size();
    }
}
