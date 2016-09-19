package com.regional.autonoma.corporacion.eva;

/**
 * Created by nestor on 5/1/2016.
 */

//TODO: this name is a little misleading,
    // try to make a "master" class for all this interactions
    //plus this class can serve as a mini database.
public class Chapter {
    public int lessonID;
    public String evaType;
    public String title;
    public int chapterNumber;
    public String description;
    public String videoURL;


    //chapter constructor
    public Chapter (String title, int chapterNumber){
        this.evaType = "chapter";
        //TODO: the service call need to assert for this index
        this.lessonID = -1;
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.description = "not aplicable";
        this.videoURL = "not aplicable";
    }

    //lesson constructor
    //care: the difference is in the parameters
    //TODO: this is prone to confusion, find a better way
    public Chapter (int ID, String title, String description, String videoURL){
        this.lessonID = ID;
        this.evaType = "lesson";
        this.title = title;
        //TODO: make sure the Adapter assert this value
        this.chapterNumber = -1;
        this.description = description;
        this.videoURL = videoURL;

    }
}
