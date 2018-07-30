package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import com.subscriber.R;

import java.util.List;
import model.BannerAdData;


/**
 * Created by Piyush on 10/11/2017.
 */

public class SponsorAdapter extends PagerAdapter {

    private Context context;
    private List<BannerAdData> sponsorBannerList;
    private LayoutInflater layoutInflater;

    public SponsorAdapter(Context context, List<BannerAdData> sponsorBanner) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.sponsorBannerList = sponsorBanner;
    }

    @Override
    public int getCount() {
        return sponsorBannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = this.layoutInflater.inflate(R.layout.sponsor_activity, container, false);
        SimpleDraweeView sponsorBanner = (SimpleDraweeView) view.findViewById(R.id.sponsorBanner);
        Uri uri = Uri.parse(sponsorBannerList.get(position).getImageMedium());
        sponsorBanner.setImageURI(uri);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

