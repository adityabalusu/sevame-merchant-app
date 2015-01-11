package in.geekvalet.sevame.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.List;
import java.util.Map;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.model.Service;
import in.geekvalet.sevame.model.Skill;
import in.geekvalet.sevame.service.SevaMeService;

/**
 * Created by gautam on 8/12/14.
 */
public class SelectSkillSetActivity extends ActionBarActivity {
    private static final String LOG_TAG = SelectSkillSetActivity.class.getName();

    SelectSkillSetListAdapter listAdapter;
    ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_skillset);

        // get the listview
        listView = (ExpandableListView) findViewById(R.id.skillset_list);

        // preparing list data
        fetchSkillTypes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_skill_set, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_done:
                updateSkills();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSkills() {
        if(listAdapter != null) {
            new AsyncTask<Object, Object, ServiceProvider>() {
                @Override
                protected ServiceProvider doInBackground(Object... objects) {
                    final Map<String, List<Skill>> skills = listAdapter.selectedSkills();
                    final String serviceProviderId = Application.getDataStore().getServiceProvider().getId();

                    try {
                        SevaMeService.UpdateServiceProviderRequest request = new SevaMeService.UpdateServiceProviderRequest();
                        request.skills = skills;
                        return Application.getSevaMeService().updateServiceProvider(serviceProviderId, request);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Failed to fetch service types " + e.getMessage());
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(ServiceProvider serviceProvider) {
                    if(serviceProvider != null) {
                        Log.i(LOG_TAG, "Successfully updated service Types");
                    }
                }
            }.execute();
        }
    }

    /*
     * Preparing the list data
     */
    private void fetchSkillTypes() {
        new AsyncTask<Object, Object, List<Service>>() {
            @Override
            protected List<Service> doInBackground(Object... objects) {
                try {
                    return Application.getSevaMeService().fetchServiceTypes().objects;
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to fetch service types " + e.getMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<Service> services) {
                if(services != null) {
                    listAdapter = new SelectSkillSetListAdapter(SelectSkillSetActivity.this, services);

                    // setting list adapter
                    listView.setAdapter(listAdapter);
                }
            }
        }.execute();
    }
}
