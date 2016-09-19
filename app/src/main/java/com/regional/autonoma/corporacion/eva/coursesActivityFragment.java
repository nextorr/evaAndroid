package com.regional.autonoma.corporacion.eva;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Adapters.courseAdapter;
import com.regional.autonoma.corporacion.eva.Communication.EvaServices;
import com.regional.autonoma.corporacion.eva.data.evaContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class coursesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private courseAdapter mCourseAdapter;
    private static final int COURSE_LOADER = 0;
    private ProgressBar mServiceProgress;

    public coursesActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_courses, container, false);

        //setting Up UI elements
        mServiceProgress = (ProgressBar)rootView.findViewById(R.id.progressBar_courses);
        mServiceProgress.setIndeterminate(true);

        //this course adapter is populated by a loader
        mCourseAdapter = new courseAdapter(getActivity(),null,0);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_courseList);
        listView.setAdapter(mCourseAdapter);


        //setting the on click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String[] courseData = {
                            //index for title
                            cursor.getString(2),
                            //index for description
                            cursor.getString(3),
                            //index for courseID
                            String.valueOf(cursor.getInt(1))
                    };
                    Intent intent = new Intent(getActivity(), lessonsActivity.class)
                            .putExtra("eva.intent.parameters", courseData);
                    startActivity(intent);
                } else {
                    Log.e("Error: ", "cannot start activity, check cursor loader functionality");
                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(COURSE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    //put the service query on the onstart method
    //to handle lifecycle managment.
    @Override
    public void onStart() {

        callEnrollmentServices();
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.v("NEXT", "create options menu called fragment");
        inflater.inflate(R.menu.menu_courses, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.v("NEXT", "create options menu called main activity");
            return true;
        }
        if (id == R.id.action_showPublicKey) {
            SharedPreferences userPreferences = this.getActivity().getSharedPreferences(
                    getResources().getString(R.string.user_logIn), Context.MODE_PRIVATE);
            String userKey = userPreferences.getString("publicKey", "empty");
            if(userKey.equals("empty")){
                Toast.makeText(this.getActivity(), "no public key stored", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this.getActivity(), "public key: "+ userKey, Toast.LENGTH_LONG).show();
            }
            Log.v("NEXT", "the user public Key is: " + userKey);
            return true;
        }
        else if (id == R.id.action_getData){
            //TODO: remove this option when testiog ins completed
            callEnrollmentServices();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        Uri courseUri = evaContract.courseEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                courseUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //IMPORTANT: here the data is loaded into the adapter
        mCourseAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //free resources.
        mCourseAdapter.swapCursor(null);
    }

    private void callEnrollmentServices(){
        SharedPreferences userPreferences = this.getActivity().getSharedPreferences(
                getResources().getString(R.string.user_logIn), Context.MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey.equals("empty")){
            Toast.makeText(this.getActivity(), "no public key stored", Toast.LENGTH_LONG).show();
        }else{
            new QueryEvaServices().execute("courseenrollments", userKey);
        }
    }

    //TODO: investigate if here is the best placement for the service call
    public class QueryEvaServices extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... table){
            EvaServices serviceReader = new EvaServices();
            final String result = serviceReader.servicePath(table[0]).serviceParameter("publickey", table[1]).read();
            publishProgress(100);
            return result;
        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            if (serviceJsonResponse != null){
                // bad request errors are handled on the parser
                ContentValues values = new ContentValues();
                values.put(evaContract.courseEntry.COLUMN_COURSE_LIST, serviceJsonResponse);
                getContext().getContentResolver().insert(evaContract.courseEntry.CONTENT_URI_JSON, values);
            }
            Log.v("courseActivity", "the response is: " + serviceJsonResponse);
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
