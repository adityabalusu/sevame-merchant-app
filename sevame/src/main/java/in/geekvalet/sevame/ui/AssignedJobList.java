package in.geekvalet.sevame.ui;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import in.geekvalet.sevame.R;
import in.geekvalet.sevame.util.AssignedListAdapter;
import in.geekvalet.sevame.util.SJob;

public class AssignedJobList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assigned_job_list, container, false);
        setHasOptionsMenu(true);
        ArrayList<SJob> arrayOfJobs = new ArrayList<SJob>();
        SJob sj1 = new SJob();
        sj1.setOrderTime("12:30 PM 8/4/2015");
        sj1.setAppointTime("12:30 PM 15/4/2015");
        sj1.setAddress("Koramangala, banaglore");
        sj1.setCusName("Bharath");
        sj1.setDesc("Fix Tap");
        sj1.setJobId(51456);

        SJob sj2 = new SJob();
        sj2.setOrderTime("12:30 PM 8/4/2015");
        sj2.setAppointTime("12:30 PM 15/4/2015");
        sj2.setAddress("Koramangala, banaglore");
        sj2.setCusName("Bharath");
        sj2.setDesc("Fix Tap");
        sj2.setJobId(1234);
        arrayOfJobs.add(sj1);
        arrayOfJobs.add(sj2);

        AssignedListAdapter adapter = new AssignedListAdapter(getActivity(), arrayOfJobs);
        ListView listview =  (ListView)view.findViewById(R.id.assigned_list_view);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SJob sjob = (SJob)parent.getAdapter().getItem(position);
                Bundle args = new Bundle();
                Gson gson = new Gson();
                args.putString("Job", gson.toJson(sjob));

                Fragment jobfrag = new CustomerAccess();
                jobfrag.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, jobfrag).commit();
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
        android.support.v4.app.Fragment fragment;
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
