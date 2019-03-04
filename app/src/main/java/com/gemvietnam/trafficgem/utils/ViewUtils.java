package com.gemvietnam.trafficgem.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Quannv on 3/31/2017.
 */

public class ViewUtils {
  public static void hideKeyBoard(Context context) {
    // Check if no view has focus:
    View view = ((Activity) context).getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }
}
