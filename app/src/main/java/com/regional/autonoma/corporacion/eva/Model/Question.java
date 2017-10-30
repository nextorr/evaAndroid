package com.regional.autonoma.corporacion.eva.Model;

import com.regional.autonoma.corporacion.eva.R;

import java.util.ArrayList;

/**
 * Created by nestor on 5/5/2016.
 * structure to control the quizes and evaluation
 */
public class Question {
    //TODO: put this values on the Strings folder
    //TOCO: put this into an enumeration.
    public final String EVA_NOT_ANSWERRED = "pendiente";
    public final String EVA_CORRECT = "correcto";
    public final String EVA_INCORRECT = "incorrecto";

    public int questionID = 0;
    public int mAttempts = 0;
    public int mFinalScore = 0;
    public int mCurrentMaxPossibleScore = 0;
    public int totalPoints = 0;
    public int currentPoints = 0;
    public String statement;
    public String evaType;
    public String evaluation;
    public ArrayList<Answer> answerList;

    public Question(int questionID, String statement, String evaType, ArrayList<Answer> answerList) {
        this.questionID = questionID;
        this.statement = statement;
        this.evaType = evaType;
        this.evaluation = EVA_NOT_ANSWERRED;
        this.answerList = answerList;
    }

    public Question(int questionID, String statement, String evaType,int qTotalPoints) {
        this.questionID = questionID;
        this.statement = statement;
        this.evaType = evaType;
        this.totalPoints = qTotalPoints;
        this.currentPoints = qTotalPoints;
        this.evaluation = EVA_NOT_ANSWERRED;
    }

    public void setAnswerList(ArrayList<Answer> answerList){
        this.answerList = answerList;
    }

    public void setDetail(boolean init, boolean isCorrect, int attempts, int finalScore, int currentMaxPossibleScore){
        evaluation = isCorrect? EVA_CORRECT:EVA_NOT_ANSWERRED;
        mAttempts = attempts;
        mFinalScore = finalScore;
        mCurrentMaxPossibleScore = currentMaxPossibleScore;
        currentPoints = adjustQuestionsTotalPoints(totalPoints, currentMaxPossibleScore);
    }
    public void setDetail(boolean isCorrect, int attempts, int finalScore, int currentMaxPossibleScore){
        evaluation = isCorrect? EVA_CORRECT:EVA_INCORRECT;
        mAttempts = attempts;
        mFinalScore = finalScore;
        mCurrentMaxPossibleScore = currentMaxPossibleScore;
        currentPoints = adjustQuestionsTotalPoints(totalPoints, currentMaxPossibleScore);
    }


    private int adjustQuestionsTotalPoints(int totalPoints, int pointWeight){
        double total = totalPoints;
        double weight = pointWeight;
        weight = (weight / 100);
        total = total * weight;
        total = Math.floor(total);
        return (int)total;
    }

    public int getEvaluationImage(){
        switch (evaluation){
            case EVA_NOT_ANSWERRED:
                return R.drawable.eva_icon_not_answered;
            case EVA_CORRECT:
                return R.drawable.eva_icon_correct;
            case EVA_INCORRECT:
                return R.drawable.eva_icon_incorrect;
        }
        return R.drawable.eva_icon_homeitem;
    }


    public void evaluate(){
        //all the options must be valid to the answer to be true
        // at least for the multiple selection single answer option
        for(int i =0; i<answerList.size();i++){
            if(answerList.get(i).isCorrect != answerList.get(i).getIsChecked()){
                evaluation=EVA_INCORRECT;
                return;
            }
        }
        //so, if no incorrect anwer was selected then the answer is valid
        evaluation=EVA_CORRECT;
    }
}
