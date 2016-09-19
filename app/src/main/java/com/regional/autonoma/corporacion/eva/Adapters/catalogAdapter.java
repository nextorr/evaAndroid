package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Communication.EvaServices;
import com.regional.autonoma.corporacion.eva.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nestor on 16-Aug-16.
 * display the information from the content adapter
 */
public class catalogAdapter extends CursorAdapter {

    private Context mContext;

    public catalogAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
        mContext = context;
    }

    public static class ViewHolder{
        public final TextView titleView;
        public final TextView descriptionView;
        public final TextView commitmentTimeView;
        public final TextView totalVideosView;
        public final Button enrollButton;

        public ViewHolder (View view){
            titleView = (TextView) view.findViewById(R.id.textView_catalog_title);
            descriptionView = (TextView) view.findViewById(R.id.textView_catalog_description);
            commitmentTimeView = (TextView) view.findViewById(R.id.textView_catalog_commitment_time);
            totalVideosView = (TextView) view.findViewById(R.id.textView_total_videos);
            enrollButton = (Button) view.findViewById(R.id.button_enroll);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.piece_calalog_list_item
                ,parent,false);

        ViewHolder viewHolder = new ViewHolder(rowView);

        rowView.setTag(viewHolder);

        return rowView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        boolean enrolled;

        //read the data from the cursor
        //we expect the fully formed cursor here
        //TODO: implement projections to reduce errors with this indices
        viewHolder.titleView.setText(cursor.getString(2));
        viewHolder.descriptionView.setText(cursor.getString(3));
        viewHolder.commitmentTimeView.setText(cursor.getString(4) + " / " + cursor.getString(5));
        viewHolder.totalVideosView.setText(cursor.getString(6));
        enrolled = Boolean.parseBoolean(cursor.getString(7));
        viewHolder.enrollButton.setEnabled(!enrolled); //enable the button if the user is not enrolled
        if(enrolled){
            viewHolder.enrollButton.setText(R.string.enrolled_button_enabled);
        }else{
            viewHolder.enrollButton.setText(R.string.enrolled_button_disabled);
            viewHolder.enrollButton.setTag(cursor.getInt(1));
            viewHolder.enrollButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //navigate the the view hierarchy to get the piece_catalog_list_item root element
                    //View parentRow = (View) v.getParent().getParent().getParent().getParent().getParent();
                    //ListView listView = (ListView) parentRow.getParent();
                    //int pos = listView.getPositionForView(parentRow);
                    //final Cursor internalCursor = getCursor();
                    final int courseID = (int) v.getTag();
                    //final Cursor internalCursor = (Cursor)v.getTag();
                    Toast.makeText(v.getContext(), "realizando inscripcion, espera un momento", Toast.LENGTH_LONG).show();
                    //disable the button so the user cant hit it repeteadly
                    v.setEnabled(false);
                    callEnrollToCourseService(courseID, v);

                }
            });
        }
    }


    private void callEnrollToCourseService (int courseID, View view){
        SharedPreferences userPreferences = mContext.getSharedPreferences(
                mContext.getResources().getString(R.string.user_logIn),Context.MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey.equals("empty")){
            Toast.makeText(mContext, "no public key stored", Toast.LENGTH_LONG).show();
        }else{
            new QueryEvaServices(String.valueOf(courseID), userKey, view).execute("courseenrollments");
        }
    }


    public class QueryEvaServices extends AsyncTask<String, Void, String> {

        private final String mCourseID;
        private final String mPublicKey;
        private final Button mButtonView;

        QueryEvaServices(String courseID, String publicKey, View view) {
            mCourseID = courseID;
            mPublicKey = publicKey;
            mButtonView = (Button)view;
        }

        @Override
        protected String doInBackground(String... params) {
            EvaServices writeRequest = new EvaServices();

            // the JSON request to send
            JSONObject evaEnroll = new JSONObject();
            try{
                evaEnroll.put("publicKey", mPublicKey);
                evaEnroll.put("courseID", mCourseID);
            }catch (JSONException e){
                Log.e("catalogAdapter", "Error creating the request", e);
                return "ERROR : creating the request" ;
            }

            return  writeRequest.servicePath(params[0]).write(evaEnroll.toString());
        }

        @Override
        protected void onPostExecute(final String serviceJsonResponse) {
            String serverResponse;
            if (serviceJsonResponse != null){
                try{
                    JSONObject response = new JSONObject(serviceJsonResponse);
                    serverResponse = response.getString("evaCode");
                    Log.v("v/ENROLMENT", "the enrollment was succesfull, the response was: "+ serviceJsonResponse);
                    Toast.makeText(mButtonView.getContext(), serverResponse + " inscripcion exitosa", Toast.LENGTH_LONG).show();
                    //keep the button disabled but change the text
                    mButtonView.setText(R.string.enrolled_button_enabled);

                }catch (JSONException e){
                    EvaServices.handleServiceErrors(mButtonView.getContext(), serviceJsonResponse);
                    Toast.makeText(mButtonView.getContext(),"inscripcion no realizada" , Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(mButtonView.getContext(), "communication error 102", Toast.LENGTH_LONG).show();
            }

        }
    }
}
