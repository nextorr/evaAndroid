package com.regional.autonoma.corporacion.eva;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Adapters.lessonAdapter;
import com.regional.autonoma.corporacion.eva.Communication.EvaServices;
import com.regional.autonoma.corporacion.eva.data.evaContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class lessonsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COURSE_DETAIL_LOADER = 1;

    private static final String SAVED_TITLE_KEY = "saved_title";
    private static final String SAVED_DESCRIPTION_KEY = "saved_description";
    private static final String SAVED_COURSE_ID_KEY = "saved_course_id";

    private ProgressBar mServiceProgress;

    private String mCourseID;
    private String mTitle;
    private String mDescription;

    private chapterAdapter mChapterAdapter;
    private lessonAdapter mLessonAdapter;

    public lessonsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lessons, container, false);

        //setting up UI elements
        mServiceProgress = (ProgressBar)rootView.findViewById(R.id.progressBar_lessons);
        mServiceProgress.setIndeterminate(true);

        //before anything call the service, as it is Async
        //in this case the parameter is the course ID of the selected item
        //to get the parameters passed to this view we inspect the intent object
        Intent intent = getActivity().getIntent();
        //the param convention is 0 -> title, 1-> description 2->courseID
        if(intent != null){
            if(intent.hasExtra("eva.intent.parameters")){
                String[] courseParams = intent.getStringArrayExtra("eva.intent.parameters");
                //verify ID is a positive number
                //TODO: see how we can send numbers as parameters
                if (Integer.parseInt(courseParams[2]) >= 0){
                    //a valid index, then query the service with that info
                    callLessonDetailService(courseParams[2]);
                    mCourseID = courseParams[2];
                }
                //TODO: inform the user an error ocurred.
                //set up the page title and description
                ((TextView) rootView.findViewById(R.id.textView_courseTitle_big)).setText(courseParams[0]);
                ((TextView) rootView.findViewById(R.id.textView_courseDescription_big)).setText(courseParams[1]);
                mTitle = courseParams[0];
                mDescription = courseParams[1];
                //since here we are getting fresh data, clear all the saved persisten one
                //to start new
                clearState(savedInstanceState);

            }
            //use this for rotation purposes
            if (savedInstanceState != null &&
                    savedInstanceState.containsKey(SAVED_TITLE_KEY) &&
                    savedInstanceState.containsKey(SAVED_DESCRIPTION_KEY) &&
                    savedInstanceState.containsKey(SAVED_COURSE_ID_KEY)) {
                // The listview probably hasn't even been populated yet.  Actually perform the
                // swapout in onLoadFinished.
                callLessonDetailService(savedInstanceState.getString(SAVED_COURSE_ID_KEY));

                mCourseID = savedInstanceState.getString(SAVED_COURSE_ID_KEY);
                mTitle = savedInstanceState.getString(SAVED_TITLE_KEY);
                mDescription = savedInstanceState.getString(SAVED_DESCRIPTION_KEY);

                ((TextView) rootView.findViewById(R.id.textView_courseTitle_big)).setText(
                        savedInstanceState.getString(SAVED_TITLE_KEY));
                ((TextView) rootView.findViewById(R.id.textView_courseDescription_big)).setText(
                        savedInstanceState.getString(SAVED_DESCRIPTION_KEY));
            }
            //saved instace null or does not contain data
            else{
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                if(preferences.contains(SAVED_DESCRIPTION_KEY) &&
                        preferences.contains(SAVED_TITLE_KEY) &&
                        preferences.contains(SAVED_COURSE_ID_KEY)){

                    //new QueryEvaServices().execute(preferences.getString(SAVED_COURSE_ID_KEY, ""));
                    callLessonDetailService(preferences.getString(SAVED_COURSE_ID_KEY, ""));
                    mCourseID = preferences.getString(SAVED_COURSE_ID_KEY, "");
                    mTitle = preferences.getString(SAVED_TITLE_KEY, "");
                    mDescription = preferences.getString(SAVED_DESCRIPTION_KEY, "");

                    ((TextView) rootView.findViewById(R.id.textView_courseTitle_big)).setText(
                            preferences.getString(SAVED_TITLE_KEY, ""));
                    ((TextView) rootView.findViewById(R.id.textView_courseDescription_big)).setText(
                            preferences.getString(SAVED_DESCRIPTION_KEY, ""));
                }
            }
        }

        //this lesson adapter is populated by a loader
        mLessonAdapter = new lessonAdapter(getActivity(), null, 0);
        //get a reference to the chapter list view
        ListView listView = (ListView) rootView.findViewById(R.id.listView_chapter_lessons);
        listView.setAdapter(mLessonAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //launch an explicit intent to the lesson player and resources activity
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor != null){
                    String[] lessonData = {
                            //index for description
                            cursor.getString(6),
                            //index for videoURL
                            cursor.getString(7),
                            //index for lessonID
                            String.valueOf(cursor.getInt(3)),
                            //index for lessonDetailID
                            String.valueOf(cursor.getInt(12))
                    };
                    Intent intent = new Intent(getActivity(), playerAndResourcesActivity.class)
                            .putExtra("eva.intent.parameters", lessonData);
                    startActivity(intent);
                }
                else
                {
                    Log.e("Error: ", "cannot start activity, check cursor loader functionality");
                }
            }
        });



        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mCourseID==null && mTitle==null&& mDescription==null){
            super.onSaveInstanceState(outState);
            return;
        }
        if(!mCourseID.isEmpty() && !mTitle.isEmpty() && !mDescription.isEmpty()){
            outState.putString(SAVED_COURSE_ID_KEY, mCourseID);
            outState.putString(SAVED_TITLE_KEY, mTitle);
            outState.putString(SAVED_DESCRIPTION_KEY, mDescription);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if(mCourseID==null && mTitle==null&& mDescription==null){
            super.onPause();
            return;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(!mCourseID.isEmpty() && !mTitle.isEmpty() && !mDescription.isEmpty()){
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SAVED_COURSE_ID_KEY, mCourseID);
            editor.putString(SAVED_TITLE_KEY, mTitle);
            editor.putString(SAVED_DESCRIPTION_KEY, mDescription);
            editor.commit();
        }
        super.onPause();
    }

    //this removes all stored and persistent data
    private void clearState(Bundle savedInstanceState){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(preferences.contains(SAVED_DESCRIPTION_KEY) &&
                preferences.contains(SAVED_TITLE_KEY) &&
                preferences.contains(SAVED_COURSE_ID_KEY)) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.remove(SAVED_DESCRIPTION_KEY);
            editor.remove(SAVED_TITLE_KEY);
            editor.remove(SAVED_COURSE_ID_KEY);
            editor.commit();
        }
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SAVED_TITLE_KEY) &&
                savedInstanceState.containsKey(SAVED_DESCRIPTION_KEY) &&
                savedInstanceState.containsKey(SAVED_COURSE_ID_KEY)) {
            savedInstanceState.remove(SAVED_TITLE_KEY);
            savedInstanceState.remove(SAVED_DESCRIPTION_KEY);
            savedInstanceState.remove(SAVED_COURSE_ID_KEY);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(COURSE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        //ATTENTION: in the contract course detail refers to the lesson list.
        Uri lessonUri = evaContract.courseDetailEntry.buildCourseDetailUri(Integer.parseInt(mCourseID));
        return new CursorLoader(getActivity(),
                lessonUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //IMPORTANT: here the data is loaded into the adapter
        mLessonAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //free resources.
        mLessonAdapter.swapCursor(null);
    }

    private void callLessonDetailService (String courseID){
        SharedPreferences userPreferences = this.getActivity().getSharedPreferences(
                getResources().getString(R.string.user_logIn), Context.MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey.equals("empty")){
            Toast.makeText(this.getActivity(), "no public key stored", Toast.LENGTH_LONG).show();
        }else{
            new QueryEvaServices().execute("lessondetail",courseID, userKey);
        }

    }

    //TODO: investigate if here is the best placement for the service call
    public class QueryEvaServices extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... table){
            EvaServices serviceReader = new EvaServices();
            final String result = serviceReader.servicePath(table[0]).serviceParameter("courseID", table[1]).
                    serviceParameter("publicKey", table[2]).read();
            publishProgress(100);
            return result;
        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            //log the response
            Log.v("courseActivity", "the response is: " + serviceJsonResponse);
            //parse the data and post it on the list adapter.
            if(serviceJsonResponse != null){
                ContentValues values = new ContentValues();
                values.put(evaContract.courseDetailEntry.COLUMN_COURSE_ID, mCourseID);
                values.put(evaContract.courseDetailEntry.COLUMN_COURSE_DETAIL_LIST, serviceJsonResponse);
                //update or add the corresponding entry
                try{
                    //update first, since it will be the most frequent operation
                    getContext().getContentResolver().update(evaContract.courseDetailEntry.buildCourseDetailUriJson(Integer.parseInt(mCourseID)),
                            values,
                            null,
                            null);
                }
                catch (SQLException e){
                    //here the entry with course ID does not exist, create it
                    getContext().getContentResolver().insert(evaContract.courseDetailEntry.CONTENT_URI_JSON,values);
                    //manually notify the change in data
                    getContext().getContentResolver().notifyChange(evaContract.courseDetailEntry.buildCourseDetailUri(Integer.parseInt(mCourseID)),null);
                }

            }
        }
        @Override
        protected void onPreExecute() {
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
