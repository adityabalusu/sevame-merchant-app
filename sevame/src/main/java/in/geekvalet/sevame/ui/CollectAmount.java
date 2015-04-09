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

import in.geekvalet.sevame.R;
import in.geekvalet.sevame.RateCustomer;


public class CollectAmount extends Fragment {

    private Button collectAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_amount, container, false);
        setHasOptionsMenu(true);
        collectAmount = (Button)view.findViewById(R.id.collect_amount);
        TextView text = (TextView)view.findViewById(R.id.text_collect_amount);
        text.setText("Please Collect an amount of Rs.300");
        final FragmentManager fragmentManager = getFragmentManager();
        collectAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=   new RateCustomer();
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
