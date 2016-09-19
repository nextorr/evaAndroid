package com.regional.autonoma.corporacion.eva;

/**
 * Created by nestor on 5/5/2016.
 */
public class Answer {
    public int answerID;
    public String text;
    public Boolean isCorrect;
    private Boolean isChecked;

    //default constructor
    public Answer(int answerID, String statement, Boolean isCorrect) {
        this.answerID = answerID;
        this.text = statement;
        this.isCorrect = isCorrect;
        this.isChecked = false;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }
}
