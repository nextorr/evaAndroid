package com.regional.autonoma.corporacion.eva.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by nestor on 6/28/2016.
 * testing the eva content provider
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    //test that the provider is registered correctly
    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();
        ComponentName cn = new ComponentName(mContext.getPackageName(), evaProvider.class.getName());
        try{
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(cn,0);
            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: evaProvider registered with autority " + providerInfo.authority +
                "instead of authority " + evaContract.CONTENT_AUTHORITY,
                    providerInfo.authority, evaContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e){
            //the provider isn registered correctly
            assertTrue("Error: evaProvider not registered at " + mContext.getPackageName(),false);
        }
    }

    public void testGetType(){
        final int COURSEID= 2;
        // content://authority/mycoursesjson
        String typeStr = mContext.getContentResolver().getType(evaContract.courseEntry.CONTENT_URI_JSON);
        //this should return a item type
        //vnd.android.cursor.item/authority/mycoursesjson
        assertEquals("Error: the eva contract my courses json CONTENT URI should return item type",
                evaContract.courseEntry.CONTENT_TYPE_JSON_ITEM, typeStr);

        // content://authority/coursedetailjson
        typeStr = mContext.getContentResolver().getType(evaContract.courseDetailEntry.CONTENT_URI_JSON);
        //this should return a item type
        //vnd.android.cursor.item/authority/mycoursesjson
        assertEquals("Error: the eva contract course detail json CONTENT URI should return item type",
                evaContract.courseDetailEntry.CONTENT_TYPE_JSON_ITEM, typeStr);
        typeStr = mContext.getContentResolver().getType(evaContract.courseDetailEntry.buildCourseDetailUri(COURSEID));
        assertEquals("Error: the eva contract course detail courseid json CONTENT URI should return DIR type",
                evaContract.courseDetailEntry.CONTENT_TYPE_DIR, typeStr);

        // content://authority/catalogjson
        typeStr = mContext.getContentResolver().getType(evaContract.catalogEntry.CONTENT_URI_JSON);
        //this should return a item type
        //vnd.android.cursor.item/authority/catalogjson
        assertEquals("Error: the eva contract catalog json CONTENT URI should return item type",
                evaContract.catalogEntry.CONTENT_TYPE_JSON_ITEM, typeStr);


        //TODO: test the other cases,give special atention to the cursor ones.

    }

    //use the content provider to write an read from the database.
    //remember, the idea is to store just enough information to enable navigation in the app
    public void testUpdateMyCoursesJson(){
        ContentValues courseValues = TestUtilities.createMyCoursesValuesJSON();

        Uri courseUri = mContext.getContentResolver().insert(
                evaContract.courseEntry.CONTENT_URI_JSON,
                courseValues);
        Long myCourseRowId = ContentUris.parseId(courseUri);

        //verify that we got a row back
        assertTrue("We didnt get a row back when inserting data using the provider",
                myCourseRowId != -1);
        Log.d(LOG_TAG, " New row ID: " + myCourseRowId);

        //simulate updated values
        //then test the query, update and the notification infraestructure
        ContentValues updatedValues = TestUtilities.createMyCoursesUpdateJSON();

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor courseCursor = mContext.getContentResolver().query(
                evaContract.courseEntry.CONTENT_URI_JSON,
                null,
                null,
                null,
                null
        );

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        courseCursor.registerContentObserver(tco);

        //remember, update deletes all previous data and insert the new one,
        //test for that behaviour
        int count = mContext.getContentResolver().update(
                evaContract.courseEntry.CONTENT_URI_JSON,
                updatedValues,
                null,
                null
        );
        assertEquals("check the update method in the content provider",count, 2);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        courseCursor.unregisterContentObserver(tco);
        courseCursor.close();

        //now check we get the data we expect back
        Cursor cursor = mContext.getContentResolver().query(
                evaContract.courseEntry.CONTENT_URI_JSON,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("Error validating update or query", cursor, updatedValues);
        cursor.close();
    }

    public void testQueryMyCoursesCursor(){
        ContentValues courseValues = TestUtilities.createMyCoursesValuesJSON();

        Uri courseUri = mContext.getContentResolver().insert(
                evaContract.courseEntry.CONTENT_URI_JSON,
                courseValues);
        Long myCourseRowId = ContentUris.parseId(courseUri);

        //verify that we got a row back
        assertTrue("We didnt get a row back when inserting data using the provider",
                myCourseRowId != -1);
        Log.d(LOG_TAG, " New row ID: " + myCourseRowId);

        // Create a cursor with the parsed data
        Cursor courseCursor = mContext.getContentResolver().query(
                evaContract.courseEntry.CONTENT_URI_JSON,
                null,
                null,
                null,
                null
        );
        //use this to see the cursor contet as a string
        //String dbug = DatabaseUtils.dumpCursorToString(courseCursor);
        courseCursor.close();
    }

    public void testUpdateCourseDetailJson(){
        final int COURSEID= 2;
        ContentValues courseDetailValues = TestUtilities.createCourseDetailValuesJSON(COURSEID);
        //first delete all the data from the database,
        mContext.getContentResolver().delete(evaContract.courseDetailEntry.CONTENT_URI_JSON, null,null);

        Uri courseDetailUri = mContext.getContentResolver().insert(
                evaContract.courseDetailEntry.CONTENT_URI_JSON,
                courseDetailValues);
        Long courseDetailRowId = ContentUris.parseId(courseDetailUri);

        //verify that we got a row back
        assertTrue("We didnt get a row back when inserting data using the provider in the course detail table",
                courseDetailRowId != -1);
        Log.d(LOG_TAG, " New row ID: " + courseDetailRowId);

        //simulate updated values
        //then test the query, update and the notification infraestructure
        ContentValues updatedValues = TestUtilities.createCourseDetailUpdateJSON();

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor courseDetailCursor = mContext.getContentResolver().query(
                evaContract.courseDetailEntry.buildCourseDetailUri(COURSEID),
                null,
                null,
                null,
                null
        );

        assertNotNull("course detail query with course ID returned no data", courseDetailCursor);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        courseDetailCursor.registerContentObserver(tco);

        //remember, update deletes all previous data and insert the new one,
        //test for that behaviour
        int count = mContext.getContentResolver().update(
                evaContract.courseDetailEntry.buildCourseDetailUriJson(COURSEID),
                updatedValues,
                null,
                null
        );
        assertEquals("check the update method in the content provider",1, count);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        courseDetailCursor.unregisterContentObserver(tco);
        courseDetailCursor.close();

        //now check we get the data we expect back
        Cursor cursor = mContext.getContentResolver().query(
                evaContract.courseDetailEntry.buildCourseDetailUriJson(COURSEID),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("Error validating courde detail update or query", cursor, updatedValues);
        cursor.close();
    }

    public void testQueryCoursesDetailCursor(){
        final int COURSEID =2;
        ContentValues courseValues = TestUtilities.createCourseDetailValuesJSON(COURSEID);

        Uri courseUri = mContext.getContentResolver().insert(
                evaContract.courseDetailEntry.CONTENT_URI_JSON,
                courseValues);
        Long myCourseRowId = ContentUris.parseId(courseUri);

        //verify that we got a row back
        assertTrue("We didnt get a row back when inserting data using the provider",
                myCourseRowId != -1);
        Log.d(LOG_TAG, " New row ID: " + myCourseRowId);

        // Create a cursor with the parsed data
        Cursor courseCursor = mContext.getContentResolver().query(
                evaContract.courseDetailEntry.buildCourseDetailUri(COURSEID),
                null,
                null,
                null,
                null
        );
        //use this to see the cursor contet as a string
        //String dbug = DatabaseUtils.dumpCursorToString(courseCursor);
        courseCursor.close();
    }

    public void testUpdateCatalogJson (){
        ContentValues catalogValues = TestUtilities.createCatalogValuesJSON();

        Uri catalogUri = mContext.getContentResolver().insert(
                evaContract.catalogEntry.CONTENT_URI_JSON,
                catalogValues);
        Long catalogRowId = ContentUris.parseId(catalogUri);

        //verify that we got a row back
        assertTrue("We didnt get a row back when inserting data using the provider",
                catalogRowId != -1);
        Log.d(LOG_TAG, " New row ID: " + catalogRowId);

        //simulate updated values
        //then test the query, update and the notification infraestructure
        ContentValues updatedValues = TestUtilities.createCatalogUpdateJSON();

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor catalogCursor = mContext.getContentResolver().query(
                evaContract.catalogEntry.CONTENT_URI_JSON,
                null,
                null,
                null,
                null
        );

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        catalogCursor.registerContentObserver(tco);

        //remember, update deletes all previous data and insert the new one,
        //test for that behaviour
        int count = mContext.getContentResolver().update(
                evaContract.catalogEntry.CONTENT_URI_JSON,
                updatedValues,
                null,
                null
        );
        assertEquals("check the update method in the content provider", count, 2);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        catalogCursor.unregisterContentObserver(tco);
        catalogCursor.close();

        //now check we get the data we expect back
        Cursor cursor = mContext.getContentResolver().query(
                evaContract.catalogEntry.CONTENT_URI_JSON,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("Error validating update or query", cursor, updatedValues);
        cursor.close();
    }

    public void testQueryCatalogCursor(){
        ContentValues catalogValues = TestUtilities.createCatalogValuesJSON();

        Uri catalogUri = mContext.getContentResolver().insert(
                evaContract.catalogEntry.CONTENT_URI_JSON,
                catalogValues);
        Long catalogRowId = ContentUris.parseId(catalogUri);

        //verify that we got a row back
        assertTrue("We didnt get a row back when inserting data using the provider",
                catalogRowId != -1);
        Log.d(LOG_TAG, " New row ID: " + catalogRowId);

        // Create a cursor with the parsed data
        Cursor catalogCursor = mContext.getContentResolver().query(
                evaContract.catalogEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //use this to see the cursor contet as a string
        //String dbug = DatabaseUtils.dumpCursorToString(courseCursor);
        catalogCursor.close();
    }
}
