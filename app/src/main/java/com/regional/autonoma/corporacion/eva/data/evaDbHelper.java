package com.regional.autonoma.corporacion.eva.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.regional.autonoma.corporacion.eva.data.evaContract.courseEntry;
import com.regional.autonoma.corporacion.eva.data.evaContract.courseDetailEntry;
import com.regional.autonoma.corporacion.eva.data.evaContract.catalogEntry;

/**
 * Created by nestor on 6/12/2016.
 * crates de database
 */
public class evaDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    static final String DATABASE_NAME = "eva.db";
    public evaDbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_COURSE_TABLE = "CREATE TABLE " + courseEntry.TABLE_NAME + " (" +
                courseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                courseEntry.COLUMN_COURSE_LIST  + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_COURSE_DETAIL_TABLE = "CREATE TABLE " + courseDetailEntry.TABLE_NAME + " (" +
                courseDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                courseDetailEntry.COLUMN_COURSE_ID +" INTEGER NOT NULL UNIQUE, " +
                courseDetailEntry.COLUMN_COURSE_DETAIL_LIST + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_CATALOG_TABLE = "CREATE TABLE " + catalogEntry.TABLE_NAME + " (" +
                catalogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                catalogEntry.COLUMN_CATALOG_LIST + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_COURSE_DETAIL_TABLE);
        db.execSQL(SQL_CREATE_COURSE_TABLE);
        db.execSQL(SQL_CREATE_CATALOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //here just drop and create again the database
        db.execSQL("DROP TABLE IF EXISTS " + catalogEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + courseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + courseDetailEntry.TABLE_NAME);
        onCreate(db);
    }
}
