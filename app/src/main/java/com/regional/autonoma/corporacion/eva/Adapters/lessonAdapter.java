package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.R;

/**
 * Created by nestor on 11-Aug-16.
 */
public class lessonAdapter extends CursorAdapter {

    //macros to control the type of view drawn
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CHAPTER = 0;
    private static final int VIEW_TYPE_LESSON = 1;

    public lessonAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }

    public static class ViewHolder{
        public final TextView titleView;
        public final TextView descriptionView;
        public final TextView chapterView;
        public final TextView numberView;
        public final TextView percentCompleteView;
        public final ImageView lessonStatusView;

        public ViewHolder (View view){
            titleView = (TextView) view.findViewById(R.id.textView_courseTitle);
            descriptionView = (TextView) view.findViewById(R.id.textView_courseDescription);
            numberView = (TextView) view.findViewById(R.id.textView_chapterSeparator_number);
            chapterView = (TextView) view.findViewById(R.id.textView_chapterSeparator_text);
            percentCompleteView = (TextView) view.findViewById((R.id.textView_chapterSeparator_percent_complete));
            lessonStatusView = (ImageView) view.findViewById(R.id.imageView_lesson_status);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView;

        int viewType = getItemViewType(cursor.getPosition());
        int layoutid = -1;

        switch (viewType){
            case VIEW_TYPE_CHAPTER:{
                layoutid = R.layout.piece_chapter_list_separator;
                break;
            }
            case VIEW_TYPE_LESSON: {
                layoutid = R.layout.piece_lesson_list_item;
                break;
            }
        }

        rowView = LayoutInflater.from(context).inflate(layoutid, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        rowView.setTag(viewHolder);

        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());
        //read the data from the cursor
        //we expect the fully formed cursor here
        //TODO: implement projections to reduce errors with this indices
        switch (viewType){
            case VIEW_TYPE_CHAPTER:{
                viewHolder.chapterView.setText(cursor.getString(5));
                //CARE: setText with an INT is a reference to a resource
                //we need to cast it to a string if we want to display its value
                viewHolder.numberView.setText(String.valueOf(cursor.getInt(4)));
                viewHolder.percentCompleteView.setText(String.valueOf(cursor.getInt(8))+ " %");
                break;
            }
            case  VIEW_TYPE_LESSON:{
                viewHolder.titleView.setText(cursor.getString(5));
                viewHolder.descriptionView.setText(cursor.getString(6));
                String viewed = cursor.getString(9);
                String passed = cursor.getString(10);

                if(viewed == "false")
                    viewHolder.lessonStatusView.setImageResource(android.R.drawable.presence_invisible);

                if(viewed == "true" && passed == "false")
                    viewHolder.lessonStatusView.setImageResource(android.R.drawable.presence_offline);

                if(viewed == "true" && passed == "true")
                    viewHolder.lessonStatusView.setImageResource(android.R.drawable.presence_online);


                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    private int getItemType(Cursor cursor){
        //TODO: check the position for the Item type
        String type = cursor.getString(1);
        if(type.equals("chapter")){
            //its a chapter
            return VIEW_TYPE_CHAPTER;
        }
        else{
            //its a lesson
            return VIEW_TYPE_LESSON;
        }

    }
}
