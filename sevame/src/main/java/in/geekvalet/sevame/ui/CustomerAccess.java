package in.geekvalet.sevame.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import junit.framework.Test;

import java.util.Locale;

import in.geekvalet.sevame.R;
import in.geekvalet.sevame.util.SJob;


public class CustomerAccess extends Fragment {

    private Button Location;
    private Button GoCustomer;
    private Fragment fragment;
    private  SJob sJob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_access, container, false);
        setHasOptionsMenu(true);
        Location = (Button)view.findViewById(R.id.cust_location);
        GoCustomer = (Button)view.findViewById(R.id.go_customer);
        final FragmentManager fragmentManager = getFragmentManager();
        Bundle args = new Bundle();
        args = getArguments();

        if (null != args){
            Gson gson = new Gson();
            sJob = gson.fromJson((String)args.get("Job"), SJob.class);
        }
        if (sJob != null){
            TextView txtApptTime = (TextView)view.findViewById(R.id.appoint_time_txt);
            txtApptTime.setText(sJob.getAppointTime());

            TextView txtId = (TextView)view.findViewById(R.id.jb_id_text1);
            txtId.setText(String.valueOf(sJob.getJobId()));

            TextView txtJobDesc = (TextView)view.findViewById(R.id.jb_desc_text);
            txtJobDesc.setText(sJob.getDesc());

            TextView address = (TextView)view.findViewById(R.id.address_txt);
            address.setText(sJob.getAddress());

            TextView cusName = (TextView)view.findViewById(R.id.cus_name_txt);
            cusName.setText(sJob.getCusName());

        }

        Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float latitude = 12.9279210F;
                float longitude = 77.62710779999998F;
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=17&q=%f,%f", latitude,longitude,latitude,longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getActivity().startActivity(intent);
            }
        });

        GoCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Gson gson = new Gson();
                args.putString("Job", gson.toJson(sJob));
                fragment = new CustomerReached();
                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_jobs, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        Bundle args = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do s.th.
                break;
            case R.id.open_jobs_list:
                fragment = new OpenJobList();
                args.putInt("jobType", 0);
                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            case R.id.assign_jobs_list:
                fragment = new AssignedJobList();
                //args.putInt("jobType", 1);
                //fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
