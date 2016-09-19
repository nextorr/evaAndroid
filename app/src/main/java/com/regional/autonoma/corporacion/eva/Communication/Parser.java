package com.regional.autonoma.corporacion.eva.Communication;

import android.database.MatrixCursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nestor on 14-Aug-16.
 * group the parsing methods to get a cursor for the service communications
 */


public final class Parser {
    //make the constructor private to prevent instantiation
    private Parser(){}

    //populate the matrix cursos with the data stored in jsonCourses
    public static MatrixCursor parseAllCourses(String jsonCourses){
        //define a matrix cursor
        String[] columns = new String[] {"_id","CourseID", "title", "Description"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        int loopIndex = 0;

        try{
            JSONArray courseList = new JSONArray(jsonCourses);
            for (int i = 0; i< courseList.length();i++){
                loopIndex = i;
                matrixCursor.addRow(new Object[]{
                        i+1, // _id
                        courseList.getJSONObject(i).getInt("CourseID"), // CourseID
                        courseList.getJSONObject(i).getString("title"), //title
                        courseList.getJSONObject(i).getString("description"), //description
                });
            }
        }catch (JSONException e){
            parseBadRequest(matrixCursor, jsonCourses, loopIndex);
            Log.e("courseActivity", "error parsing the response");
        }
        Log.v("database read", "the response is: " + jsonCourses);
        return matrixCursor;
    }
    //-------------------------------------------------------------------------------------------------
    public static MatrixCursor parseUserCourseCatalog(String jsonCourseCatalog, String selection){
        String[] columns = new String[] {"_id","CourseID", "title", "description", "dailyHours", "totalDays",
                "totalVideos" , "enrolled"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        boolean enrollmentStatus;
        int loopIndex = 0;
        try{
            JSONArray courseCatalogList = new JSONArray(jsonCourseCatalog);
            for(int i = 0; i < courseCatalogList.length(); i++){
                loopIndex = i;
                // if there is (any)selection parameter, send only the required courses
                if(selection != null && selection.equals("required = true")){
                    if(courseCatalogList.getJSONObject(i).getString("required").equals("false"))
                        continue; //we are looking for the required, so if it is not do not create an entry
                }
                else{
                    if(courseCatalogList.getJSONObject(i).getString("required").equals("true"))
                        continue; //we are looking for the not required, so if it is not do not create an entry
                }
                //calculate the enrollment status
                //TODO: if statement marked to delete.
//                if(courseCatalogList.getJSONObject(i).getJSONObject("course").optJSONArray("enrollments")== null){
//                    enrollmentStatus = false; //the user is not enrolled in the course
//                }else{
//                    enrollmentStatus = true; // the user Is enrolled in the course
//                }
                enrollmentStatus = !(courseCatalogList.getJSONObject(i).getJSONObject("course").optJSONArray("enrollments")== null);
                matrixCursor.addRow(new Object[]{
                        i+1, //_id
                        courseCatalogList.getJSONObject(i).getString("courseID"), //courseID
                        courseCatalogList.getJSONObject(i).getJSONObject("course").getString("title"), //title
                        courseCatalogList.getJSONObject(i).getJSONObject("course").getString("description"), //description
                        courseCatalogList.getJSONObject(i).getJSONObject("course").getString("commitmentHoursPerDay"), //dailyHours
                        courseCatalogList.getJSONObject(i).getJSONObject("course").getString("commitmentDays"), //totalDays
                        courseCatalogList.getJSONObject(i).getJSONObject("course").getString("totalLessons"), //totalDays
                        enrollmentStatus
                });
            }
        }catch(JSONException e){
            parseBadRequest(matrixCursor,jsonCourseCatalog,loopIndex);
            Log.e("courseActivity", "error parsing the response");
        }

        Log.v("database read", "the response is: " + jsonCourseCatalog);
        return matrixCursor;
    }
    //-------------------------------------------------------------------------------------------------

    public static MatrixCursor parseUserCourseEnrollments(String jsonEnrollments){
        //define a matrix cursor
        String[] columns = new String[] {"_id","CourseID", "title", "description", "progress", "points", "totalPoints" , "due"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        float progress = 0;
        int totalLessons = 1;
        int loopIndex = 0;
        JSONArray courseEnrollmentsList;
        try{
            courseEnrollmentsList = new JSONArray(jsonEnrollments);
            for (int i = 0; i<courseEnrollmentsList.length();i++){
                loopIndex = i;
                //calculate some fields
                if(!courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getJSONObject("Course").getString("totalLessons").equals("null")){
                    // the total lessons field its not null.
                    totalLessons = courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getJSONObject("Course").getInt("totalLessons");
                }
                if(totalLessons != 0){
                    progress = (float)courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getInt("completedLessons") / (float)totalLessons;
                }
                //if totalLesson == 0 then progress will be the initialization value, with is 0

                matrixCursor.addRow(new Object[]{
                        i + 1, //_id
                        courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getJSONObject("Course").getInt("CourseID"),//courseID
                        courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getJSONObject("Course").getString("title"), //title
                        courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getJSONObject("Course").getString("description"), //description
                        progress * 100, //progress in percent
                        courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getInt("currentScore"), //points
                        courseEnrollmentsList.getJSONObject(i).getJSONObject("enrollment").getJSONObject("Course").getInt("totalPoints"), //total points
                        courseEnrollmentsList.getJSONObject(i).getString("dueDate") //dueDate
                });
            }
        } catch(JSONException e){
            //check for a bad request message
            parseBadRequest(matrixCursor, jsonEnrollments, loopIndex);
            Log.e("courseActivity", "error parsing the response");
        }

        Log.v("database read", "the response is: " + jsonEnrollments);

        return matrixCursor;
    }

    //-------------------------------------------------------------------------------------------------

    //populates the matrix cursor with the data stored in jsonLessons
    public static MatrixCursor parseAllLessons(String jsonLessons){
        String[] columns = new String[] {"_id", "evaType", "lessonID", "chapterNumber", "title", "Description", "videoURL" };
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        int _id = 1;
        try{
            JSONArray chapterList = new JSONArray(jsonLessons);
            JSONArray lessonList;
            for (int i = 0; i< chapterList.length();i++){
                //get the chapter title and number
                matrixCursor.addRow(new Object[]{
                        _id,
                        "chapter",
                        -1,
                        chapterList.getJSONObject(i).getInt("index"),
                        chapterList.getJSONObject(i).getString("title"),
                        "not aplicable",
                        "not aplicable"
                });
                //IMPORTANT: increment the cursor index
                _id++;
                //now parse the lessons
                lessonList = chapterList.getJSONObject(i).getJSONArray("lessons");
                for(int j = 0; j < lessonList.length(); j++){
                    matrixCursor.addRow(new Object[]{
                            _id,
                            "lesson",
                            lessonList.getJSONObject(j).getInt("LessonID"),
                            -1,
                            lessonList.getJSONObject(j).getString("title"),
                            lessonList.getJSONObject(j).getString("description"),
                            lessonList.getJSONObject(j).getString("videoURL")
                    });
                    //IMPORTANT: increment the cursor index
                    _id++;
                }
            }
        }catch (JSONException e){
            parseBadRequest(matrixCursor,jsonLessons,_id);
            Log.e("courseActivity", "error parsing the response");
        }
        Log.v("Parser read", "the response is: " + jsonLessons);
        return matrixCursor;
    }

    //-------------------------------------------------------------------------------------------------

    public static MatrixCursor parseUserLessonDetail(String jsonLessonDetail){
        String[] columns = new String[] {"_id", "evaType","chapterID", "lessonID", "chapterNumber", "title", "Description", "videoURL",
                "percentViewed" , "viewed", "passed", "currentGrade", "lessonDetailID"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        int _id = 0;
        try{
            JSONArray chapterList = new JSONArray(jsonLessonDetail);
            JSONArray lessonDetailList;
            for (int i = 0; i<chapterList.length(); i++){
                //IMPORTANT: increment the cursor index
                _id++;
                //we are creating a flat strucutre, denormalizing the related data
                matrixCursor.addRow(new Object[]{
                        _id, // _id
                        "chapter", // evatype
                        chapterList.getJSONObject(i).getJSONObject("chapter").getInt("ChapterID"),// chapterID
                        -1,// lessonID
                        chapterList.getJSONObject(i).getJSONObject("chapter").getInt("index"), // chapterNumber
                        chapterList.getJSONObject(i).getJSONObject("chapter").getString("title"), //title
                        "not aplicable", // description
                        "not aplicable", // videoURL
                        chapterList.getJSONObject(i).getInt("percentViewed"), // percentViewed
                        null, //viewed
                        null, //passed
                        null, //currentGrade
                        null //lessonDetailID
                });

                lessonDetailList = chapterList.getJSONObject(i).getJSONArray("lessons");
                for (int j = 0; j < lessonDetailList.length(); j++){
                    //IMPORTANT: increment the cursor index
                    _id++;
                    matrixCursor.addRow(new Object[]{
                            _id, // _id
                            "lesson", // evatype
                            chapterList.getJSONObject(i).getJSONObject("chapter").getInt("ChapterID"),// chapterID
                            lessonDetailList.getJSONObject(j).getJSONObject("info").getInt("LessonID"),// lessonID
                            chapterList.getJSONObject(i).getJSONObject("chapter").getInt("index"), // chapterNumber
                            lessonDetailList.getJSONObject(j).getJSONObject("info").getString("title"), //title
                            lessonDetailList.getJSONObject(j).getJSONObject("info").getString("description"), // description
                            lessonDetailList.getJSONObject(j).getJSONObject("info").getString("videoURL"), // videoURL
                            chapterList.getJSONObject(i).getInt("percentViewed"), // percentViewed
                            lessonDetailList.getJSONObject(j).getJSONObject("userDetail").getBoolean("viewed"), //viewed
                            lessonDetailList.getJSONObject(j).getJSONObject("userDetail").getBoolean("passed"), //passed
                            lessonDetailList.getJSONObject(j).getJSONObject("userDetail").getInt("currentTotalGrade"), //currentGrade
                            lessonDetailList.getJSONObject(j).getJSONObject("userDetail").getInt("evaLessonDetailID") //lessonDetailID
                    });
                }
            }
        } catch (JSONException e){
            parseBadRequest(matrixCursor,jsonLessonDetail,_id);
            Log.e("courseActivity", "error parsing the response");
        }
        Log.v("Parser read", "the response is: " + jsonLessonDetail);
        return matrixCursor;
    }

    //-------------------------------------------------------------------------------------------------


    //return a row in the received cursor indicating the bad request message
    private static void parseBadRequest (MatrixCursor cursor, String JsonMessage, int index){
        //default initialization, all unassigned fields are left null
        Object[] data = new Object[cursor.getColumnCount()];
        //here we are processing a bad request, its expected that no entry has been added to the cursor
        data[cursor.getColumnIndex("_id")] = index + 2;

        //if there is no evatype or enrroled column the method fails silently (perfect!!)
        if (cursor.getColumnIndex("evaType") != -1)
            data[cursor.getColumnIndex("evaType")] = "lesson";
        if (cursor.getColumnIndex("enrolled") != -1)
            data[cursor.getColumnIndex("enrolled")] = true; // this disables the enroll button when there is an error

        JSONObject badRequest;
        try {
            badRequest = new JSONObject(JsonMessage);
            data[cursor.getColumnIndex("title")] = "SERVICE ERROR";
            data[cursor.getColumnIndex("Description")] = badRequest.getString("Message");
        } catch (JSONException e){
            if (JsonMessage.startsWith("ERROR")) {
                //handle service communication hand shake errors
                data[cursor.getColumnIndex("title")] = "SERVICE HANDSHAKE ERROR";
                data[cursor.getColumnIndex("Description")] = "server general error";
            }
            //this is an undefined error, possibly communication corruption
            data[cursor.getColumnIndex("title")] = "UNKNOWN ERROR";
            data[cursor.getColumnIndex("Description")] = "try again the operation. details: " + JsonMessage;
        }
        cursor.addRow(data);
    }
}
