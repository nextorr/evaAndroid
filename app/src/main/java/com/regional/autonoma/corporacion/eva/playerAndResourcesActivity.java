package com.regional.autonoma.corporacion.eva;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Model.Lesson;

import java.util.ArrayList;
import java.util.List;

public class playerAndResourcesActivity extends AppCompatActivity {
    //global data to be used when recreating the fragments
    protected Lesson mLessonInfo;
    protected String mLessonID;
    protected String mLessonDetailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_and_resources);

        //call the service to populate the question list
        //first get the parameters to call the service
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("eva.intent.parameters")){
            //the param convention is 0 -> title, 1-> videoURL 2->lessonID 3->lessonDetailID
            String[] lessonParams = intent.getStringArrayExtra("eva.intent.parameters");
            //verify ID is a positive number
            if (Integer.parseInt(lessonParams[2]) >= 0){
                //a valid index, then query the service with that info
                //new QueryEvaServices().execute(lessonParams[2]);
                mLessonInfo = new Lesson(lessonParams[0],lessonParams[1]);
                mLessonID = lessonParams[2];
                mLessonDetailID = lessonParams[3];

            }
            else{
                Toast.makeText(this, "Intent paramenter error", Toast.LENGTH_LONG).show();
            }

        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Clase");
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(playerAndResourcesFragment.newInstance(mLessonID, mLessonInfo.description, mLessonInfo.videoURL));
        mSectionsPagerAdapter.addFragment(questionsFragment.newInstance(mLessonDetailID));

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if(mViewPager != null){
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        else{
            Toast.makeText(this,"viewpager adapter error", Toast.LENGTH_LONG).show();
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }
        else{
            Toast.makeText(this,"viewpager tab error", Toast.LENGTH_LONG).show();
        }




        //TODO: here we are intentionally disabling the FAB, but we need the code to
        //communicate witht he professor. modify the functionality to be initiated on the menu option.
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab = null;
        if(fab!= null){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
//                    Cursor c = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
//                            new String[]{ContactsContract.Contacts.Data._ID}, ContactsContract.Data.DATA1 + "=?",
//                            new String[]{"573015920856@s.whatsapp.net"}, null);
//
//
//                    c.moveToFirst();
//
//                    Intent whatsapp = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + c.getString(0)));
//                    c.close();
//                    whatsapp.setType("text/plain");
//                    whatsapp.putExtra(whatsapp.EXTRA_TEXT, "buenos dias profesor, tengo una pregunta");
//                    whatsapp.setPackage("com.whatsapp");
//                    startActivity(whatsapp);

                        Uri uri = Uri.parse("smsto:" + "573102033106");
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                        //sendIntent.setAction(Intent.ACTION_SEND);
                        //sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                    sendIntent.setType("text/plain");
//                    sendIntent.putExtra("sms_body", "buenos dias profesor, tengo una pregunta");
                        sendIntent.setPackage("com.whatsapp");
                        startActivity(sendIntent);
                    }
                    catch (Exception e){
                        Log.e("Error", "Cant complete the whatsapp intent");

                    }
                }
            });
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }

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
                    icon = getBaseContext().getResources().getDrawable(R.drawable.eva_video);
                    icon.setBounds(0,0,icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                    sb = new SpannableString("    " + "RECURSOS");
                    imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                    //return "RECURSOS";
                case 1:
                    icon = getBaseContext().getResources().getDrawable(R.drawable.eva_quiz);
                    icon.setBounds(0,0,icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                    sb = new SpannableString("    " + "PREGUNTAS");
                    imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                    //return "PREGUNTAS";
            }
            return null;
        }

    }
}
