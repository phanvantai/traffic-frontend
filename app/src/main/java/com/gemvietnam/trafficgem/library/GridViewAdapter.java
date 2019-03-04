package com.gemvietnam.trafficgem.library;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gemvietnam.trafficgem.R;

/**
 * Created by Stork on 06/12/2016.
 */

public class GridViewAdapter extends BaseAdapter {
  private Activity mContext;

  public GridViewAdapter(Activity context) {
    mContext = context;
  }

  @Override
  public int getCount() {
    return mThumbIds.length;
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {
      convertView = mContext.getLayoutInflater().inflate(R.layout.item_gridview, parent, false);
    }
    TextView mTextView = (TextView) convertView.findViewById(R.id.item_gridview_tv);
    convertView.setBackgroundResource(mThumbIds[position]);
    int screenHeight = Util.getScreenSize(mContext).y;
    int cellHeight = screenHeight / 20;
    convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, cellHeight));
    return convertView;
  }

  private int[] mThumbIds = {

  };
}
