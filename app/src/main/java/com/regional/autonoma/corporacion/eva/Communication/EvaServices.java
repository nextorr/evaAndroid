package com.regional.autonoma.corporacion.eva.Communication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.regional.autonoma.corporacion.eva.multipleLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nestor on 29-Aug-16.
 * standard class to query the eva services
 */
public class EvaServices {
    //use this address to communicate to the Azure infrastructure
    //private final String SERVICE_BASE_URL = "http://careva.azurewebsites.net/api/";
    private final String SERVICE_BASE_URL = "http://evacar.azurewebsites.net/api/";
    //use this address to communicate to the localhost from the emulator
    //private final String SERVICE_BASE_URL = "http://10.0.2.2:63052/api/";
    //use this address to communicate to the local server from the local network on the device
    //private final String SERVICE_BASE_URL = "http://192.168.0.5:63052/api/";
    private Uri.Builder mBuilder = Uri.parse(SERVICE_BASE_URL).buildUpon();

    public EvaServices servicePath(String path){
        mBuilder.appendPath(path);
        return  this;
    }

    public EvaServices serviceParameter(String key, String value){
        mBuilder.appendQueryParameter(key, value);
        return  this;
    }

    public String read(){

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String tableDataJsonStr = null;
        //will contain the spaced string
        String line;

        try {
//                final String SERVICE_BASE_URL = "http://careva.azurewebsites.net/api/chapters";
//                Uri serviceQueryUrl = Uri.parse(SERVICE_BASE_URL).buildUpon()
//                        .appendQueryParameter("id",courseID[0]).build();

            Uri serviceQueryUrl = mBuilder.build();

            URL url = new URL(serviceQueryUrl.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream;

            switch (urlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    inputStream = urlConnection.getInputStream();
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    inputStream = urlConnection.getErrorStream();
                    break;
                default:
                    return "ERROR" + String.valueOf(urlConnection.getResponseCode());

            }
            //TODO: if block marked to delete.

            // validate the response and read the input stream accordingly
//            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                inputStream = urlConnection.getInputStream();
//            }else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
//                inputStream = urlConnection.getErrorStream();
//            }else{
//                //anything here is undefined behaviour.
//                //any structured error response is logged on the server side
//                return "ERROR" + String.valueOf(urlConnection.getResponseCode());
//            }


            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            tableDataJsonStr = buffer.toString();
        }catch (IOException e){
            //error getting the data. log the info
            Log.e("service Reader", "Error while connecting to the service: " + mBuilder.build().toString(), e);
            return null;
        }finally {
            //close the connection
            if(urlConnection!= null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e("service reader", "Error closing stream: " + mBuilder.build().toString(), e);
                }
            }
        }
        return tableDataJsonStr;
    }


    public String write(String JsonRequest){

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        DataOutputStream writer = null;

        // Will contain the raw JSON response as a string.
        String tableDataJsonStr = null;
        //will contain the spaced string
        String line;

        try {
            Uri serviceQueryUrl = mBuilder.build();

            URL url = new URL(serviceQueryUrl.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // 0 forces the default transmission value
            //urlConnection.setChunkedStreamingMode(0);
            writer = new DataOutputStream(urlConnection.getOutputStream());
            writer.write(JsonRequest.getBytes("UTF-8"));

            //no need to explicitly connect as writing data sends it to the server
            //urlConnection.connect();

            InputStream inputStream;

            switch (urlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_OK:
                    inputStream = urlConnection.getInputStream();
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    inputStream = urlConnection.getErrorStream();
                    break;
                default:
                    return "ERROR" + String.valueOf(urlConnection.getResponseCode());

            }

            //TODO: block marked to delete
//            // validate the response and read the input stream accordingly
//            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK
//                    || urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED){
//                inputStream = urlConnection.getInputStream();
//            }else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
//                inputStream = urlConnection.getErrorStream();
//            }else{
//                //anything here is undefined behaviour.
//                //any structured error response is logged on the server side
//                return "ERROR : " + String.valueOf(urlConnection.getResponseCode());
//            }

            // Read the input stream into a String
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            tableDataJsonStr = buffer.toString();
        }
        catch (IOException e){
            //error getting the data. log the info
            Log.e("mainActivity", "Error while connecting to the service: ", e);
            return null;
        }
        finally {
            //close the connection
            if(urlConnection!= null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e("courseActivity", "Error closing stream: ", e);
                }
            }
            if (writer != null){
                try{
                    writer.flush();
                    writer.close();
                }catch (final IOException e){
                    Log.e("courseActivity", "Error closing stream: ", e);
                }
            }
        }
        return tableDataJsonStr;
    }

    //there are two cases to handle, there is a message inside a bad Request or string starting with ERROR.
    // finally handle everything else as a default error
    public static String handleServiceErrors(Context context, String JsonString){
        JSONObject jObject;
        String outMsg = "unknown error";
        if (JsonString != null){
            try {
                jObject = new JSONObject(JsonString);
                outMsg = jObject.getString("Message");
                Toast.makeText(context, outMsg, Toast.LENGTH_LONG).show();
                //send the user to the login page if the error is invalid public key
                if(outMsg.startsWith("ERROR : 100")){
                    Intent signInIntent = new Intent(context, multipleLoginActivity.class);
                    context.startActivity(signInIntent);
                }
            } catch (JSONException ex) {
                if(JsonString.startsWith("ERROR")){
                    Toast.makeText(context,JsonString, Toast.LENGTH_LONG).show();
                    return JsonString;
                }
                Toast.makeText(context,"communication error 100", Toast.LENGTH_LONG).show();
                Log.e("mainActivity", "communication error, invalid response received");
            }
        }
        return outMsg;
    }
}
