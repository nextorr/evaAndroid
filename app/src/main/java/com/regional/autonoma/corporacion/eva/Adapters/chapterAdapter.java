package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.Model.Chapter;
import com.regional.autonoma.corporacion.eva.R;

import java.util.List;


/**
 * Created by nestor on 4/30/2016.
 * this determines the chapter and lessons view
 */
public class chapterAdapter extends ArrayAdapter {
    private final List list;
    private final Context context;

    //macros to control the type of view drawn
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CHAPTER = 0;
    private static final int VIEW_TYPE_LESSON = 1;


    public chapterAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
    }

    public static class ViewHolder{
        public final TextView titleView;
        public final TextView descriptionView;
        public final TextView chapterView;
        public final TextView numberView;

        public ViewHolder (View view){
            titleView = (TextView) view.findViewById(R.id.textView_courseTitle);
            descriptionView = (TextView) view.findViewById(R.id.textView_courseDescription);
            numberView = (TextView) view.findViewById(R.id.textView_chapterSeparator_number);
            chapterView = (TextView) view.findViewById(R.id.textView_chapterSeparator_text);
        }
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        ViewHolder viewHolder;

        int viewType = getItemViewType(position);
        int layoutid = -1;

        switch (viewType){
            case VIEW_TYPE_CHAPTER:{
                layoutid = R.layout.piece_chapter_list_separator;
                break;
            }
            case VIEW_TYPE_LESSON: {
                //layoutid = R.layout.piece_course_list_item;
                layoutid = R.layout.piece_lesson_list_item;
                break;
            }
        }

        if(rowView == null){
            rowView = LayoutInflater.from(context).inflate(layoutid
                    ,parent,false);

            viewHolder = new ViewHolder(rowView);

            rowView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) rowView.getTag();
        }

        //setting the values
        Chapter item = (Chapter) list.get(position);
        switch (viewType){
            case VIEW_TYPE_CHAPTER:{
                viewHolder.chapterView.setText(item.title);
                //CARE: setText with an INT is a reference to a resource
                //we need to cast it to a string if we want to display its value
                viewHolder.numberView.setText(String.valueOf(item.chapterNumber));
                break;
            }
            case  VIEW_TYPE_LESSON:{
                viewHolder.titleView.setText(item.title);
                viewHolder.descriptionView.setText(item.description);
                break;
            }
        }

        return rowView;
    }

    @Override
    public int getItemViewType(int position) {
        Chapter item = (Chapter) list.get(position);
        //only two return types
        return (item.evaType.equals("chapter"))?VIEW_TYPE_CHAPTER:VIEW_TYPE_LESSON;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}