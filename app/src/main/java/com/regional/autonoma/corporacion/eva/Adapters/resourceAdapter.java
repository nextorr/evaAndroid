package com.regional.autonoma.corporacion.eva.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.regional.autonoma.corporacion.eva.Model.Lesson;
import com.regional.autonoma.corporacion.eva.R;

import java.util.List;

/**
 * Created by nestor on 5/9/2016.
 */
public class resourceAdapter extends ArrayAdapter {
    private final List list;
    private final Context context;


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
