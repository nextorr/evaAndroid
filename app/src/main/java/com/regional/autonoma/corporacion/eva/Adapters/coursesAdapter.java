package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.Model.Course;
import com.regional.autonoma.corporacion.eva.R;

import java.util.List;


/**
 * Created by nestor on 4/30/2016.
 */
public class coursesAdapter extends ArrayAdapter {
    private final List list;
    private final Context context;

    public coursesAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
    }

    public static class ViewHolder{
        public final TextView titleView;
        public final TextView descriptionView;

        public ViewHolder (View view){
            titleView = (TextView) view.findViewById(R.id.textView_courseTitle);
            descriptionView = (TextView) view.findViewById(R.id.textView_courseDescription);
        }
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View rowView = convertView;
        ViewHolder viewHolder;

        if(rowView == null){
            rowView = LayoutInflater.from(context).inflate(R.layout.piece_course_list_item
            ,parent,false);

            viewHolder = new ViewHolder(rowView);

            rowView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) rowView.getTag();
        }

        Course item = (Course) list.get(position);
        viewHolder.titleView.setText(item.title);
        viewHolder.descriptionView.setText(item.description);

        return rowView;
    }
}
