package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.Model.Answer;
import com.regional.autonoma.corporacion.eva.Model.Question;
import com.regional.autonoma.corporacion.eva.R;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by nestor on 5/5/2016.
 * adapter to handle the questions view, also holds the answers and builds the query to the
 * grader service
 */
public class questionAdapter extends BaseExpandableListAdapter {
    //private final List list;
    private final ArrayList<Question> mQuestionList;
    private final Context mContext;
    private TextView mPointsCard;
    private TextView mRequiredPointsCard;
    private TextView mResultCard;


    public questionAdapter(Context mContext,@NonNull ArrayList<Question> questionList) {
        //super(mContext, resource, objects);
        this.mQuestionList = questionList;
        this.mContext = mContext;
    }


    public static class questionHolder{
        public final TextView questionStatement;
        public final TextView questionsPoints;
        public final ImageView evaluationImage;
        public questionHolder (View view){
            questionStatement = (TextView)view.findViewById(R.id.textView_questionText);
            questionsPoints = (TextView)view.findViewById(R.id.textView_questionPoints);
            evaluationImage = (ImageView)view.findViewById(R.id.imageView_evaluationResult);
        }
    }

    public static class answerHolder{
        public final CheckBox answerText;
        public answerHolder (View view){
            answerText = (CheckBox)view.findViewById(R.id.checkbox_answer_option);
        }
    }

    //this is a container to use in the tag property of the checkbutton
    public class itemPosition{
        public int groupPosition;
        public int childPosition;

        public itemPosition(int groupPosition, int childPosition) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }
    }

    //marked to delete
//    public void addAllItems (ArrayList<Question> list){
//        if (mQuestionList != null){
//            mQuestionList.addAll(list);
//        }
//        notifyDataSetChanged();
//    }
    //this clears previous information and adds new data
    public void addNewItems (ArrayList<Question> list){
        if (mQuestionList != null){
            mQuestionList.clear();
            mQuestionList.addAll(list);
        }
        notifyDataSetChanged();
    }

    //TODO:this is the best location for this method?
    //as the adapter must include only adapter methods
    public Hashtable<Integer, Integer> evaluateAll (){
        Hashtable<Integer, Integer> response = new Hashtable<>();

        //TODO: remove this client side grading
//        for(int i= 0; i<mQuestionList.size();i++){
//            mQuestionList.get(i).evaluate();
//        }

        //build the evaluation score card, send the selected answers to the grader.
        for (int i = 0; i<mQuestionList.size(); i++) {
            for (int j = 0; j < mQuestionList.get(i).answerList.size(); j++) {
                if (mQuestionList.get(i).answerList.get(j).getIsChecked()) {
                    //CAUTION: we use answerID as the first paramenter (KEY), since it must be unique.
                    response.put(mQuestionList.get(i).answerList.get(j).answerID , mQuestionList.get(i).questionID);
                }
            }
        }
        notifyDataSetChanged();
        return response;
    }

    public int getTotalPoints(){
        int total = 0;
        for (int i = 0; i<mQuestionList.size(); i++) {
            total = total + mQuestionList.get(i).totalPoints;
        }
        return  total;
    }

    public void setPointsView (TextView viewPoints, TextView viewRequired, TextView viewResult){
        mRequiredPointsCard = viewRequired;
        mResultCard = viewResult;
        mPointsCard = viewPoints;
    }

    public void setPointsCard(String points, boolean passed){
        int total = 0;
        for (int i = 0; i<mQuestionList.size(); i++) {
            total = total + mQuestionList.get(i).totalPoints;
        }
        String value = mContext.getResources().getString(R.string.quiz_evaluationPoints) + points +
                " / " + total;
        mPointsCard.setText(value);
        if(passed)
            mResultCard.setText(mContext.getResources().getString(R.string.quizAproved));
        else
            mResultCard.setText(mContext.getResources().getString(R.string.quizFailed));
    }
    //set the points card with the information from the question Array
    public void setPointsCard(boolean viewed, boolean passed, int obtainedScore){
        int total = 0;
        String pointsValue;
        String requiredValue;
        for (int i = 0; i<mQuestionList.size(); i++) {
            total = total + mQuestionList.get(i).totalPoints;
        }
        requiredValue = mContext.getResources().getString(R.string.quiz_pointsToPass) +
                String.valueOf((int)(Math.ceil((double)total*0.6)));
        mRequiredPointsCard.setText(requiredValue);
        if(viewed){
            pointsValue = mContext.getResources().getString(R.string.quiz_evaluationPoints) + String.valueOf(obtainedScore) +
                    " / " + total;
            if(passed)
                mResultCard.setText(mContext.getResources().getString(R.string.quizAproved));
            else
                mResultCard.setText(mContext.getResources().getString(R.string.quizFailed));
        }
        else{
            pointsValue = mContext.getResources().getString(R.string.quiz_evaluationPoints)  + " - / " + total;
            mResultCard.setText(mContext.getResources().getString(R.string.quizPending));
        }
        mPointsCard.setText(pointsValue);
    }

    public void setGraderFromService(int questionID, int finalScore, boolean isCorrect, int attempts,
                                     int currentMaxPossibleScore){
        for(int i = 0; i< mQuestionList.size(); i++){
            if(mQuestionList.get(i).questionID == questionID){
                mQuestionList.get(i).setDetail(isCorrect, attempts, finalScore, currentMaxPossibleScore);
            }
        }

    }

    private int getAnswerCount(int groupPosition){
        return mQuestionList.get(groupPosition).answerList.size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Answer> proxyAnswerList = mQuestionList.get(groupPosition).answerList;
        return proxyAnswerList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //see how this works, if anything breaks use this:
        return childPosition;
    }

//    @Override
//    public long getCombinedChildId(long groupId, long childId) {
//        ArrayList<Answer> proxyAnswerList = mQuestionList.get((int)groupId).answerList;
//        return proxyAnswerList.get((int)childId).answerID;
//    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  mQuestionList.get(groupPosition).answerList.size();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        View childView = convertView;
        answerHolder childViewHolder;
        Answer proxyAnswer = (Answer) getChild(groupPosition,childPosition);
        Question proxyQuestion = (Question) getGroup(groupPosition);
        if (childView == null){
            childView = LayoutInflater.from(mContext).inflate(R.layout.piece_answer_text,parent, false);
            childViewHolder = new answerHolder(childView);
            //WARNIGN: this may cause multiple instantiation of the event,
            //TODO: check if this does not cause unwanted behaviour
            childViewHolder.answerText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //this is a reference, so i think it can change the value
                    //on the original array
                    itemPosition position = (itemPosition) buttonView.getTag();
                    Answer internalAnswer = (Answer) getChild(
                            position.groupPosition,
                            position.childPosition
                    );
                    internalAnswer.setIsChecked(isChecked);
                    if(isChecked){
                        //uncheck all the other buttons in the group
                        for(int i = 0; i < getAnswerCount(position.groupPosition); i++){
                            if(position.childPosition != i){
                                internalAnswer = (Answer) getChild(
                                        position.groupPosition,
                                        i
                                );
                                internalAnswer.setIsChecked(false);
                            }
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            childView.setTag(childViewHolder);
        }else{
            childViewHolder = (answerHolder)childView.getTag();
        }
        childViewHolder.answerText.setTag(new itemPosition(groupPosition,childPosition));
        //setting the values on the UI
        childViewHolder.answerText.setText(proxyAnswer.text);
        childViewHolder.answerText.setChecked(proxyAnswer.getIsChecked());
        if(proxyQuestion.evaluation.equals(proxyQuestion.EVA_CORRECT)){
            //disble the controls
            childViewHolder.answerText.setEnabled(false);
        }
        else
        {
            childViewHolder.answerText.setEnabled(true);
        }




        return childView;
    }



    @Override
    public Object getGroup(int groupPosition) {
        return mQuestionList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mQuestionList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

//    @Override
//    public long getCombinedGroupId(long groupId) {
//        return mQuestionList.get((int)groupId).questionID;
//    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View groupView = convertView;
        questionHolder groupViewHolder;
        Question proxyQuestion = (Question) getGroup(groupPosition);
        String pointsValue;
        if(groupView == null){
            groupView = LayoutInflater.from(mContext).inflate(R.layout.piece_question_header,parent, false);
            groupViewHolder = new questionHolder(groupView);
            groupView.setTag(groupViewHolder);
        }else{
            groupViewHolder = (questionHolder) groupView.getTag();
        }
        //setting the values on the UI
        groupViewHolder.questionStatement.setText(proxyQuestion.statement);
        pointsValue = String.valueOf(proxyQuestion.currentPoints) +
                mContext.getResources().getString(R.string.questionsPoints);
        groupViewHolder.questionsPoints.setText(pointsValue);
        groupViewHolder.evaluationImage.setImageResource(proxyQuestion.getEvaluationImage());

        return groupView;
    }



    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
