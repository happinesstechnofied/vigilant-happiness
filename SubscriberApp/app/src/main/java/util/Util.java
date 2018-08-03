package util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Piyush on 11/2/2017.
 */

public class Util {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

}
