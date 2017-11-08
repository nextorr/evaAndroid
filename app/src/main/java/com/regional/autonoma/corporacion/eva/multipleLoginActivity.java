package com.regional.autonoma.corporacion.eva;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

public class multipleLoginActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private LogInSectionsPagerAdapter mLogInSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_login);

        //The options toolbar is populated by each fragment call
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the log in methods
        mLogInSectionsPagerAdapter = new LogInSectionsPagerAdapter(getSupportFragmentManager());
        //manually add the pages for the logIn methods
        mLogInSectionsPagerAdapter.addFragment(domainLoginFragment.newInstance());
        mLogInSectionsPagerAdapter.addFragment(publicLoginFragment.newInstance());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.logInPager);
        mViewPager.setAdapter(mLogInSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.logInTabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //TODO: delete the FAB as its not needed on this view
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class LogInSectionsPagerAdapter extends FragmentPagerAdapter {
        //create an internal page list, since the amout of options are limited
        private final List<Fragment> fragmentList = new ArrayList<>();
        public LogInSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //add fragment to the internal pages list
        public void addFragment(Fragment fragment){fragmentList.add(fragment);}

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable icon;
            SpannableString sb;
            ImageSpan imageSpan;
            switch (position) {
                case 0:
//                    icon = getBaseContext().getResources().getDrawable(R.drawable.eva_video);
//                    icon.setBounds(0,0,icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//                    sb = new SpannableString("    " + "Institucional");
//                    imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);
//                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    return sb;
                return "INSTITUCIONAL";
                case 1:
//                    icon = getBaseContext().getResources().getDrawable(R.drawable.eva_quiz);
//                    icon.setBounds(0,0,icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//                    sb = new SpannableString("    " + "Personal");
//                    imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);
//                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    return sb;
                return "PERSONAL";
            }
            return null;
        }
    }
}
