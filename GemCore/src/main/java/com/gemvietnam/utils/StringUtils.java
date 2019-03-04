package com.gemvietnam.utils;

import com.gemvietnam.base.log.Logger;
import com.gemvietnam.common.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * String Utils
 * Created by neo on 2/16/2016.
 */
public class StringUtils {
  public static boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }

  public static boolean isEmpty(CharSequence s) {
    return s == null || s.length() == 0;
  }

  public static boolean isNumeric(String s) {
    return s.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
  }

  public static void openLink(Context context, String link) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(link));
    context.startActivity(i);
  }

  public static void callPhone(Context context, String phone) {
    // TODO: 1/19/2017 fix phone has no sim card 
    TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    int simState = telMgr.getSimState();
    switch (simState) {
      case TelephonyManager.SIM_STATE_ABSENT:
        // do something
        DialogUtils.showAlert(context, R.string.has_no_phone_card);
        break;
      case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
        // do something
        break;
      case TelephonyManager.SIM_STATE_PIN_REQUIRED:
        // do something
        break;
      case TelephonyManager.SIM_STATE_PUK_REQUIRED:
        // do something
        break;
      case TelephonyManager.SIM_STATE_READY:
        // do something
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
        break;
      case TelephonyManager.SIM_STATE_UNKNOWN:
        // do something
        break;
    }
  }

  /**
   * Strip accent character from string
   */
  public static String stripAccent(String s) {
    if (s == null) {
      return null;
    }
//        String encodedString = org.apache.commons.lang3.StringUtils.toEncodedString(s.getBytes(), Charset.defaultCharset());
    String stripAccent = org.apache.commons.lang3.StringUtils.stripAccents(s);
    Logger.e("stripAccent " + stripAccent);

    stripAccent = replaceSpecialAccent(stripAccent);
    return stripAccent;
  }

//    public static boolean isContain(String token, String target) {
//        target.toLowerCase().contains(token);
//    }

  /**
   * Replace specials characters with EN characters
   */
  private static String replaceSpecialAccent(String s) {
    String result = s.replaceAll("đ", "d");
    result = result.replaceAll("Đ", "D");
    return result;
  }

  /**
   * Format number to thousands comma (,) separator
   */
  public static String getNumberFormatted(long number) {
    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

    symbols.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(symbols);

    return formatter.format(number);
  }
}
