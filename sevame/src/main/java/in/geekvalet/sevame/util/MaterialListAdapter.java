package in.geekvalet.sevame.util;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import in.geekvalet.sevame.R;

/**
 * Created by root on 4/9/15.
 */
public class MaterialListAdapter extends ArrayAdapter<String> {

    public MaterialListAdapter(Context context, ArrayList<String> jobs) {
        super(context, 0, jobs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position


        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.material_list, parent, false);
        }


        // Return the completed view to render on screen
        return convertView;
    }


}
