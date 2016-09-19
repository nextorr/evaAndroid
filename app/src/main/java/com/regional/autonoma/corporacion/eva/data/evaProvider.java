package com.regional.autonoma.corporacion.eva.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.regional.autonoma.corporacion.eva.Communication.Parser;

/**
 * Created by nestor on 6/23/2016.
 * content provider defining the eva data interactions
 */
public class evaProvider extends ContentProvider {

    //constants used by the Uri matcher
    private static final UriMatcher evaUriMatcher = buildUriMatcher();
    private evaDbHelper mOpenHelper;

    static final int ALL_MY_COURSES = 100;
    static final int ALL_COURSE_DETAIL = 200;
    static final int ALL_CATALOG = 300;
    static final int MY_COURSES_JSON = 101;
    static final int COURSE_DETAIL_JSON = 201;
    static final int CATALOG_JSON = 301;
    static final int MY_COURSES_JSON_USERID = 102;
    static final int COURSE_DETAIL_JSON_COURSEID = 202;
    static final int CATALOG_JSON_USERID = 302;
    static final int ALL_COURSE_DETAIL_COURSEID = 204;

    //selection arguments
    private static final String sCourseDetailSelection = evaContract.courseDetailEntry.TABLE_NAME + "." +
            evaContract.courseDetailEntry.COLUMN_COURSE_ID + " = ?";

    static UriMatcher buildUriMatcher(){
        //we start with no match
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = evaContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        //at this point just get all the table data.
        matcher.addURI(authority,evaContract.PATH_MY_COURSES, ALL_MY_COURSES);
        // contetn://authority/mycourses/json
        matcher.addURI(authority,evaContract.PATH_MY_COURSES_JSON, MY_COURSES_JSON);
        // contetn://authority/mycoursesjson/userid
        matcher.addURI(authority,evaContract.PATH_MY_COURSES_JSON + "/#", MY_COURSES_JSON_USERID);
        //content://authority/coursedetail/
        matcher.addURI(authority,evaContract.PATH_COURSE_DETAIL, ALL_COURSE_DETAIL);
        //content://authority/coursedetail/courseid
        matcher.addURI(authority,evaContract.PATH_COURSE_DETAIL + "/#", ALL_COURSE_DETAIL_COURSEID);
        // contetn://authority/coursedetailjson
        matcher.addURI(authority,evaContract.PATH_COURSE_DETAIL_JSON, COURSE_DETAIL_JSON);
        // contetn://authority/coursedetailjson/userid
        matcher.addURI(authority,evaContract.PATH_COURSE_DETAIL_JSON + "/#", COURSE_DETAIL_JSON_COURSEID);
        //whatever is in this table, send it when requested with this Uri.
        matcher.addURI(authority,evaContract.PATH_CATALOG, ALL_CATALOG);
        // contetn://authority/coursedetail/json
        matcher.addURI(authority,evaContract.PATH_CATALOG_JSON, CATALOG_JSON);
        // contetn://authority/coursedetailjson/userid
        matcher.addURI(authority,evaContract.PATH_CATALOG_JSON + "/#", CATALOG_JSON_USERID);

        return matcher;
    }

    //now define the data manipulation methods

    @Override
    public boolean onCreate() {
        mOpenHelper = new evaDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = evaUriMatcher.match(uri);

        //the idea of this is to set the return type of the final cursor
        //IMPORTANT: the database return 1 record, the JSON data.
        switch (match) {
            case ALL_MY_COURSES:
                //interesting: no break needed as the fucntions returns here
                return evaContract.courseEntry.CONTENT_TYPE_DIR;
            case MY_COURSES_JSON:
                return evaContract.courseEntry.CONTENT_TYPE_JSON_ITEM;
            case MY_COURSES_JSON_USERID:
                return evaContract.courseEntry.CONTENT_TYPE_JSON_ITEM;
            case ALL_COURSE_DETAIL:
                return evaContract.courseDetailEntry.CONTENT_TYPE_DIR;
            case ALL_COURSE_DETAIL_COURSEID:
                return evaContract.courseDetailEntry.CONTENT_TYPE_DIR;
            case COURSE_DETAIL_JSON:
                return evaContract.courseDetailEntry.CONTENT_TYPE_JSON_ITEM;
            case COURSE_DETAIL_JSON_COURSEID:
                return evaContract.courseDetailEntry.CONTENT_TYPE_JSON_ITEM;
            case ALL_CATALOG:
                return evaContract.catalogEntry.CONTENT_TYPE_DIR;
            case CATALOG_JSON:
                return evaContract.catalogEntry.CONTENT_TYPE_JSON_ITEM;
            case CATALOG_JSON_USERID:
                return evaContract.catalogEntry.CONTENT_TYPE_JSON_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        Cursor internalCursor;
        String jsonStr;
        String[] qSelection;

        switch(evaUriMatcher.match(uri)){
            case ALL_MY_COURSES:
                //TODO: test this case
                //parse the my courses information
                internalCursor = mOpenHelper.getReadableDatabase().query(
                        evaContract.courseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                //index 1 contains the json string
                if(internalCursor.moveToFirst()){
                    jsonStr = internalCursor.getString(1);
                    //parse the stored jsonString

                    //retCursor = Parser.parseAllCourses(jsonStr);
                    retCursor = Parser.parseUserCourseEnrollments(jsonStr);
                    //use this to see the cursor contet as a string
                    //String dbug = DatabaseUtils.dumpCursorToString(matrixCursor);
                }else{
                    retCursor = internalCursor;
                }

                break;
            case MY_COURSES_JSON:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        evaContract.courseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case ALL_COURSE_DETAIL:
                //this is undefined behaviour
                //per definition should return ALL de course details (lesson) defined in the database.
                //but that does not make sense, the users needs the course detail(lesson) for a given courseID
                //however keep the method to test
                break;
            case ALL_COURSE_DETAIL_COURSEID:
                qSelection = new String[]{evaContract.courseDetailEntry.getCourseIdFromUri(uri)};
                internalCursor =  mOpenHelper.getReadableDatabase().query(
                        evaContract.courseDetailEntry.TABLE_NAME,
                        projection,
                        sCourseDetailSelection,
                        qSelection,
                        null,
                        null,
                        null
                );
                //index 2 contains the Json String
                if(internalCursor.moveToFirst()){
                    jsonStr = internalCursor.getString(2);
                    //parse the stored jsonstring
                    //retCursor = Parser.parseAllLessons(jsonStr);
                    retCursor = Parser.parseUserLessonDetail(jsonStr);
                    //use this to see the cursor contet as a string
                    //String dbug1 = DatabaseUtils.dumpCursorToString(matrixCursor);
                }else{
                    retCursor = internalCursor;
                }
                break;
            case COURSE_DETAIL_JSON:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        evaContract.courseDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case COURSE_DETAIL_JSON_COURSEID:
                //use this to test that the query returns just one entry
                qSelection = new String[]{evaContract.courseDetailEntry.getCourseIdFromUri(uri)};
                retCursor =  mOpenHelper.getReadableDatabase().query(
                        evaContract.courseDetailEntry.TABLE_NAME,
                        projection,
                        sCourseDetailSelection,
                        qSelection,
                        null,
                        null,
                        null
                );
                break;
            case ALL_CATALOG:
                //the query parameters are null because here we only need one record
                internalCursor = mOpenHelper.getReadableDatabase().query(
                        evaContract.catalogEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                //TODO: parse the response and return the matrix cursor
                //index 1 contains the Json String
                if(internalCursor.moveToFirst()){
                    jsonStr = internalCursor.getString(1);
                    //parse the stored jsonstring
                    //retCursor = Parser.parseAllLessons(jsonStr);
                    retCursor = Parser.parseUserCourseCatalog(jsonStr, selection);
                    //use this to see the cursor contet as a string
                    //String dbug1 = DatabaseUtils.dumpCursorToString(matrixCursor);
                } else{
                    retCursor = internalCursor;
                }
                break;
            case CATALOG_JSON:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        evaContract.catalogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = evaUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MY_COURSES_JSON:
                //first delete all the database data, as we need a fresh start
                db.delete(evaContract.courseEntry.TABLE_NAME,null,null);
                Long _id = db.insert(evaContract.courseEntry.TABLE_NAME,null,values);
                if ( _id > 0 )
                    returnUri = evaContract.courseEntry.buildMyCoursesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);

                //notify that this 2 URLS changed, since content_uri just parses the data we inserted
                //throgh contet_uri_json
                getContext().getContentResolver().notifyChange(evaContract.courseEntry.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case COURSE_DETAIL_JSON:
                //db.delete(evaContract.courseDetailEntry.TABLE_NAME,null,null);
                Long _id2 = db.insert(evaContract.courseDetailEntry.TABLE_NAME,null,values);
                if ( _id2 > 0 )
                    returnUri = evaContract.courseDetailEntry.buildCourseDetailUriJson(_id2);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                //notify that this 2 URLS changed, since content_uri just parses the data we inserted
                //throgh contet_uri_json
                getContext().getContentResolver().notifyChange(evaContract.courseDetailEntry.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case CATALOG_JSON:
                //first, delete all the database data, we are using it as a temp store
                db.delete(evaContract.catalogEntry.TABLE_NAME, null, null);
                Long _id4 = db.insert(evaContract.catalogEntry.TABLE_NAME, null, values);
                if(_id4 > 0){
                    returnUri = evaContract.catalogEntry.buildCatalogUri(_id4);
                } else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                //notify that this 2 URLS changed, since content_uri just parses the data we inserted
                //throgh contet_uri_json
                getContext().getContentResolver().notifyChange(evaContract.catalogEntry.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = evaUriMatcher.match(uri);
        Uri returnUri;
        int rowsDeleted;

        switch (match){
            case MY_COURSES_JSON:
                rowsDeleted = db.delete(evaContract.courseEntry.TABLE_NAME, null,null);
                break;
            case COURSE_DETAIL_JSON:
                rowsDeleted = db.delete(evaContract.courseDetailEntry.TABLE_NAME,null,null);
                break;
            case CATALOG_JSON:
                rowsDeleted = db.delete(evaContract.catalogEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = evaUriMatcher.match(uri);
        Uri returnUri;
        int rowsUpdated;
        String[] uSelection;
        int courseID;

        switch (match){
            case MY_COURSES_JSON:
                //first delete all the database data, as we need a fresh start
                rowsUpdated = db.delete(evaContract.courseEntry.TABLE_NAME,null,null);
                Long _id = db.insert(evaContract.courseEntry.TABLE_NAME,null,values);
                //increment in one the rowsupdated if the insert is succesfull
                if ( _id > 0 )
                    rowsUpdated ++;
                    //returnUri = evaContract.courseEntry.buildMyCoursesUri(_id);
                else
                    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            case COURSE_DETAIL_JSON:
                //this needs to update the correct row with the given courseID using the selection args
                rowsUpdated = db.update(evaContract.courseDetailEntry.TABLE_NAME, values, selection,selectionArgs);
                if(rowsUpdated <= 0)
                    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            case COURSE_DETAIL_JSON_COURSEID:
                //this builds the query given the URI
                courseID = Integer.parseInt(uri.getLastPathSegment());
                uSelection = new String[]{evaContract.courseDetailEntry.getCourseIdFromUri(uri)};
                rowsUpdated = db.update(evaContract.courseDetailEntry.TABLE_NAME, values, sCourseDetailSelection, uSelection);
                if(rowsUpdated <= 0)
                    throw new android.database.SQLException("Failed to update row into " + uri);
                else
                    getContext().getContentResolver().notifyChange(evaContract.courseDetailEntry.buildCourseDetailUri(courseID), null);
                break;
            case CATALOG_JSON:
                //first delete all the database data, we are using it as a temp store
                rowsUpdated = db.delete(evaContract.catalogEntry.TABLE_NAME, null, null);
                Long _id3 = db.insert(evaContract.catalogEntry.TABLE_NAME, null, values);
                if (_id3 > 0)
                    rowsUpdated++;
                else
                    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
