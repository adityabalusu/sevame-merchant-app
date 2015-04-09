package in.geekvalet.sevame.ui;

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

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.libs.KeyValueStore;
import in.geekvalet.sevame.util.SJob;


public class JobDescription extends Fragment {
    public Button acceptJob;
    public Button denyJob;
    private Fragment fragment;
    private  SJob sJob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_description, container, false);
/*      TextView  txtview = (TextView)view.findViewById(R.id.job_desc);
        txtview.setText("Address: 5th main 10th Cross");*/
        setHasOptionsMenu(true);
        Bundle args = new Bundle();
        args = getArguments();
       ;
        if (null != args){
            Gson gson = new Gson();
            sJob = gson.fromJson((String)args.get("Job"), SJob.class);
        }
        if (sJob != null){
            TextView txtApptTime = (TextView)view.findViewById(R.id.appoint_time_txt);
            txtApptTime.setText(sJob.getAppointTime());

            TextView txtId = (TextView)view.findViewById(R.id.jb_id_text);
            txtId.setText(String.valueOf(sJob.getJobId()));

            TextView txtJobDesc = (TextView)view.findViewById(R.id.jb_desc_text);
            txtJobDesc.setText(sJob.getDesc());

            TextView address = (TextView)view.findViewById(R.id.address_txt);
            address.setText(sJob.getAddress());

        }


        acceptJob = (Button)view.findViewById(R.id.accept_job);
        denyJob = (Button)view.findViewById(R.id.deny_job);

        acceptJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make a call to back end

                if(sJob != null){
                    KeyValueStore keyStore = Application.getDataStore().getKeyStore();
                    keyStore.write(String.valueOf(sJob.getJobId()), "accepted");
                }
                Fragment fragment = new OpenJobList();
                Bundle args = new Bundle();
                args.putInt("jobType", 0);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        denyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sJob != null){
                    KeyValueStore keyStore = Application.getDataStore().getKeyStore();
                    keyStore.write(String.valueOf(sJob.getJobId()), "denied");
                }
                Fragment fragment = new OpenJobList();
                Bundle args = new Bundle();
                args.putInt("jobType", 0);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });
        return view;
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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
