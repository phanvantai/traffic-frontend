package com.gemvietnam.trafficgem.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;

/**
 * Created by Stork on 02/12/2016.
 */

public final class Util {
    public static final class Operations {
        private Operations() throws InstantiationException {
            throw new InstantiationException("This class is not for instantiation");
        }

        /**
         * Checks to see if the device is online before carrying out any operations.
         *
         * @return
         */
        public static boolean isOnline(Context context) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }


    }

    private Util() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static Point getScreenSize(Activity activity) {
        Display disp = activity.getWindowManager().getDefaultDisplay();
        Point dimensions = new Point();
        disp.getSize(dimensions);
        return dimensions;
    }
}
