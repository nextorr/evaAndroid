package com.regional.autonoma.corporacion.eva.Model;

import java.util.ArrayList;

/**
 * Created by nestor on 5/9/2016.
 */

//this represent the info of a given lesson.
public class Lesson {
    public String description;
    public String videoURL;
    public ArrayList<evaFile> fileURLList;

    public Lesson(String description, String videoURL, ArrayList<evaFile> fileURLList) {
        this.description = description;
        this.videoURL = videoURL;
        this.fileURLList = fileURLList;
    }

    public Lesson(String description, String videoURL) {
        this.description = description;
        this.videoURL = videoURL;
        this.fileURLList = new ArrayList<evaFile>();
    }

    public void addFile(String fileUrl, String displayName){
        this.fileURLList.add(new evaFile(displayName, fileUrl));
    }
    public void clearFileList(){
        this.fileURLList.clear();
    }

    public String getFileURL(int position){
        return fileURLList.get(position).fileURL;
    }

    public String getFileDisplayName(int position){
        return fileURLList.get(position).nameToShow;
    }

    public class evaFile{
        public String nameToShow;
        public String fileURL;

        public evaFile(String nameToShow, String fileURL) {
            this.nameToShow = nameToShow;
            this.fileURL = fileURL;
        }
    }
}
