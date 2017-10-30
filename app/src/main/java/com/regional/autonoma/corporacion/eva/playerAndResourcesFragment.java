package com.regional.autonoma.corporacion.eva;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.regional.autonoma.corporacion.eva.Adapters.resourceAdapter;
import com.regional.autonoma.corporacion.eva.Communication.EvaServices;
import com.regional.autonoma.corporacion.eva.Model.Lesson;
import com.regional.autonoma.corporacion.eva.Utils.FileOpen;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nestor on 13-Sep-16.
 * fragment implememntation for the player and resources layout
 */
public class playerAndResourcesFragment extends Fragment {


    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_VIDEO_URL = "videoUrl";
    private static final String ARG_LESSON_ID = "lessonID";

    //member UI elements
    private ProgressBar mServiceFiles;
    private resourceAdapter mResourceAdapter;
    private Lesson mLessonInfo;
    private MediaController mVideoController;
    private ListView mFileListView; //use this to show download progress

    //helper class to send current file location to the asynctask
    public class stringInt {
        public String text;
        public int number;
    }
    public class DoubleInt {
        public int first;
        public int second;
    }

    public static playerAndResourcesFragment newInstance(String lessonID, String description, String videoURL){
        playerAndResourcesFragment fragment = new playerAndResourcesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LESSON_ID, lessonID);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_VIDEO_URL, videoURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = R.layout.fragment_player_and_resources;
        final View rootView = inflater.inflate(layoutId, container, false);
        //get the arguments for te fragment
        Bundle bundle = getArguments();
        mLessonInfo = new Lesson(bundle.getString(ARG_DESCRIPTION),bundle.getString(ARG_VIDEO_URL));

        //configure the toolbar
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Clase");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //settingUp UI elements

        mServiceFiles = (ProgressBar) rootView.findViewById(R.id.progressBar_files);
        mServiceFiles.setIndeterminate(true);

        TextView textView = (TextView) rootView.findViewById(R.id.textView_lessonTitle);
        textView.setText(bundle.getString(ARG_DESCRIPTION));

        //setting the video
        final VideoView video = (VideoView)rootView.findViewById(R.id.videoView_videoLesson);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mVideoController = new MediaController((getActivity()), false);
                        video.setMediaController(mVideoController);
                        mVideoController.setAnchorView(video);
                        mVideoController.show(1000);
                    }
                });
            }
        });

        video.setVideoPath(bundle.getString(ARG_VIDEO_URL));
        video.requestFocus();
        video.start();

        mResourceAdapter = new resourceAdapter(
                getActivity(),
                0,
                mLessonInfo.fileURLList
        );

        //ListView listView = (ListView) rootView.findViewById(R.id.listView_file_list);
        mFileListView = (ListView) rootView.findViewById(R.id.listView_file_list);
        //ViewCompat.setNestedScrollingEnabled(listView, true);
        mFileListView.setAdapter(mResourceAdapter);

        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new DownloadFile(getActivity()).execute(mLessonInfo.getFileURL(position), mLessonInfo.getFileDisplayName(position), String.valueOf(position));
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new QueryEvaServicesFiles().execute(getArguments().getString(ARG_LESSON_ID));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_player_and_resources, menu);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mVideoController!= null && !isVisibleToUser){
            mVideoController.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                //TODO: remove this option from the definition
                //do nothing
                break;
            case R.id.action_evaluate:
                //do nothing
                break;
            case R.id.action_fullscreen:
                Intent intent = new Intent(getActivity(), FullscreenPlayerActivity.class);
                intent.putExtra("eva.intent.parameters", mLessonInfo.videoURL);
                startActivity(intent);
                break;
            default:
                //Toast.makeText(getActivity(), "Menu selection error PR", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DownloadFile extends AsyncTask<String, Integer, File> {
        private Context mContext;
        public DownloadFile (Context context){
            mContext = context;
        }

        @Override
        protected File doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            int itemArrayPosition = Integer.parseInt(strings[2]); //position of the clicked item

            //check if externar storage is present
            String storageDirectory;
            String state = Environment.getExternalStorageState();

            File pdfFile;

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File folder = new File(storageDirectory, "eva");
                boolean success = true;
                if(!folder.exists()){
                    success = folder.mkdirs();
                }
                if(!success){
                    return  null;
                }


                pdfFile = new File(folder, fileName);
            } else {
                // cant write to external storage
                //so save the file to the internal system cache.
                pdfFile = new File(mContext.getCacheDir(), fileName);
            }



            //TODO: marked to delete
//            try{
//                pdfFile.createNewFile();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
            //TODO:remove the function
            //FileDownloader.downloadFile(fileUrl, pdfFile);
            //to get download progrees we need the actual cicle here, so we can publish the info to the UI
            final int  MEGABYTE = 1024 * 1024;
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
                float totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                float bitesDownloaded = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    fileOutputStream.write(buffer, 0, bufferLength);
                    bitesDownloaded += bufferLength;
                    publishProgress((int)((bitesDownloaded/totalSize)*100), itemArrayPosition);
                }
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return pdfFile;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //[0] -> progress;  [1]itemPosition
            //directly modify the list view
            //TODO: a reclicler view can be a better approach.
            View element = mFileListView.getChildAt(values[1] - mFileListView.getFirstVisiblePosition());

            if (element == null)
                return;

            TextView fileDisplayName = (TextView) element.findViewById(R.id.textView_file_item_downloadProgress);
            fileDisplayName.setText("   " + String.valueOf(values[0]) + " %");
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            if(pdfFile == null){
                Toast.makeText(mContext, "cannot create file", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                FileOpen.openFile(mContext, pdfFile);
            } catch (IOException e) {
                Toast.makeText(mContext, "cannot open the file", Toast.LENGTH_LONG).show();
                Log.v("FILE", "error opening the file");
                e.printStackTrace();
            }
        }
    }

    public class QueryEvaServicesFiles extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... courseID){
            EvaServices serviceReader = new EvaServices();
            final String result = serviceReader.servicePath("evafiles").serviceParameter("id", courseID[0]).read();
            publishProgress(100);
            return result;
        }

        @Override
        protected void onPostExecute(String serviceJsonResponse){
            //log the response
            Log.v("courseActivity", "the response is: " + serviceJsonResponse);
            //clear the file list to fill it with new data
            mLessonInfo.clearFileList();

            if (serviceJsonResponse != null){
                try{
                    JSONArray fileList = new JSONArray(serviceJsonResponse);
                    for (int i = 0 ; i < fileList.length(); i++){
                        mLessonInfo.addFile(fileList.getJSONObject(i).getString("fileURL"),
                                fileList.getJSONObject(i).getString("fileName"));
                    }
                    //once we have the question array fully formed, we set the adapter
                    //todo: change the way the file is structured, give it its own class
                    mResourceAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    EvaServices.handleServiceErrors(getActivity(), serviceJsonResponse);
                    Log.e("playerActivity","error parsing the file response");
                }
            }

        }

        @Override
        protected void onPreExecute() {
            mServiceFiles.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //mServiceProgress.setProgress(values[0]);
            if(values[0] == 100){
                mServiceFiles.setVisibility(View.GONE);
            }
        }
    }
}
