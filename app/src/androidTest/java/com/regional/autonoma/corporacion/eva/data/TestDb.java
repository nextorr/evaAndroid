package com.regional.autonoma.corporacion.eva.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by nestor on 6/21/2016.
 * test the database is built in the correct form
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    //TODO: this does delete the original DB? where does the test execute?
    //anyway it does not matter because we are not saving user data here.
    void deleteTheDatabase() {
        mContext.deleteDatabase(evaDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable{
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(evaContract.courseEntry.TABLE_NAME);
        tableNameHashSet.add(evaContract.courseDetailEntry.TABLE_NAME);
        tableNameHashSet.add(evaContract.catalogEntry.TABLE_NAME);

        // delete an existing database to start clean
        mContext.deleteDatabase(evaDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new evaDbHelper(this.mContext).getWritableDatabase();
        //test if the database creates correctly
        assertEquals(true, db.isOpen());

        // have we created the tables?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            //remove entry matching tablename
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain any of the eva model tables
        assertTrue("Error: Your database was created but the eva model tables where not created",
                tableNameHashSet.isEmpty());

        // ----now, do our tables contain the correct columns?-----
        c = db.rawQuery("PRAGMA table_info(" + evaContract.courseEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> courseColumnHashSet = new HashSet<String>();
        courseColumnHashSet.add(evaContract.courseEntry._ID);
        courseColumnHashSet.add(evaContract.courseEntry.COLUMN_COURSE_LIST);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            courseColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required course
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required course entry columns",
                courseColumnHashSet.isEmpty());

        //-----check that course detail have the correct columns-----
        c = db.rawQuery("PRAGMA table_info(" + evaContract.courseDetailEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> courseDetailColumnHashSet = new HashSet<String>();
        courseDetailColumnHashSet.add(evaContract.courseDetailEntry._ID);
        courseDetailColumnHashSet.add(evaContract.courseDetailEntry.COLUMN_COURSE_ID);
        courseDetailColumnHashSet.add(evaContract.courseDetailEntry.COLUMN_COURSE_DETAIL_LIST);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            courseDetailColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required course detail entry columns",
                courseDetailColumnHashSet.isEmpty());

        // ----check that catalog have the correct columns----
        c = db.rawQuery("PRAGMA table_info(" + evaContract.catalogEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> catalogColumnHashSet = new HashSet<String>();
        catalogColumnHashSet.add(evaContract.catalogEntry._ID);
        catalogColumnHashSet.add(evaContract.catalogEntry.COLUMN_CATALOG_LIST);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            catalogColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required catalog entry columns",
                catalogColumnHashSet.isEmpty());

        c.close();
        db.close();
    }

    //here we test that we can insert and query the myCourses table
    public void testMyCoursesTable(){
        evaDbHelper dbHelper = new evaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMyCoursesValuesJSON();

        long courseRowId;
        courseRowId = db.insert(evaContract.courseEntry.TABLE_NAME,null,testValues);

        //test that we got a row back
        assertTrue(courseRowId != -1);

        //now verify that we can read the data.
        Cursor cursor = db.query(
                evaContract.courseEntry.TABLE_NAME,
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        //return courseRowId;
    }

    public void testCourseDetailTable(){
        //at this point the tables are not related here, to keep the model simple
        evaDbHelper dbHelper = new evaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues courseDetailValues = TestUtilities.createCourseDetailValuesJSON();
        long courseDetailRowID = db.insert(evaContract.courseDetailEntry.TABLE_NAME, null, courseDetailValues);
        assertTrue(courseDetailRowID != -1);

        Cursor courseDetailCursor = db.query(
                evaContract.courseDetailEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", courseDetailCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                courseDetailCursor, courseDetailValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                courseDetailCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        courseDetailCursor.close();
        dbHelper.close();
    }

    public void testCatalogTable(){
        //at this point the tables are not related here, to keep the model simple
        evaDbHelper dbHelper = new evaDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues catalogValues = TestUtilities.createCatalogValuesJSON();
        Long catalogRowID = db.insert(evaContract.catalogEntry.TABLE_NAME, null, catalogValues);
        assertTrue("could not insert into the catalog table",catalogRowID != -1);

        Cursor catalogCursor = db.query(
                evaContract.catalogEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", catalogCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                catalogCursor, catalogValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                catalogCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        catalogCursor.close();
        dbHelper.close();




    }
}
