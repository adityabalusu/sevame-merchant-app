package in.geekvalet.sevame.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.libs.GoogleMapsClient;
import in.geekvalet.sevame.model.Job;
import in.geekvalet.sevame.service.Util;
import retrofit.client.Response;

public class JobsActivity extends ActionBarActivity {
    private static final String LOG_TAG = JobsActivity.class.getName();
    private ViewPager viewPager;

    class TabListener implements ActionBar.TabListener {

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            viewPager.setCurrentItem(tab.getPosition());

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

    }
    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            if(i == 0) {
                return new PendingJobsFragment();
            } else {
                return new OpenJobsFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    public static class PendingJobsFragment extends Fragment {
        private LinearLayout jobsLayout;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_pending_jobs, container, false);

            jobsLayout = (LinearLayout) rootView.findViewById(R.id.pending_jobs_list);

            fetchAssignedJobs(inflater);

            return rootView;
        }

        private void fetchAssignedJobs(final LayoutInflater inflater) {
            new AsyncTask<Object, Object, List<Job>>() {
                @Override
                protected List<Job> doInBackground(Object... params) {
                    String serviceProviderId = Application.getDataStore().getServiceProvider().getId();

                    List<Job> jobs = Application.getSevaMeService().fetchAssignedJobs(serviceProviderId).objects;
                    return jobs;
                }

                @Override
                protected void onPostExecute(List<Job> jobs) {
                    if(jobs != null) {
                        for(Job job: jobs) {
                            addJob(inflater, job);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job fetch failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }

        private void addJob(LayoutInflater inflater, final Job job) {
            View view = inflater.inflate(R.layout.fragment_pending_job, jobsLayout, false);

            TextView description = (TextView) view.findViewById(R.id.description);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView address = (TextView) view.findViewById(R.id.address);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView phoneNumber = (TextView) view.findViewById(R.id.phone_number);

            description.setText(job.getDescription());
            date.setText(job.getDate());
            address.setText(job.getAddress());
            name.setText(job.getCustomer().getName());
            phoneNumber.setText("(" + job.getCustomer().getPhoneNumber() + ")");

            final Button startButton = (Button) view.findViewById(R.id.start_button);
            final Button stopButton = (Button) view.findViewById(R.id.stop_button);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startJob(job, startButton, stopButton);
                }
            });

            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopJob(job, view);
                }
            });
            stopButton.setEnabled(false);


            if(job.getLocation() != null) {
                LatLng location = job.getLocation().asLatLng();
                ImageView imageView = (ImageView) view.findViewById(R.id.location_thumbnail);
                fetchAndSetLocationBitmap(location, imageView, getActivity());
            }

            jobsLayout.addView(view);
        }

        private void startJob(final Job job, final Button startButton, final Button stopButton) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    Response response = Application.getSevaMeService().startJob(job.getId());
                    return Util.isSuccessful(response);
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    if(successful) {
                        startButton.setEnabled(false);
                        stopButton.setEnabled(true);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job start failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }

        private void stopJob(final Job job, final View view) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    Response response = Application.getSevaMeService().stopJob(job.getId());
                    return Util.isSuccessful(response);
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    if(successful) {
                        view.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job stop failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    public static class OpenJobsFragment extends Fragment {
        private LinearLayout jobsLayout;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(
                    R.layout.fragment_open_jobs, container, false);

            jobsLayout = (LinearLayout) rootView.findViewById(R.id.open_jobs_list);

            fetchOpenJobs(inflater);
            return rootView;
        }


        private void fetchOpenJobs(final LayoutInflater inflater) {
            new AsyncTask<Object, Object, List<Job>>() {
                @Override
                protected List<Job> doInBackground(Object... params) {
                    String serviceProviderId = Application.getDataStore().getServiceProvider().getId();

                    List<Job> jobs = Application.getSevaMeService().fetchOpenJobs(serviceProviderId).objects;
                    return jobs;
                }

                @Override
                protected void onPostExecute(List<Job> jobs) {
                    if(jobs != null) {
                        for(Job job: jobs) {
                            addJob(inflater, job);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job fetch failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }

        private void addJob(LayoutInflater inflater, final Job job) {
            View view = inflater.inflate(R.layout.fragment_open_job, jobsLayout, false);

            TextView description = (TextView) view.findViewById(R.id.description);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView address = (TextView) view.findViewById(R.id.address);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView phoneNumber = (TextView) view.findViewById(R.id.phone_number);

            description.setText(job.getDescription());
            date.setText(job.getDate());
            address.setText(job.getAddress());
            name.setText(job.getCustomer().getName());
            phoneNumber.setText("(" + job.getCustomer().getPhoneNumber() + ")");

            Button acceptButton = (Button) view.findViewById(R.id.accept_button);
            Button rejectButton = (Button) view.findViewById(R.id.reject_button);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptJob(job, view);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rejectJob(job, view);
                }
            });

            if(job.getLocation() != null) {
                LatLng location = job.getLocation().asLatLng();
                ImageView imageView = (ImageView) view.findViewById(R.id.location_thumbnail);
                fetchAndSetLocationBitmap(location, imageView, getActivity());
            }

            jobsLayout.addView(view);
        }

        private void rejectJob(final Job job, final View view) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    String serviceProviderId = Application.getDataStore().getServiceProvider().getId();
                    Response response = Application.getSevaMeService().rejectJob(serviceProviderId, job.getId());
                    return Util.isSuccessful(response);
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    if(successful) {
                        view.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Reject job failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }

        private void acceptJob(final Job job, final View view) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    String serviceProviderId = Application.getDataStore().getServiceProvider().getId();
                    Response response = Application.getSevaMeService().acceptJob(serviceProviderId, job.getId());
                    return Util.isSuccessful(response);
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    if(successful) {
                        view.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Accept job failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new TabListener();

        actionBar.addTab(actionBar.newTab().setText("ACCEPTED").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("OPEN").setTabListener(tabListener));
    }

    private static void fetchAndSetLocationBitmap(final LatLng latLng, final ImageView imageContent, final Activity activity) {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                if(latLng != null) {
                    return GoogleMapsClient.getThumbnail(latLng.latitude, latLng.longitude);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null) {
                    imageContent.setImageBitmap(bitmap);
                    imageContent.setVisibility(View.VISIBLE);

                    imageContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, ShowLocationActivity.class);
                            intent.putExtra("latLng", latLng);
                            activity.startActivity(intent);
                        }
                    });

                }
            }
        }.execute();
    }
}
