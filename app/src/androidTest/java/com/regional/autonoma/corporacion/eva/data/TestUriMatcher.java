package com.regional.autonoma.corporacion.eva.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nestor on 6/23/2016.
 * testing the uri matcher
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final int USER_ID = 2;
    private static final int COURSE_ID = 2;

    private static final Uri TEST_MYCOURSE_JSON = evaContract.courseEntry.CONTENT_URI_JSON;
    private static final Uri TEST_COURSE_DETAIL_JSON = evaContract.courseDetailEntry.CONTENT_URI_JSON;
    private static final Uri TEST_CATALOG_JSON = evaContract.catalogEntry.CONTENT_URI_JSON;

    private static final Uri TEST_MYCOURSE = evaContract.courseEntry.CONTENT_URI;
    private static final Uri TEST_COURSE_DETAIL = evaContract.courseDetailEntry.CONTENT_URI;
    private static final Uri TEST_CATALOG = evaContract.catalogEntry.CONTENT_URI;

    private static final Uri TEST_COURSE_DETAIL_COURSEID = evaContract.courseDetailEntry.buildCourseDetailUri(COURSE_ID);

    private static final Uri TEST_MYCOURSE_USERID_JSON = evaContract.courseEntry.buildMyCoursesUriJson(USER_ID);
    private static final Uri TEST_CATALOG_USERID_JSON = evaContract.catalogEntry.buildCatalogUriJson(USER_ID);

    //remember the Uri matcher job is to relate a integer ID with the adress
    public void testUriMatcher(){
        UriMatcher testMatcher = evaProvider.buildUriMatcher();

        assertEquals("Error: The COURSE JSON URI was matched incorrectly.",
                testMatcher.match(TEST_MYCOURSE_JSON), evaProvider.MY_COURSES_JSON);
        assertEquals("Error: The COURSE URI was matched incorrectly.",
                testMatcher.match(TEST_MYCOURSE), evaProvider.ALL_MY_COURSES);
        assertEquals("Error: The COURSE URI WITH USER ID was matched incorrectly.",
                testMatcher.match(TEST_MYCOURSE_USERID_JSON), evaProvider.MY_COURSES_JSON_USERID);

        assertEquals("Error: The COURSE DETAIL JSON URI was matched incorrectly.",
                testMatcher.match(TEST_COURSE_DETAIL_JSON), evaProvider.COURSE_DETAIL_JSON);
        assertEquals("Error: The COURSE DETAIL URI was matched incorrectly.",
                testMatcher.match(TEST_COURSE_DETAIL), evaProvider.ALL_COURSE_DETAIL);
        assertEquals("Error: The COURSE DETAIL URI WITH USER ID was matched incorrectly.",
                testMatcher.match(TEST_COURSE_DETAIL_COURSEID), evaProvider.ALL_COURSE_DETAIL_COURSEID);

        assertEquals("Error: The CATALOG JSON URI was matched incorrectly.",
                testMatcher.match(TEST_CATALOG_JSON), evaProvider.CATALOG_JSON);
        assertEquals("Error: The CATALOG URI was matched incorrectly.",
                testMatcher.match(TEST_CATALOG), evaProvider.ALL_CATALOG);
        assertEquals("Error: The CATALOG URI WITH USER ID was matched incorrectly.",
                testMatcher.match(TEST_CATALOG_USERID_JSON), evaProvider.CATALOG_JSON_USERID);


    }
}
