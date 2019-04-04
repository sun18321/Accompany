package com.play.accompany.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.play.accompany.R;
import com.play.accompany.base.BaseActivity;
import com.play.accompany.base.BaseFragment;
import com.play.accompany.fragment.HomeFragment;
import com.play.accompany.fragment.MessageFragment;
import com.play.accompany.fragment.MyFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

public class MainActivity extends BaseActivity {

    private BottomNavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private MessageFragment mMessageFragment;
    private MyFragment mMyFragment;
    private final String TAG_HOME = "home";
    private final String TAG_MESSAGE = "message";
    private final String TAG_MY = "my";
    private String mCurrentTag;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected String getTag() {
        return "mainActivity";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initViews() {
        mNavigationView = findViewById(R.id.navigation);
        mHomeFragment = HomeFragment.newInstance();
        mMessageFragment = MessageFragment.newInstance();
        mMyFragment = MyFragment.newInstance();

        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        switchFragment(mHomeFragment, TAG_HOME);
                        return true;
                    case R.id.menu_message:
                        switchFragment(mMessageFragment, TAG_MESSAGE);
                        return true;
                    case R.id.menu_my:
                        switchFragment(mMyFragment, TAG_MY);
                        return true;
                }
                return false;
            }
        });
        switchFragment(mHomeFragment, TAG_HOME);
    }

    private void switchFragment(BaseFragment fragment, String tag) {
        if (TextUtils.equals(mCurrentTag, tag)) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded() && getSupportFragmentManager().findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.nav_container,fragment, tag);
        }
        BaseFragment oldFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mCurrentTag);
        if (oldFragment != null && oldFragment.isAdded()) {
            fragmentTransaction.hide(oldFragment);
        }
        fragmentTransaction.show(fragment).commitAllowingStateLoss();
        mCurrentTag = tag;
    }

    @Override
    protected void parseIntent() {

    }

}
