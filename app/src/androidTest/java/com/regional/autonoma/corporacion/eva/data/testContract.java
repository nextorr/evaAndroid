package com.regional.autonoma.corporacion.eva.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by nestor on 6/23/2016.
 * testing the eva contract methods
 */
public class testContract extends AndroidTestCase {

    private static final int USER_ID =2;
    private static final int COURSE_ID =2;

    public void testBuildMyCourse(){
        Uri myCourseUri = evaContract.courseEntry.buildMyCoursesUri(USER_ID);
        assertNotNull("Error: Null Uri returned.  check buildMyCoursesUri function" +
                        "evaContract.",
                myCourseUri);
        assertEquals("Error: user id not properly appended to the end of the Uri",
                String.valueOf(USER_ID), myCourseUri.getLastPathSegment());
        // content://com.regional.autonoma.corporacion.eva/mycourses/2
        assertEquals("Error: my courses Uri doesn't match our expected result",
                myCourseUri.toString(),
                "content://com.regional.autonoma.corporacion.eva/mycourses/" + String.valueOf(USER_ID));
    }

    public void testBuildCourseDetail (){
        Uri myCourseDetail = evaContract.courseDetailEntry.buildCourseDetailUri(COURSE_ID);
        assertNotNull("Error: Null Uri returned.  You must check buildCourseDetailUri in " +
                        "evaContract.",
                myCourseDetail);
        assertEquals("Error: course id not properly appended to the end of the Uri",
                String.valueOf(COURSE_ID), myCourseDetail.getLastPathSegment());
        // content://com.regional.autonoma.corporacion.eva/coursedetail/2
        assertEquals("Error: course detail Uri doesn't match our expected result",
                myCourseDetail.toString(),
                "content://com.regional.autonoma.corporacion.eva/coursedetail/" + String.valueOf(COURSE_ID));
    }

    public void testGetCourseIdFromUri(){
        Uri courseDetail = evaContract.courseDetailEntry.buildCourseDetailUri(COURSE_ID);
        assertEquals("Error: could not get the course ID from Uri",
                String.valueOf(COURSE_ID),
                evaContract.courseDetailEntry.getCourseIdFromUri(courseDetail));
    }

    public void testBuildCourseDetailJson (){
        Uri myCourseDetail = evaContract.courseDetailEntry.buildCourseDetailUriJson(COURSE_ID);
        assertNotNull("Error: Null Uri returned.  You must check buildCourseDetailUri in " +
                        "evaContract.",
                myCourseDetail);
        assertEquals("Error: user id not properly appended to the end of the Uri",
                String.valueOf(COURSE_ID), myCourseDetail.getLastPathSegment());
        // content://com.regional.autonoma.corporacion.eva/coursedetailjson/2
        assertEquals("Error: course detail Uri doesn't match our expected result",
                myCourseDetail.toString(),
                "content://com.regional.autonoma.corporacion.eva/coursedetailjson/" + String.valueOf(COURSE_ID));
    }

    public void testBuildCatalog(){
        //TODO: evaluate why we need a URI with user id, the catalog is always the same for the logged user.
        Uri catalogUri = evaContract.catalogEntry.buildCatalogUri(USER_ID);
        assertNotNull("Error: Null Uri returned.  check buildMyCoursesUri function" +
                        "evaContract.",
                catalogUri);
        assertEquals("Error: user id not properly appended to the end of the Uri",
                String.valueOf(USER_ID), catalogUri.getLastPathSegment());
        // content://com.regional.autonoma.corporacion.eva/catalog/2
        assertEquals("Error: my courses Uri doesn't match our expected result",
                catalogUri.toString(),
                "content://com.regional.autonoma.corporacion.eva/catalog/" + String.valueOf(USER_ID));
    }

    public void testBuildCatalogJson(){
        //TODO: evaluate why we need a URI with user id, the catalog is always the same for the logged user.
        Uri catalogUri = evaContract.catalogEntry.buildCatalogUriJson(USER_ID);
        assertNotNull("Error: Null Uri returned.  check buildMyCoursesUri function" +
                        "evaContract.",
                catalogUri);
        assertEquals("Error: user id not properly appended to the end of the Uri",
                String.valueOf(USER_ID), catalogUri.getLastPathSegment());
        // content://com.regional.autonoma.corporacion.eva/catalogjson/2
        assertEquals("Error: my courses Uri doesn't match our expected result",
                catalogUri.toString(),
                "content://com.regional.autonoma.corporacion.eva/catalogjson/" + String.valueOf(USER_ID));
    }
}
