package com.regional.autonoma.corporacion.eva;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nestor on 5/9/2016.
 */
public class resourceAdapter extends ArrayAdapter {
    private final List list;
    private final Context context;

    //macros to control the type of view drawn
//    private static final int VIEW_TYPE_COUNT = 2;
//    private static final int VIEW_TYPE_CHAPTER = 0;
//    private static final int VIEW_TYPE_LESSON = 1;


    public resourceAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
    }

    public static class ViewHolder{
        public final TextView fileDisplayName;

        public ViewHolder (View view){
            fileDisplayName = (TextView) view.findViewById(R.id.textView_file_item);
        }
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View rootView = convertView;
        ViewHolder viewHolder;


        if (rootView == null) {
            rootView=LayoutInflater.from(context).inflate(R.layout.piece_file_lesson,parent,false);
            viewHolder = new ViewHolder(rootView);
            rootView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) rootView.getTag();
        }

        //setting the values
        Lesson.evaFile item = (Lesson.evaFile) list.get(position);
        viewHolder.fileDisplayName.setText(item.nameToShow);


        return rootView;
    }

}
