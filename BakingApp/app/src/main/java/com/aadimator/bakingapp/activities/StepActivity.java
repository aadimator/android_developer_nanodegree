package com.aadimator.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.aadimator.bakingapp.R;
import com.aadimator.bakingapp.fragments.StepFragment;
import com.aadimator.bakingapp.models.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity {

    private static final String BUNDLE_STEP_DATA = "com.aadimator.bakingapp.activities.step_data";
    private static final String BUNDLE_CURRENT_RECIPE = "com.aadimator.bakingapp.activities.current_recipe";
    private static final String BUNDLE_CURRENT_STEP = "com.aadimator.bakingapp.activities.current_step";

    @BindView(R.id.tl_activity_step_viewpager)
    TabLayout mTabLayout;
    @BindView(R.id.vp_activity_step_viewpager)
    ViewPager mViewPager;

    public static Intent newIntent(Context packageContext, ArrayList<Step> stepList,
                                   int currentStep, String recipeName) {
        Intent intent = new Intent(packageContext, StepActivity.class);
        intent.putExtra(BUNDLE_STEP_DATA, stepList);
        intent.putExtra(BUNDLE_CURRENT_STEP, currentStep);
        intent.putExtra(BUNDLE_CURRENT_RECIPE, recipeName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        final ArrayList<Step> stepList = getIntent().getExtras().getParcelableArrayList(BUNDLE_STEP_DATA);
        final int currentStep = getIntent().getExtras().getInt(BUNDLE_CURRENT_STEP);
        String currentRecipeName = getIntent().getExtras().getString(BUNDLE_CURRENT_RECIPE);

        getSupportActionBar().setTitle(currentRecipeName);

        for (Step step : stepList) {
            if (step.getId() == 0) {
                mTabLayout.addTab(mTabLayout.newTab().setText("Intro"));
            } else {
                mTabLayout.addTab(mTabLayout.newTab().setText(
                        String.format(getString(R.string.step_number_format), (step.getId()))));
            }
        }

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            mTabLayout.setVisibility(View.GONE);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return StepFragment.newInstance(stepList.get(position));
            }

            @Override
            public int getCount() {
                return stepList.size();
            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mViewPager.setCurrentItem(currentStep);
    }
}