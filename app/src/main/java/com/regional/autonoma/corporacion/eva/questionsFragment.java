package com.regional.autonoma.corporacion.eva;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.Adapters.questionAdapter;
import com.regional.autonoma.corporacion.eva.Communication.EvaServices;
import com.regional.autonoma.corporacion.eva.Model.Answer;
import com.regional.autonoma.corporacion.eva.Model.Question;
import com.regional.autonoma.corporacion.eva.dialogs.EvaluationDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by nestor on 13-Sep-16.
 * implements the functionality for the questions and grader
 */
public class questionsFragment extends Fragment {
    private static final String ARG_LESSON_DETAIL_ID = "lessonDetailID";

    private ProgressBar mServiceQuestions;
    protected questionAdapter mQuestionAdapter;
    private String mlessonDetailID;
    private Button mEvaluateButton;

    EvaluationDialogFragment mScoreCardFragment = new EvaluationDialogFragment();

    public static questionsFragment newInstance(String lessonDetailID){
        questionsFragment fragment = new questionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LESSON_DETAIL_ID, lessonDetailID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = R.layout.fragment_questions;
        final View rootView = inflater.inflate(layoutId, container, false);
        Bundle bundle = getArguments();

        mlessonDetailID = bundle.getString(ARG_LESSON_DETAIL_ID);
        //configure the toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Preguntas");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //setting Up the UI
        mServiceQuestions = (ProgressBar) rootView.findViewById(R.id.progressBar_questions);
        mServiceQuestions.setIndeterminate(true);
        //set the expandable list adapter.
        //get a reference to the expandable list adapter
        final ArrayList<Question> questionList = new ArrayList<Question>();
        mQuestionAdapter = new questionAdapter(
                getActivity(),
                questionList
        );
        ExpandableListView listView = (ExpandableListView) rootView.findViewById(
                R.id.expandableListView_questions
        );
        //bind the UI result elements to the adapter so he can update its values.
        mQuestionAdapter.setPointsView((TextView) rootView.findViewById(R.id.textView_quizPoints),
                (TextView) rootView.findViewById(R.id.textView_quizPointsToPass),
                (TextView) rootView.findViewById(R.id.textView_quizResult));
        listView.setAdapter(mQuestionAdapter);

        //setting up the click event for the evaluate button
        mEvaluateButton = (Button) rootView.findViewById(R.id.button_evaluate);
        mEvaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEvaluateButton.setEnabled(false);
                Toast.makeText(getActivity(), "Evaluando.. por favor espere", Toast.LENGTH_LONG).show();
                callGraderService(getActivity(), mQuestionAdapter.evaluateAll());
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        callQuestionDetailService(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_player_and_resources, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                //TODO: remove this option from the definition
                //do nothing
                break;
            case R.id.action_scoreCard:
                mScoreCardFragment.show(getFragmentManager(), "dialog");
                //do nothing
                break;
            case R.id.action_evaluate:
                Toast.makeText(getActivity(), "Evaluando.. por favor espere", Toast.LENGTH_LONG).show();
                callGraderService(getActivity(), mQuestionAdapter.evaluateAll());
                break;
            case R.id.action_fullscreen:
                //do nothing.
                break;
            default:
                //Toast.makeText(getActivity(), "Menu selection error Q", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callQuestionDetailService (Context context){
        SharedPreferences userPreferences = context.getSharedPreferences(
                getResources().getString(R.string.user_logIn), Context.MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey.equals("empty")){
            Toast.makeText(context, "no public key stored", Toast.LENGTH_LONG).show();
        }else{
            new QueryEvaServices().execute("questiondetails",userKey,mlessonDetailID);
        }
    }

    private void callGraderService (Context context, Hashtable<Integer, Integer> responses){
        SharedPreferences userPreferences = context.getSharedPreferences(
                getResources().getString(R.string.user_logIn), Context.MODE_PRIVATE);
        String userKey = userPreferences.getString("publicKey", "empty");
        if(userKey.equals("empty")){
            Toast.makeText(context, "no public key stored", Toast.LENGTH_LONG).show();
        }else{
            new QueryEvaGraderService(responses,context).execute("grader",userKey,mlessonDetailID);
        }
    }

    //service call class
    public class QueryEvaServices extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params){
            EvaServices serviceReader = new EvaServices();
            final String result = serviceReader.servicePath(params[0]).serviceParameter("publicKey", params[1]).
                    serviceParameter("lessondetailid", params[2]).read();
            publishProgress(100);
            return result;
        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            //log the response
            Log.v("courseActivity", "the response is: " + serviceJsonResponse);
            //parse the data and post it on the list adapter.
            int tempID;
            int tempAnswerID;
            String tempStatement;
            String tempText;
            String tempEvaType;
            int tempPoints;
            boolean tempIsCorrect;
            Question tempQuestion;
            Answer tempAnswer;
            int lastAnswerID;
            ArrayList<Question> tempQuestionList = new ArrayList<Question>();
            ArrayList<Answer> tempAnswerList;

            //TODO: move the parsing to its own class so its easier to manage
            if (serviceJsonResponse != null){
                try{
                    JSONObject responseObject = new JSONObject(serviceJsonResponse);
                    JSONArray questionList = responseObject.getJSONArray("quizDetail");
                    JSONArray answerList;
                    JSONObject answerDetail;
                    for (int i = 0; i< questionList.length();i++){
                        //initialize the last answer Id flag
                        lastAnswerID = 0;
                        //initialize a new array list
                        tempAnswerList = new ArrayList<Answer>();
                        //get the question Statement and ID
                        tempStatement = questionList.getJSONObject(i).getJSONObject("question").getString("statement");
                        tempID = questionList.getJSONObject(i).getJSONObject("question").getInt("QuestionID");
                        tempEvaType = questionList.getJSONObject(i).getJSONObject("question").getString("evaType");
                        //IMPORTANT: initialize the question points assuming there is no detail
                        tempPoints = questionList.getJSONObject(i).getJSONObject("question").getInt("points");
                        //now if the user has detail for this answer modify the object accordingly
                        //optJSONObject gets the objet or null, does not throw exception
                        answerDetail = questionList.getJSONObject(i).optJSONObject("detail");
                        tempQuestion = new Question(tempID, tempStatement, tempEvaType, tempPoints);
                        if(answerDetail != null){
                            //the first true indicates its a initialization parameter,
                            //so the wrong answers are shown to the user as not answered
                            tempQuestion.setDetail(true, answerDetail.getBoolean("isCorrect"),
                                    answerDetail.getInt("totalGrongAttempts"),
                                    answerDetail.getInt("finalScore"),
                                    answerDetail.getInt("currentMaxScore"));
                            lastAnswerID = answerDetail.getInt("lastGradedAnswerID");
                        }
                        //now parse the answers
                        answerList = questionList.getJSONObject(i).getJSONObject("question").getJSONArray("answerOptions");
                        for(int j = 0; j < answerList.length(); j++){
                            tempText = answerList.getJSONObject(j).getString("text");
                            tempIsCorrect = answerList.getJSONObject(j).getBoolean("isCorrect");
                            tempAnswerID = answerList.getJSONObject(j).getInt("AnswerID");
                            tempAnswer = new Answer(tempAnswerID, tempText, tempIsCorrect);
                            if((answerDetail!= null) && (tempAnswerID == lastAnswerID) &&
                                    answerDetail.getBoolean("isCorrect")){
                               tempAnswer.setIsChecked(true);
                            }
                            tempAnswerList.add(tempAnswer);
                        }
                        tempQuestion.setAnswerList(tempAnswerList);
                        tempQuestionList.add(tempQuestion);
                    }
                    //once we have the question array fully formed, we set the adapter
                    mQuestionAdapter.addNewItems(tempQuestionList);
                    //set the Score card UI
                    //TODO: marked for deletion
                    mQuestionAdapter.setPointsCard(responseObject.getBoolean("viewed"),
                            responseObject.getBoolean("passed"),
                            responseObject.getInt("totalObtainedPoints"));

                    //set up the score card values
                    mScoreCardFragment.setScoreCard(responseObject.getBoolean("viewed"),
                            responseObject.getBoolean("passed"),
                            responseObject.getInt("totalObtainedPoints"),
                            mQuestionAdapter.getTotalPoints(), getContext().getResources());

                }catch (JSONException e){
                    EvaServices.handleServiceErrors(getActivity(), serviceJsonResponse);
                    Log.e("courseActivity","error parsing the response");
                }
            }

        }

        @Override
        protected void onPreExecute() {
            mServiceQuestions.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //mServiceProgress.setProgress(values[0]);
            if(values[0] == 100){
                mServiceQuestions.setVisibility(View.GONE);
            }
        }

    }

    public class QueryEvaGraderService extends AsyncTask<String,Void,String> {

        private Hashtable<Integer, Integer> mResponses;
        private Context mContext;

        QueryEvaGraderService(Hashtable<Integer, Integer> responses, Context context){

            mResponses = responses;
            mContext = context;
        }


        @Override
        protected String doInBackground(String... params){
            EvaServices serviceWriter = new EvaServices();
            JSONObject evaGrader;
            try{
                //build the JSON reques to sent to the server
                JSONArray responseJSON = new JSONArray();
                JSONObject responseItem;
                Iterator<Hashtable.Entry<Integer, Integer>> iter = mResponses.entrySet().iterator();
                while (iter.hasNext()){
                    Hashtable.Entry<Integer, Integer> entry = iter.next();
                    responseItem = new JSONObject();
                    //The table is coded as KEY -> answerID, VALUE -> questionID
                    responseItem.put("questionID", entry.getValue());
                    responseItem.put("answerID", entry.getKey());
                    responseJSON.put(responseItem);
                }

                //final JSON response.
                evaGrader = new JSONObject();
                evaGrader.put("publicKey", params[1]); //params[1] has the current public key
                evaGrader.put("lessonDetailID", params[2]); //params[2] has the current lesson detail ID
                evaGrader.put("responses", responseJSON); //JSON array with all the responses
            }
            catch (JSONException e){
                Toast.makeText(mContext, "error parsing the request", Toast.LENGTH_LONG).show();
                Log.e("mainActivity", "Error parsing the request data: ", e);
                return null;
            }

            return serviceWriter.servicePath(params[0]).write(evaGrader.toString());
        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            //enable the button to prevent it from becoming irresponsive on errors
            mEvaluateButton.setEnabled(true);

            //parse the data and post it on the list adapter.
            if(serviceJsonResponse == null){
                Log.e("player activity", "null response from the service");
                Toast.makeText(mContext, "Communication Error", Toast.LENGTH_LONG).show();
                return;
            }
            //log the response
            Log.v("courseActivity", "the response is: " + serviceJsonResponse);
            try{
                JSONObject response = new JSONObject(serviceJsonResponse);
                JSONArray graderDetail = response.getJSONArray("questionDetail");
                for(int i = 0; i< graderDetail.length(); i++){
                    mQuestionAdapter.setGraderFromService(graderDetail.getJSONObject(i).getInt("questionID"),
                            graderDetail.getJSONObject(i).getInt("finalScore"),
                            graderDetail.getJSONObject(i).getBoolean("isCorrect"),
                            graderDetail.getJSONObject(i).getInt("totalGrongAttempts"),
                            graderDetail.getJSONObject(i).getInt("currentMaxScore"));
                }
                //todo: find a better way to to this process

                mQuestionAdapter.notifyDataSetChanged();

                mScoreCardFragment.setScoreCard(response.getBoolean("passed"),
                        response.getInt("currentTotalGrade"),
                        getContext().getResources());
                mScoreCardFragment.show(getFragmentManager(), "dialog");


                if(response.getBoolean("passed")){
                    //Toast.makeText(mContext, "Quiz aprobado, felicitaciones", Toast.LENGTH_LONG).show();
                    mQuestionAdapter.setPointsCard(response.getString("currentTotalGrade"), true);
                }else{
                    //Toast.makeText(mContext, "Reprobado", Toast.LENGTH_LONG).show();
                    mQuestionAdapter.setPointsCard(response.getString("currentTotalGrade"), false);
                }


            }catch (JSONException e){
                EvaServices.handleServiceErrors(mContext, serviceJsonResponse);
                Log.e("player activity", "error parsing the response");
            }

        }
    }




}
