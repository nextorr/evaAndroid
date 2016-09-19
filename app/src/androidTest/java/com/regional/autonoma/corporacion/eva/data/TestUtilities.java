package com.regional.autonoma.corporacion.eva.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.regional.autonoma.corporacion.eva.data.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by nestor on 6/21/2016.
 */
public class TestUtilities extends AndroidTestCase {
    private static final int COURSEID = 2;

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    static ContentValues createMyCoursesValuesJSON (){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.courseEntry.COLUMN_COURSE_LIST, "[{\"CourseID\":2,\"title\":\"CURSO MANEJO SAE (pruebas)\",\"description\":\"Aprenda el funcionamiento del sistema de administracion de expedientes de la CAR SAE\",\"Chapters\":null,\"Files\":null},{\"CourseID\":5,\"title\":\"CURSO MANEJO SIDCAR (pruebas)\",\"description\":\"Aprenda el funcionamiento del sistema documental de la CAR SIDCAR\",\"Chapters\":null,\"Files\":null},{\"CourseID\":6,\"title\":\"CURSO SISTEMA DE ADMINISTRACION DE EXPEDIENTES - SAE\",\"description\":\"Aprenda el funcionamiento del  SAE\",\"Chapters\":null,\"Files\":null}]");
        return testValues;
    }

    static ContentValues createMyCoursesUpdateJSON (){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.courseEntry.COLUMN_COURSE_LIST, "[{\"CourseID\":2,\"title\":\"CURSO MANEJO SAE (pruebas actualizacion)\",\"description\":\"Aprenda el funcionamiento del sistema de administracion de expedientes de la CAR SAE\",\"Chapters\":null,\"Files\":null},{\"CourseID\":5,\"title\":\"CURSO MANEJO SIDCAR (pruebas actualizacion)\",\"description\":\"Aprenda el funcionamiento del sistema documental de la CAR SIDCAR\",\"Chapters\":null,\"Files\":null},{\"CourseID\":6,\"title\":\"CURSO SISTEMA DE ADMINISTRACION DE EXPEDIENTES - SAE\",\"description\":\"Aprenda el funcionamiento del  SAE\",\"Chapters\":null,\"Files\":null}]");
        return testValues;
    }

    static ContentValues createCourseDetailValuesJSON (){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_ID, COURSEID);
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_DETAIL_LIST, "[{\"ChapterID\":10,\"title\":\"Asignación Expediente\",\"index\":2,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":21,\"title\":\"Como recibirlo\",\"description\":\"El expediente asigando se vera reflejado en su sesion en la parte izquierda de Mis Actividades Pendientes o en Expedinentes Enviados y Recibidos\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":10,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":11,\"title\":\"Que es el SAE\",\"index\":1,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":22,\"title\":\"Sistema de Administración Expedientes - SAE\",\"description\":\"Este sistema involucra a los usuarios en la administración de expedientes, optimiza y agiliza el proceso que a diario realizan en torno al registro, control y seguimiento de expedientes. \",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":11,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":12,\"title\":\"manejo basico del sistema\",\"index\":3,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":23,\"title\":\"ingreso al sistema\",\"description\":\"Aprenda como ubicar el SAE y utilizar su usuario y contraseña\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":12,\"questions\":null,\"Files\":null}],\"Files\":null}]");
        return testValues;
    }

    static ContentValues createCourseDetailValuesJSON (int courseID){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_ID, courseID);
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_DETAIL_LIST, "[{\"ChapterID\":10,\"title\":\"Asignación Expediente\",\"index\":2,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":21,\"title\":\"Como recibirlo\",\"description\":\"El expediente asigando se vera reflejado en su sesion en la parte izquierda de Mis Actividades Pendientes o en Expedinentes Enviados y Recibidos\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":10,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":11,\"title\":\"Que es el SAE\",\"index\":1,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":22,\"title\":\"Sistema de Administración Expedientes - SAE\",\"description\":\"Este sistema involucra a los usuarios en la administración de expedientes, optimiza y agiliza el proceso que a diario realizan en torno al registro, control y seguimiento de expedientes. \",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":11,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":12,\"title\":\"manejo basico del sistema\",\"index\":3,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":23,\"title\":\"ingreso al sistema\",\"description\":\"Aprenda como ubicar el SAE y utilizar su usuario y contraseña\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":12,\"questions\":null,\"Files\":null}],\"Files\":null}]");
        return testValues;
    }

    static ContentValues createCourseDetailUpdateJSON (){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_ID, COURSEID);
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_DETAIL_LIST, "[{\"ChapterID\":10,\"title\":\"Asignación Expediente (prueba actualizacion)\",\"index\":2,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":21,\"title\":\"Como recibirlo\",\"description\":\"El expediente asigando se vera reflejado en su sesion en la parte izquierda de Mis Actividades Pendientes o en Expedinentes Enviados y Recibidos\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":10,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":11,\"title\":\"Que es el SAE\",\"index\":1,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":22,\"title\":\"Sistema de Administración Expedientes - SAE\",\"description\":\"Este sistema involucra a los usuarios en la administración de expedientes, optimiza y agiliza el proceso que a diario realizan en torno al registro, control y seguimiento de expedientes. \",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":11,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":12,\"title\":\"manejo basico del sistema\",\"index\":3,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":23,\"title\":\"ingreso al sistema\",\"description\":\"Aprenda como ubicar el SAE y utilizar su usuario y contraseña\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":12,\"questions\":null,\"Files\":null}],\"Files\":null}]");
        return testValues;
    }

    static ContentValues createCourseDetailUpdateJSON (int courseID){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_ID, courseID);
        testValues.put(evaContract.courseDetailEntry.COLUMN_COURSE_DETAIL_LIST, "[{\"ChapterID\":10,\"title\":\"Asignación Expediente (prueba actualizacion)\",\"index\":2,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":21,\"title\":\"Como recibirlo\",\"description\":\"El expediente asigando se vera reflejado en su sesion en la parte izquierda de Mis Actividades Pendientes o en Expedinentes Enviados y Recibidos\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":10,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":11,\"title\":\"Que es el SAE\",\"index\":1,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":22,\"title\":\"Sistema de Administración Expedientes - SAE\",\"description\":\"Este sistema involucra a los usuarios en la administración de expedientes, optimiza y agiliza el proceso que a diario realizan en torno al registro, control y seguimiento de expedientes. \",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":11,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":12,\"title\":\"manejo basico del sistema\",\"index\":3,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":23,\"title\":\"ingreso al sistema\",\"description\":\"Aprenda como ubicar el SAE y utilizar su usuario y contraseña\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":12,\"questions\":null,\"Files\":null}],\"Files\":null}]");
        return testValues;
    }

    static ContentValues createCatalogValuesJSON(){
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.catalogEntry.COLUMN_CATALOG_LIST, "[{\"ChapterID\":10,\"title\":\"Asignación Expediente (prueba del catalogo)\",\"index\":2,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":21,\"title\":\"Como recibirlo\",\"description\":\"El expediente asigando se vera reflejado en su sesion en la parte izquierda de Mis Actividades Pendientes o en Expedinentes Enviados y Recibidos\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":10,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":11,\"title\":\"Que es el SAE\",\"index\":1,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":22,\"title\":\"Sistema de Administración Expedientes - SAE\",\"description\":\"Este sistema involucra a los usuarios en la administración de expedientes, optimiza y agiliza el proceso que a diario realizan en torno al registro, control y seguimiento de expedientes. \",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":11,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":12,\"title\":\"manejo basico del sistema\",\"index\":3,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":23,\"title\":\"ingreso al sistema\",\"description\":\"Aprenda como ubicar el SAE y utilizar su usuario y contraseña\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":12,\"questions\":null,\"Files\":null}],\"Files\":null}]");
        return  testValues;
    }

    static ContentValues createCatalogUpdateJSON () {
        ContentValues testValues = new ContentValues();
        testValues.put(evaContract.catalogEntry.COLUMN_CATALOG_LIST, "[{\"ChapterID\":10,\"title\":\"Asignación Expediente (prueba actualizacion en el catalogo)\",\"index\":2,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":21,\"title\":\"Como recibirlo\",\"description\":\"El expediente asigando se vera reflejado en su sesion en la parte izquierda de Mis Actividades Pendientes o en Expedinentes Enviados y Recibidos\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":10,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":11,\"title\":\"Que es el SAE\",\"index\":1,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":22,\"title\":\"Sistema de Administración Expedientes - SAE\",\"description\":\"Este sistema involucra a los usuarios en la administración de expedientes, optimiza y agiliza el proceso que a diario realizan en torno al registro, control y seguimiento de expedientes. \",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":11,\"questions\":null,\"Files\":null}],\"Files\":null},{\"ChapterID\":12,\"title\":\"manejo basico del sistema\",\"index\":3,\"CourseID\":2,\"Course\":null,\"lessons\":[{\"LessonID\":23,\"title\":\"ingreso al sistema\",\"description\":\"Aprenda como ubicar el SAE y utilizar su usuario y contraseña\",\"videoURL\":null,\"videoName\":null,\"videoStorageName\":null,\"ChapterID\":12,\"questions\":null,\"Files\":null}],\"Files\":null}]");
        return testValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /*
       Students: The functions we provide inside of TestProvider use this utility class to test
       the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
       CTS tests.
       Note that this only tests that the onChange function is called; it does not test that the
       correct Uri is returned.
    */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }


}
