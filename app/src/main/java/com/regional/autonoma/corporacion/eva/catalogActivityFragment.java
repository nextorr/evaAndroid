package com.regional.autonoma.corporacion.eva;

import android.content.ContentValues;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Adapters.catalogAdapter;
import com.regional.autonoma.corporacion.eva.Communication.EvaServices;
import com.regional.autonoma.corporacion.eva.data.evaContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class catalogActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //TODO: implement the catalog adapter
    private catalogAdapter mCatalogAdapter;
    private static final int CATALOG_LOADER = 0;
    private String mType = "catalog"; //default here to show the full catalog
    private ProgressBar mServiceProgress;


    public catalogActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);

        mCatalogAdapter = new catalogAdapter(getActivity(),null,0);
        //setting up  UI elements
        mServiceProgress = (ProgressBar)rootView.findViewById(R.id.progressBar_catalog);
        mServiceProgress.setIndeterminate(true);


        ListView listView = (ListView) rootView.findViewById(R.id.listView_catalogList);
        listView.setAdapter(mCatalogAdapter);

        //set up mType, to tell the adapter to filter for required courses
        Intent intent = getActivity().getIntent();
        if(intent !=  null){
            //here we expect data in the EXTRA_TEXT key
            if(intent.hasExtra(Intent.EXTRA_TEXT)){
                //TODO: make some standarization of the names expected here
                mType = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        //NOTE: this view does not star new activities.

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATALOG_LOADER, null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        SharedPreferences userPreferences = this.getActivity().getSharedPreferences(
                getResources().getString(R.string.user_logIn), this.getActivity().MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey == "empty"){
            Toast.makeText(this.getActivity(), "no public key stored", Toast.LENGTH_LONG).show();
        }else{
            new QueryEvaServices().execute("coursecatalog", userKey);
        }
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.v("NEXT", "create options menu called fragment");
        inflater.inflate(R.menu.menu_catalog, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO: implement the contract for the catalog
        Uri catalogUri = evaContract.catalogEntry.CONTENT_URI;

        if(mType.equals("catalog")){
            return new CursorLoader(getActivity(),
                    catalogUri,
                    null,
                    null,
                    null,
                    null);
        }

        return new CursorLoader(getActivity(),
                catalogUri,
                null,
                "required = true", //filter the returned data
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCatalogAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCatalogAdapter.swapCursor(null);
    }

    //TODO: investigate if here is the best placement for the service call
    public class QueryEvaServices extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... table){
            EvaServices serviceReader = new EvaServices();
            final String result = serviceReader.servicePath(table[0]).serviceParameter("publicKey", table[1]).read();
            publishProgress(100);
            return result;

        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            if (serviceJsonResponse != null) {

                // bad request errors are handled on the parser
                ContentValues values = new ContentValues();
                values.put(evaContract.catalogEntry.COLUMN_CATALOG_LIST, serviceJsonResponse);
                if(getContext()!= null){
                    getContext().getContentResolver().insert(evaContract.catalogEntry.CONTENT_URI_JSON, values);
                }
                else{
                    Log.e("calalog Activity", "null context");
                }

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
