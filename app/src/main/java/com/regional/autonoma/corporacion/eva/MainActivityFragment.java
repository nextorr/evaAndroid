package com.regional.autonoma.corporacion.eva;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Adapters.homePageAdapter;
import com.regional.autonoma.corporacion.eva.Communication.EvaServices;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private homePageAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;

    //key definitions to store the info into sharep preferences
    private final String COURSES_CERTIFIED = "courses_certified";
    private final String COURSES_PENDING = "courses_pending";
    private final String CATALOG_AVAILABLE = "catalog_available";
    private final String CATALOG_COMPLETED = "catalog_completed";
    private final String REQUIRED_AVAILABLE = "required_available";
    private final String REQUIRED_COMPLETED = "required_completed";


    //main page fields
    private TextView mCoursesCertified;
    private TextView mCoursesPending;
    private TextView mCatalogAvailable;
    private TextView mCatalogCompleted;
    private TextView mRequiredAvailable;
    private TextView mRequiredCompleted;

    //progress bar
    private ProgressBar mServiceProgress;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mCustomPagerAdapter = new homePageAdapter(getContext());

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        super.onCreate(savedInstanceState);


        //binding the UI elements so the service call can modify then
        mCoursesCertified = (TextView) rootView.findViewById(R.id.textView_certified_courses);
        mCoursesPending = (TextView) rootView.findViewById(R.id.textView_pending_courses);
        mCatalogAvailable = (TextView) rootView.findViewById(R.id.textView_available_catalog);
        mCatalogCompleted = (TextView) rootView.findViewById(R.id.textView_completed_catalog);
        mRequiredAvailable= (TextView) rootView.findViewById(R.id.textView_available_required);
        mRequiredCompleted= (TextView) rootView.findViewById(R.id.textView_completed_required);
        mServiceProgress = (ProgressBar) rootView.findViewById(R.id.progressBar_main);
        mServiceProgress.setIndeterminate(true);

        //set default values if they exist on the shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(preferences.contains(COURSES_CERTIFIED) && preferences.contains(COURSES_PENDING) &&
                preferences.contains(CATALOG_AVAILABLE) && preferences.contains(CATALOG_COMPLETED) &&
                preferences.contains(REQUIRED_AVAILABLE) && preferences.contains(REQUIRED_COMPLETED)){

            mCoursesCertified.setText(preferences.getString(COURSES_CERTIFIED, "--"));
            mCoursesPending.setText(preferences.getString(COURSES_PENDING, "--"));
            mCatalogAvailable.setText(preferences.getString(CATALOG_AVAILABLE, "--"));
            mCatalogCompleted.setText(preferences.getString(CATALOG_COMPLETED, "--"));
            mRequiredAvailable.setText(preferences.getString(REQUIRED_AVAILABLE, "--"));
            mRequiredCompleted.setText(preferences.getString(REQUIRED_COMPLETED, "--"));
        }

        //setting up the click events for the main navigation
        FrameLayout myCoursesFL = (FrameLayout) rootView.findViewById(R.id.frameLayout_misCursos);
        myCoursesFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the courses activity.
                Intent intent = new Intent(getActivity(), coursesActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, "courses");
                startActivity(intent);
            }
        });

        FrameLayout catalogFL = (FrameLayout)rootView.findViewById(R.id.frameLayout_catalog);
        catalogFL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //call the catalog activity
                Intent intent = new Intent(getActivity(), catalogActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, "catalog");
                startActivity(intent);
            }
        });

        FrameLayout requiredFL = (FrameLayout)rootView.findViewById(R.id.frameLayout_required);
        requiredFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the catalog activity, setting up the parameter so it can filter the info
                Intent intent = new Intent(getActivity(), catalogActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, "required");
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        callScoreService();
    }



    private void callScoreService (){
        SharedPreferences userPreferences = this.getActivity().getSharedPreferences(
                getResources().getString(R.string.user_logIn), Context.MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey.equals("empty")){
            Toast.makeText(this.getActivity(), "no public key stored fragment", Toast.LENGTH_LONG).show();
            //send the user to the login activity
            Intent logIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(logIntent);
        }else{
            new QueryEvaServices().execute("score", userKey);
        }
    }

   //------------------------------------------------------------------

    public class QueryEvaServices extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... table){
            EvaServices reader =  new EvaServices();
            String result = reader.servicePath(table[0]).serviceParameter("publicKey", table[1]).read();
            publishProgress(100);
            return  result;
        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            //parse the data and post it on the list adapter.
            JSONObject scoreList;

            if(serviceJsonResponse == null){
                //no user message here, the reasons for serviceJsonResponse to be null are
                //handled on the EvaServices
                return;
            }
            if(getContext() == null){
                //the service call completed when there is no context, do nothing
                return;
            }
            try{
                scoreList = new JSONObject(serviceJsonResponse);
                final String coursesCertified = String.valueOf(scoreList.getInt("completedCatalogCourses") +
                        scoreList.getInt("completedRequiredCourses"));
                final String coursesPending = scoreList.getString("totalActiveEnrollments");
                final String catalogAvailable = String.valueOf(scoreList.getInt("totalCatalogCourses"));
                final String catalogCompleted = scoreList.getString("completedCatalogCourses");
                final String requiredAvailable = scoreList.getString("totalRequiredCourses");
                final String requiredCompleted = scoreList.getString("completedRequiredCourses");

                mCoursesCertified.setText(coursesCertified);
                mCoursesPending.setText(coursesPending);
                mCatalogAvailable.setText(catalogAvailable);
                mCatalogCompleted.setText(catalogCompleted);
                mRequiredAvailable.setText(requiredAvailable);
                mRequiredCompleted.setText(requiredCompleted);

                //save the state
                SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                preferences.putString(COURSES_CERTIFIED, coursesCertified);
                preferences.putString(COURSES_PENDING, coursesPending);
                preferences.putString(CATALOG_COMPLETED, catalogCompleted);
                preferences.putString(CATALOG_AVAILABLE, catalogAvailable);
                preferences.putString(REQUIRED_COMPLETED, requiredCompleted);
                preferences.putString(REQUIRED_AVAILABLE, requiredAvailable);
                preferences.commit();


                Log.v("mainActivity", "the response is: " + serviceJsonResponse);

            }catch(JSONException e){
                EvaServices.handleServiceErrors(getContext(), serviceJsonResponse);
                //TODO: block marked to delete
//                if (scoreList != null){
//                    //its a valid JSON, so we ended up here only if there where a bad request
//                    String outMsg;
//                    try {
//                        outMsg = scoreList.getString("Message");
//                        Toast.makeText(getContext(),outMsg, Toast.LENGTH_LONG).show();
//                        //send the user to the login page
//                        Intent logIntent = new Intent(getActivity(), LoginActivity.class);
//                        startActivity(logIntent);
//                    } catch (JSONException ex) {
//                        if(serviceJsonResponse.startsWith("ERROR")){
//                            Toast.makeText(getContext(),serviceJsonResponse, Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                        Toast.makeText(getContext(),"communication error 100", Toast.LENGTH_LONG).show();
//                        Log.e("mainActivity", "communication error, invalid response received");
//                    }
//                }
                Log.e("mainActivity", "error parsing the response");
            }
        }

        @Override
        protected void onPreExecute() {
            //mServiceProgress.setProgress(10);
            mServiceProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //mServiceProgress.setProgress(values[0]);
            if(values[0] == 100){
                mServiceProgress.setVisibility(View.GONE);
            }
        }
    }
}
