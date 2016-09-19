package com.regional.autonoma.corporacion.eva.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by nestor on 6/12/2016.
 * database contract to use in the data model for the APP
 */
public class evaContract {
    //give it a empty constructor to follow android guidelines
    public evaContract(){}

    //define the content provider URIs ---------------------------------------
    public static final String CONTENT_AUTHORITY = "com.regional.autonoma.corporacion.eva";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //possible paths to use with the content provider
    public static final String PATH_CATALOG = "catalog";
    public static final String PATH_MY_COURSES = "mycourses";
    public static final String PATH_COURSE_DETAIL = "coursedetail";
    public static final String PATH_CATALOG_JSON = "catalogjson";
    public static final String PATH_MY_COURSES_JSON = "mycoursesjson";
    public static final String PATH_COURSE_DETAIL_JSON = "coursedetailjson";

    //table definitions -------------------------------
    //inner class that defines the table "mis CURSOS" content
    //TODO: in udacity this class is final instead of abstract,
    //see if that affect the behaviour
    public static abstract class courseEntry implements BaseColumns{
        //content provider definition
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_COURSES).build();
        public static final Uri CONTENT_URI_JSON =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_COURSES_JSON).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_COURSES;
        public static final String CONTENT_TYPE_JSON_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_COURSES_JSON;

        //table definition
        public static final String TABLE_NAME = "myCourses";
        //this is the full JSON response from the server representing
        //the full list of myCourses for the user.
        public static final String COLUMN_COURSE_LIST = "courseList";

        public static final String USER_ID = "userid";

        //content provider uri builder
        public static Uri buildMyCoursesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        //content provider uri builder to the json response
        public static Uri buildMyCoursesUriJson(long id){
            return ContentUris.withAppendedId(CONTENT_URI_JSON,id);
        }

    }

    //inner class that defines the table "DETALLE CURSO"
    public static abstract class courseDetailEntry implements BaseColumns {
        //content provider definitions
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE_DETAIL).build();
        public static final Uri CONTENT_URI_JSON =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE_DETAIL_JSON).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE_DETAIL;
        public static final String CONTENT_TYPE_JSON_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE_DETAIL_JSON;

        // table definitions
        public static final String TABLE_NAME = "courseDetail";
        //this will represent the "active" course JSON response, providing
        // back navigation from the course lesson content and the
        // course detal list. if the user
        //navigates to another course this info will be refreshed
        //from the server.
        public static final String COLUMN_COURSE_DETAIL_LIST = "courseDetailList";

        public static final String COLUMN_COURSE_ID = "courseID";

        //content provider uri builder
        // content://com.regional.autonoma.corporacion.eva/coursedetail/courseID?userID=ID"
        public static Uri buildCourseDetailUri(long courseID, long userID){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(courseID))
                    .appendQueryParameter(COLUMN_COURSE_ID, Long.toString(userID)).build();
        }
        //content provider uri builder
        // content://com.regional.autonoma.corporacion.eva/coursedetail/2
        public static Uri buildCourseDetailUri(long courseID){
            return ContentUris.withAppendedId(CONTENT_URI, courseID);
        }
        //content provider uri builder
        // content://com.regional.autonoma.corporacion.eva/coursedetailjson/2
        public static Uri buildCourseDetailUriJson(long courseID){
            return ContentUris.withAppendedId(CONTENT_URI_JSON, courseID);
        }
        public static String getCourseIdFromUri(Uri uri){
            return uri.getLastPathSegment();
        }
    }

    //inner class that defines the course catalog
    public static abstract class catalogEntry implements BaseColumns{
        //content provider definitions
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATALOG).build();
        public static final Uri CONTENT_URI_JSON =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATALOG_JSON).build();

        public static final String CONTENT_TYPE_DIR =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATALOG;
        public static final String CONTENT_TYPE_JSON_ITEM =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATALOG_JSON;

        //table definitions
        public static final String TABLE_NAME = "catalog";

        public static final String COLUMN_CATALOG_LIST = "catalogList";

        //content provider uri builder
        public static Uri buildCatalogUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        //content provider uri builder to the json response
        public static Uri buildCatalogUriJson(long id){
            return ContentUris.withAppendedId(CONTENT_URI_JSON,id);
        }
    }

}

