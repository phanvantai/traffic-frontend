package com.gemvietnam.trafficgem.screen.leftmenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import java.util.HashMap;
import java.util.Map;
import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The LeftMenu Fragment
 */
public class LeftMenuFragment extends ViewFragment<LeftMenuContract.Presenter> implements LeftMenuContract.View {

  @Bind(R.id.menu_profile_img)
  CircleImageView mProfileImg;
  @Bind(R.id.profile_name_tv)
  TextView mNameTv;
  @Bind(R.id.profile_email_tv)
  TextView mEmailTv;
  @Bind(R.id.menu_your_location_tv)
  TextView mYourLocationTv;
  @Bind(R.id.menu_direction_tv)
  TextView mDirectionTv;
  @Bind(R.id.menu_minus_img)
  ImageView mMinusImg;
  @Bind(R.id.menu_plus_img)
  ImageView mPlusImg;
  @Bind(R.id.menu_normal_search_tv)
  TextView mNormalSearchTv;
  @Bind(R.id.menu_advance_search_tv)
  TextView mAdvanceSearchTv;
  @Bind(R.id.menu_traffic_state_tv)
  TextView mTrafficStateTv;
  @Bind(R.id.menu_signout_tv)
  TextView mSignoutTv;
  @Bind(R.id.menu_expand_ll)
  LinearLayout mExpandLl;

  private final Map<TextView, MenuItem> mNavigationItemMap = new HashMap<>();

  public static LeftMenuFragment getInstance() {
    return new LeftMenuFragment();
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_left_menu;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);

    mMinusImg.setVisibility(View.VISIBLE);
    mPlusImg.setVisibility(View.GONE);
    mExpandLl.setVisibility(View.VISIBLE);

    onMenuItemClicked();

    return view;
  }

  private void onMenuItemClicked() {
    mYourLocationTv.setSelected(true);
    mYourLocationTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_location_selected, 0, 0, 0);

    mNavigationItemMap.put(mYourLocationTv, MenuItem.YOUR_LOCATION);
    mNavigationItemMap.put(mNormalSearchTv, MenuItem.NORMAL_SEARCH);
    mNavigationItemMap.put(mDirectionTv, MenuItem.DIRECTION);
    mNavigationItemMap.put(mAdvanceSearchTv, MenuItem.ADVANCE_SEARCH);
    mNavigationItemMap.put(mTrafficStateTv, MenuItem.TRAFFIC_STATE);

    mNavigationItemMap.put(mSignoutTv, MenuItem.SIGN_OUT);
    for (final Map.Entry<TextView, MenuItem> entry : mNavigationItemMap.entrySet()) {
      entry.getKey().setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mPresenter.onMenuItemClicked(entry.getValue());

          // Change color
          unselectAll();
          entry.getKey().setSelected(true);

          int iconId = 0;
          switch (entry.getValue()) {
            case YOUR_LOCATION:
              iconId = R.drawable.ic_location_selected;
              break;
            case DIRECTION:
              iconId = R.drawable.ic_direction_selected;
              break;
            case TRAFFIC_STATE:
              iconId = R.drawable.ic_traffic_selected;
              break;
            case SIGN_OUT:
              iconId = R.drawable.ic_signout_white;
              break;
          }
          entry.getKey().setCompoundDrawablesRelativeWithIntrinsicBounds(iconId, 0,0,0);

          if (entry.getValue() == MenuItem.NORMAL_SEARCH || entry.getValue() == MenuItem.ADVANCE_SEARCH ||
              entry.getValue() == MenuItem.DIRECTION) {
            mDirectionTv.setSelected(true);
            mDirectionTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_direction_selected, 0, 0, 0);
          } else {
            mDirectionTv.setSelected(false);
            mDirectionTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_direction_white, 0, 0, 0);
          }
        }
      });
    }
  }

  private void unselectAll() {
    mYourLocationTv.setSelected(false);
    mYourLocationTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_location_white, 0,0,0);
    mDirectionTv.setSelected(false);
    mDirectionTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_direction_white, 0,0,0);
    mNormalSearchTv.setSelected(false);
    mAdvanceSearchTv.setSelected(false);
    mTrafficStateTv.setSelected(false);
    mTrafficStateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_traffic_white, 0, 0,0);
    mSignoutTv.setSelected(false);
    mSignoutTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signout_white, 0,0,0);
  }
}