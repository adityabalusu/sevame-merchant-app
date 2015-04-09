package in.geekvalet.sevame.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import in.geekvalet.sevame.R;

/**
 * Created by root on 4/8/15.
 */
public class AssignedListAdapter extends ArrayAdapter<SJob>{

    public AssignedListAdapter(Context context, ArrayList<SJob> jobs) {
        super(context, 0, jobs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SJob job = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_assigned, parent, false);
        }

        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.job_list_ele);
        if ("accepted".equals(job.getAccepted())){
            layout.setBackgroundColor(Color.parseColor("#ADFFAD"));

        }else if ("denied".equals(job.getAccepted())){
            layout.setBackgroundColor(Color.parseColor("#FFB27F"));
        }
        // Lookup view for data population
        TextView assignOrderId = (TextView) convertView.findViewById(R.id.assign_order_id);
        TextView assignTime = (TextView) convertView.findViewById(R.id.assign_time);
        // Populate the data into the template view using the data object
        assignOrderId.setText("ID: "+String.valueOf(job.getJobId()));
        assignTime.setText(job.getAppointTime());
        // Return the completed view to render on screen
        return convertView;
    }

}
