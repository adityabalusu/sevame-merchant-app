package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.libs.GoogleMapsClient;
import in.geekvalet.sevame.model.Job;
import in.geekvalet.sevame.service.SevaMeService;
import in.geekvalet.sevame.service.Util;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class JobsActivity extends ActionBarActivity {
    private static final String LOG_TAG = JobsActivity.class.getName();
    private ViewPager viewPager;

    class TabListener implements ActionBar.TabListener {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            viewPager.setCurrentItem(tab.getPosition());
            viewPager.getAdapter().notifyDataSetChanged();
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
            Fragment fragment;

            if(i == 0) {
                fragment = new MyOrdersFragment();
            } else {
                fragment = new OpenBidsFragment();
            }
            return fragment;

        }

        //https://stackoverflow.com/questions/18088076/update-fragment-from-viewpager
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    public static class MyOrdersFragment extends Fragment {
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

                    return Application.getSevaMeService().fetchAssignedJobs(serviceProviderId).objects;
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
            final View view = inflater.inflate(R.layout.fragment_pending_job, jobsLayout, false);

            TextView description = (TextView) view.findViewById(R.id.description);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView address = (TextView) view.findViewById(R.id.address);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView phoneNumber = (TextView) view.findViewById(R.id.phone_number);

            description.setText(job.getRequest());
            String appointmentTime = formatDate(job.getAppointmentTime());

            date.setText(appointmentTime);
            address.setText(job.getAddress());
            name.setText(job.getUser().getName());
            phoneNumber.setText("(" + job.getUser().getPhoneNumber() + ")");

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
                public void onClick(View buttonView) {
                    showRatingsDialog(getActivity(), job);
                }
            });

            if(job.isStarted()) {
                startButton.setEnabled(false);
            } else {
                stopButton.setEnabled(false);
            }

            if(job.getLocation() != null) {
                LatLng location = job.getLocation().asLatLng();
                ImageView imageView = (ImageView) view.findViewById(R.id.location_thumbnail);
                fetchAndSetLocationBitmap(location, imageView, getActivity());
            }

            view.setTag("job_" + job.getId());

            jobsLayout.addView(view);
        }

        private void showRatingsDialog(FragmentActivity activity, final Job job) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_ratings, null);

            final RatingBar bar = (RatingBar) view.findViewById(R.id.rating_bar);
            bar.setNumStars(5);
            bar.setStepSize(1);

            builder.setCancelable(false);
            builder.setTitle("Please rate the customer");
            builder.setNeutralButton("Rate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    stopJob(job, bar.getRating());
                }
            });

            builder.setView(view);

            final AlertDialog dialog = builder.create();

            bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                }
            });

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
                }
            });
            dialog.show();
        }

        protected void startJob(final Job job, final Button startButton, final Button stopButton) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    Response response = Application.getSevaMeService().startJob(job.getId());

                    try {
                        return Util.isSuccessful(response);
                    } catch(RetrofitError error) {
                        return false;
                    }
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

        protected void stopJob(final Job job, final float rating) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    Response response = Application.getSevaMeService().stopJob(job.getId(), new SevaMeService.StopJobRequest(rating));
                    try {
                        return Util.isSuccessful(response);
                    } catch (RetrofitError e) {
                        return  false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    if(successful) {
                        View view = jobsLayout.findViewWithTag("job_" + job.getId());
                        view.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Job stop failed. Please try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    public static class OpenBidsFragment extends Fragment {
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

                    return Application.getSevaMeService().fetchOpenJobs(serviceProviderId).objects;
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
            final View view = inflater.inflate(R.layout.fragment_open_job, jobsLayout, false);

            TextView description = (TextView) view.findViewById(R.id.description);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView address = (TextView) view.findViewById(R.id.address);

            String appointmentTime = formatDate(job.getAppointmentTime());

            date.setText(appointmentTime);
            description.setText(job.getRequest());

            Button acceptButton = (Button) view.findViewById(R.id.accept_button);
            Button rejectButton = (Button) view.findViewById(R.id.reject_button);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View buttonView) {
                    acceptJob(job, view);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View buttonView) {
                    rejectJob(job, view);
                }
            });

            if(job.getLocation() != null) {
                LatLng location = job.getLocation().asLatLng();
                ImageView imageView = (ImageView) view.findViewById(R.id.location_thumbnail);
                fetchAndSetLocationBitmap(location, imageView, getActivity());
                fetchAndSetApproximateLocation(location, address, getActivity());
            }

            jobsLayout.addView(view);
        }

        private void fetchAndSetApproximateLocation(final LatLng location, final TextView addressView, final Context context) {
            new AsyncTask<Object, Object, Address>() {
                @Override
                protected Address doInBackground(Object... objects) {
                    Geocoder geocoder;
                    geocoder = new Geocoder(context);
                    try {
                        return geocoder.getFromLocation(location.latitude, location.longitude, 1).get(0);
                    } catch (IOException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Address address) {
                    if(address != null) {
                        addressView.setText(address.getSubLocality() + " (Approximate)");
                    }
                }
            }.execute();
        }

        private void rejectJob(final Job job, final View view) {
            new AsyncTask<Object, Object, Boolean>() {
                @Override
                protected Boolean doInBackground(Object... params) {
                    Response response = Application.getSevaMeService().rejectJob(job.getId());

                    try {
                        return Util.isSuccessful(response);
                    } catch (RetrofitError e) {
                        return false;
                    }
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
                    Response response = Application.getSevaMeService().acceptJob(job.getId());

                    try {
                        return Util.isSuccessful(response);
                    } catch (RetrofitError e) {
                        return false;
                    }
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

    private static String formatDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            Date date = simpleDateFormat.parse(dateString);
            int format = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY;
            return android.text.format.DateUtils.formatDateTime(Application.getContext(), date.getTime(), format);
        } catch (ParseException e) {
            return dateString;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new TabListener();

        actionBar.addTab(actionBar.newTab().setText("MY ORDERS").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("OPEN BIDS").setTabListener(tabListener));
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
