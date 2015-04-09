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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import in.geekvalet.sevame.R;
import in.geekvalet.sevame.util.AssignedListAdapter;
import in.geekvalet.sevame.util.MaterialListAdapter;


public class SubmitQuote extends Fragment {

    private Button submitQuote;
    private MaterialListAdapter adapter;
    private Button addButton;
    private  ArrayList<String> arraym;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_submit_quote, container, false);
        setHasOptionsMenu(true);
        final FragmentManager fragmentManager = getFragmentManager();
        submitQuote = (Button)view.findViewById(R.id.submit_quote);
        addButton = (Button)view.findViewById(R.id.material_add_button);


        /* Add Items to Add Material */
        arraym = new ArrayList<String>();
        adapter  = new MaterialListAdapter(getActivity(), arraym);
        ListView listview =  (ListView)view.findViewById(R.id.material_list);
        listview.setAdapter(adapter);

        List<String> sList = new ArrayList<String>();
        sList.add("1 Hr");
        sList.add("2 Hr");
        sList.add("3 Hr");
        sList.add("4 Hr");
        sList.add("5 Hr");
        sList.add("> 5 Hr");
        ArrayAdapter<String> adapterApprox = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, sList);

        Spinner approxTimeSpinner = (Spinner)view.findViewById(R.id.approx_time_spinner);
        approxTimeSpinner.setAdapter(adapterApprox);


        List<String> sListSeva = new ArrayList<String>();
        sListSeva.add("50 Rs");
        sListSeva.add("100 Rs");
        sListSeva.add("150 Rs");
        sListSeva.add("200 Rs");
        sListSeva.add("250 Rs");
        sListSeva.add("> 250 Rs");
        ArrayAdapter<String> adapterSeva = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, sListSeva);

        Spinner sevaSpinner = (Spinner)view.findViewById(R.id.sevame_charge_spinner);
        sevaSpinner.setAdapter(adapterSeva);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arraym.add("test");
                adapter.notifyDataSetChanged();
            }
        });
        submitQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CollectAmount();
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

                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
