package com.regional.autonoma.corporacion.eva;

import java.util.ArrayList;

/**
 * Created by nestor on 5/5/2016.
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
    public Question(int questionID, String statement, String evaType) {
        this.questionID = questionID;
        this.statement = statement;
        this.evaType = evaType;
        this.evaluation = EVA_NOT_ANSWERRED;
    }

    public void setAnswerList(ArrayList<Answer> answerList){
        this.answerList = answerList;
    }

    public void setDetail(boolean isCorrect, int attempts, int finalScore, int currentMaxPossibleScore){
        evaluation = isCorrect? EVA_CORRECT:EVA_INCORRECT;
        mAttempts = attempts;
        mFinalScore = finalScore;
        mCurrentMaxPossibleScore = currentMaxPossibleScore;
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
