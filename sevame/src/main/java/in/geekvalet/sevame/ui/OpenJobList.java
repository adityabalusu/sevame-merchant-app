package in.geekvalet.sevame.ui;

import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.httpClient.HttpClientM;
import in.geekvalet.sevame.httpClient.Models.User;
import in.geekvalet.sevame.libs.KeyValueStore;
import in.geekvalet.sevame.model.Job;
import in.geekvalet.sevame.util.AssignedListAdapter;
import in.geekvalet.sevame.util.SJob;


public class OpenJobList extends Fragment {

    private ArrayList<String> jobList = new ArrayList<String>();
    private AssignedListAdapter adapter;
    private List<Job> jobsmList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job_list, container, false);
        setHasOptionsMenu(true);

        ArrayList<SJob> arrayOfJobs = new ArrayList<SJob>();

        KeyValueStore keyStore = Application.getDataStore().getKeyStore();

        SJob sj1 = new SJob();
        sj1.setOrderTime("12:30 PM 8/4/2015");
        sj1.setAppointTime("12:30 PM 15/4/2015");
        sj1.setAddress("Koramangala, banaglore");
        sj1.setDesc("Fix Tap");
        sj1.setJobId(51456);

        if ("accepted".equals(keyStore.getString("51456", null))){
            sj1.setAccepted("accepted");
        }else if("denied".equals(keyStore.getString("51456", null))){
            sj1.setAccepted("denied");
        }

        SJob sj2 = new SJob();
        sj2.setOrderTime("9:30 AM 6/4/2015");
        sj2.setAppointTime("4:30 PM 17/4/2015");
        sj2.setAddress("Koramangala, banaglore");
        sj2.setDesc("Fix Tap");
        sj2.setJobId(51557);

        if ("accepted".equals(keyStore.getString("51557", null))){
            sj2.setAccepted("accepted");
        }else if("denied".equals(keyStore.getString("51557", null))){
            sj2.setAccepted("denied");
        }

        SJob sj3 = new SJob();
        sj3.setOrderTime("10:30 AM 5/4/2015");
        sj3.setAppointTime("4:30 PM 17/4/2015");
        sj3.setJobId(51558);
        sj3.setAddress("Koramangala, banaglore");
        sj3.setDesc("Fix Tap");

        if ("accepted".equals(keyStore.getString("51558", null))){
            sj3.setAccepted("accepted");
        }else if("denied".equals(keyStore.getString("51558", null))){
            sj3.setAccepted("denied");
        }

        arrayOfJobs.add(sj1);
        arrayOfJobs.add(sj2);
        arrayOfJobs.add(sj3);

        adapter  = new AssignedListAdapter(getActivity(), arrayOfJobs);
        ListView listview =  (ListView)view.findViewById(R.id.listView1);
        listview.setAdapter(adapter);
        getOpenJobs();

 /*       Bundle b = getArguments();
        int s = b.getInt("jobType"); if ( 0 == s) {
            new AsyncTask<Object, Object, List<Job>>() {
                @Override
                protected List<Job> doInBackground(Object... params) {

                    List<Job> jobList = new ArrayList<Job>();
                    //String serviceProviderId = Application.getDataStore().getServiceProvider().getId();
                    Job job1 = new Job();
                    Job job2 = new Job();
                    job1.setRequest("OpenJob1");
                    job2.setRequest("openJob2");
                    jobList.add(job1);
                    jobList.add(job2);
                    return jobList;
                    //return Application.getSevaMeService().fetchAssignedJobs(serviceProviderId).objects;
                }

                @Override
                protected void onPostExecute(List<Job> jobs) {
                    if (jobs != null) {
                        for (Job job : jobs) {
                            //Make list view
                            jobList.add(job.getRequest());
                        }
                        //adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job fetch failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();

        }else{
            new AsyncTask<Object, Object, List<Job>>() {
                @Override
                protected List<Job> doInBackground(Object... params) {

                    List<Job> jobList = new ArrayList<Job>();

                    //String serviceProviderId = Application.getDataStore().getServiceProvider().getId();
                    Job job1 = new Job();
                    Job job2 = new Job();
                    job1.setRequest("AssignedJob1");
                    job2.setRequest("Assigned2");
                    jobList.add(job1);
                    jobList.add(job2);
                    return jobList;
                    //return Application.getSevaMeService().fetchAssignedJobs(serviceProviderId).objects;
                }

                @Override
                protected void onPostExecute(List<Job> jobs) {
                    if (jobs != null) {
                        for (Job job : jobs) {
                            //Make list view
                            jobList.add(job.getRequest());
                        }
                       // adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job fetch failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();*/

        //}

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SJob sjob = (SJob)parent.getAdapter().getItem(position);
                Bundle args = new Bundle();
                Gson gson = new Gson();
                args.putString("Job", gson.toJson(sjob));
                Fragment jobfrag = new JobDescription();
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

    public String getOpenJobs(){

   //Implement get jobs

        return null;
    }

}
