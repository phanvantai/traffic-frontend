package com.gemvietnam.trafficgem.screen.leftmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gemvietnam.base.viper.ViewFragment;
import com.gemvietnam.trafficgem.R;
import com.gemvietnam.trafficgem.library.User;
import com.gemvietnam.trafficgem.user.ProfileActivity;
import com.gemvietnam.trafficgem.utils.AppUtils;
import com.gemvietnam.trafficgem.utils.ReportActivity;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.gemvietnam.trafficgem.utils.Constants.LAST_USER;

/**
 * The LeftMenu Fragment
 */
public class LeftMenuFragment extends ViewFragment<LeftMenuContract.Presenter> implements LeftMenuContract.View {

    @BindView(R.id.ll_fragment_left_menu_profile)
    LinearLayout mProfile;
    @BindView(R.id.menu_profile_img)
    CircleImageView mProfileImg;
    @BindView(R.id.profile_name_tv)
    TextView mNameTv;
    @BindView(R.id.profile_email_tv)
    TextView mEmailTv;
    @BindView(R.id.menu_your_location_tv)
    TextView mYourLocationTv;
    @BindView(R.id.menu_direction_tv)
    TextView mDirectionTv;
//    @BindView(R.id.menu_minus_img)
//    ImageView mMinusImg;
//    @BindView(R.id.menu_plus_img)
//    ImageView mPlusImg;
    @BindView(R.id.menu_normal_search_tv)
    TextView mNormalSearchTv;
    @BindView(R.id.menu_advance_search_tv)
    TextView mAdvanceSearchTv;
    @BindView(R.id.menu_traffic_state_tv)
    TextView mTrafficStateTv;
    @BindView(R.id.tv_fragment_left_menu_report)
    TextView mReport;
    @BindView(R.id.tv_fragment_left_menu_view_traffic)
    TextView mViewTraffic;
    @BindView(R.id.tv_fragment_left_menu_view_event)
    TextView mViewEvent;
    @BindView(R.id.menu_signout_tv)
    TextView mSignOutTv;
    @BindView(R.id.menu_expand_ll)
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

        Hawk.init(getContext()).build();

//        mMinusImg.setVisibility(View.VISIBLE);
//        mPlusImg.setVisibility(View.GONE);
        User user = Hawk.get(LAST_USER);
        try {
            mNameTv.setText(user.getName());
            mEmailTv.setText(user.getEmail());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
//        AppUtils.loadImage(user.getPathAvatar(), mProfileImg);
        mExpandLl.setVisibility(View.VISIBLE);
        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                startActivity(intent);
            }
        });

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AppUtils.createDialog(getActivity());
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

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
        mNavigationItemMap.put(mViewTraffic, MenuItem.VIEW_STATE);
        mNavigationItemMap.put(mViewEvent, MenuItem.VIEW_EVENT);

        mNavigationItemMap.put(mSignOutTv, MenuItem.SIGN_OUT);

        for (final Map.Entry<TextView, MenuItem> entry : mNavigationItemMap.entrySet()) {
            entry.getKey().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onMenuItemClicked(entry.getValue());

                    // Change color
                    unSelectAll();
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
                        default:
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

                    if (entry.getValue() == MenuItem.VIEW_STATE || entry.getValue() == MenuItem.VIEW_EVENT ||
                            entry.getValue() == MenuItem.TRAFFIC_STATE) {
                        mTrafficStateTv.setSelected(true);
                        mTrafficStateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_traffic_selected, 0, 0, 0);
                    } else {
                        mTrafficStateTv.setSelected(false);
                        mTrafficStateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_traffic_white, 0, 0, 0);
                    }
                }
            });
        }
    }

    private void unSelectAll() {
        mYourLocationTv.setSelected(false);
        mYourLocationTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_location_white, 0,0,0);

        mDirectionTv.setSelected(false);
        mDirectionTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_direction_white, 0,0,0);
        mNormalSearchTv.setSelected(false);
        mAdvanceSearchTv.setSelected(false);

        mTrafficStateTv.setSelected(false);
        mTrafficStateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_traffic_white, 0, 0,0);
        mViewTraffic.setSelected(false);
        mViewEvent.setSelected(false);

        mSignOutTv.setSelected(false);
        mSignOutTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_signout_white, 0,0,0);
    }
}