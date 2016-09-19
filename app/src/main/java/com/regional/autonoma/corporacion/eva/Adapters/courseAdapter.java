package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.R;

/**
 * Created by nestor on 6/29/2016.
 */
public class courseAdapter extends CursorAdapter{

    public courseAdapter(Context context, Cursor c, int flags){
        super(context,c,flags);
    }

    public static class ViewHolder{
        public final TextView titleView;
        public final TextView descriptionView;
        public final TextView progressView;
        public final TextView coursePointsView;
        public final TextView courseDueDateView;

        public ViewHolder (View view){
            titleView = (TextView) view.findViewById(R.id.textView_courseTitle);
            descriptionView = (TextView) view.findViewById(R.id.textView_courseDescription);
            progressView = (TextView) view.findViewById(R.id.textView_courseProgress);
            coursePointsView = (TextView) view.findViewById(R.id.textView_coursePoints);
            courseDueDateView = (TextView) view.findViewById(R.id.textView_courseDueDate);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.piece_course_list_item
                ,parent,false);

        ViewHolder viewHolder = new ViewHolder(rowView);

        rowView.setTag(viewHolder);

        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //read the data from the cursor
        //we expect the fully formed cursor here
        //TODO: implement projections to reduce errors with this indices
//        String courseTitle = cursor.getString(2);
//        String courseDescription = cursor.getString(3);
        viewHolder.titleView.setText(cursor.getString(2));
        viewHolder.descriptionView.setText(cursor.getString(3));
        viewHolder.progressView.setText(cursor.getString(4) + " %");
        viewHolder.coursePointsView.setText(cursor.getString(5) +" / "+cursor.getString(6));
        viewHolder.courseDueDateView.setText(cursor.getString(7).split("T")[0]);
    }
}
