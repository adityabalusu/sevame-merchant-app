package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.util.List;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.httpClient.HttpClientM;
import in.geekvalet.sevame.httpClient.Models.User;
import in.geekvalet.sevame.libs.KeyValueStore;
import in.geekvalet.sevame.model.Job;


public class Profile extends Fragment {

    private ProgressDialog pDialog;
    private boolean clicked;
    private Button editButton;
    private Button callSevaMe;

    private boolean sevaCallButtonToggle = true;
    private boolean editButtonToggle = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pDialog = new ProgressDialog(getActivity());
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        getProfile();
        final FragmentManager fragmentManager = getFragmentManager();
        clicked = false;

        editButton = (Button)view.findViewById(R.id.edit_button);
        callSevaMe = (Button)view.findViewById(R.id.call_seva_me_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editButtonToggle) {
                    EditClicked(true);
                    editButtonToggle = false;
                    editButton.setText("Save");
                    callSevaMe.setText("Cancel");
                    sevaCallButtonToggle = false;

                }else{
                    saveProfile();
                    editButtonToggle = true;
                    editButton.setText("Edit");
                    sevaCallButtonToggle = true;
                   /* getProfile();*/
                    EditClicked(false);
                    callSevaMe.setText("Call Sevame");

                }
            }
        });


        callSevaMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sevaCallButtonToggle){
                    //Call Seva Me;
                }else{
                    EditClicked(true);
                    getProfile();
                    editButton.setText("Edit");
                    callSevaMe.setText("Call Sevame");
                    sevaCallButtonToggle = true;
                    editButtonToggle = true;
                }
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

    private void getProfile(){
        //pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait ..");
        pDialog.show();
        new AsyncTask<Object, Object, User>() {
            @Override
            protected User doInBackground(Object... params) {
                HttpClientM client = new HttpClientM();
                User user = null;
                ObjectMapper mapper = Application.getJacksonMapper();
                try {
                    user = mapper.readValue(client.get("user", null, null, null), User.class);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return user;
            }
            @Override
            protected void onPostExecute(User user) {
                populateProfile(user);

                pDialog.dismiss();
            }
        }.execute();
    }


    public void EditClicked(boolean editclicked){
        /* Name */
        ViewSwitcher switcher = (ViewSwitcher) getView().findViewById(R.id.switcher_name);
        EditText edit_name = (EditText) getView().findViewById(R.id.name_edit_v);
        TextView textv_name = (TextView) getView().findViewById(R.id.name_text_v);


        /* Phone NUmber*/
        ViewSwitcher switcher_phone = (ViewSwitcher) getView().findViewById(R.id.switcher_phone);
        EditText edit_phone = (EditText) getView().findViewById(R.id.phone_edit_v);
        TextView textv_phone = (TextView) getView().findViewById(R.id.phone_text_v);


        /* Service */
        ViewSwitcher switcher_service = (ViewSwitcher) getView().findViewById(R.id.switcher_service);
        EditText edit_service = (EditText) getView().findViewById(R.id.service_edit_v);
        TextView textv_service = (TextView) getView().findViewById(R.id.service_text_v);


          /* Experience */
        ViewSwitcher switcher_experience = (ViewSwitcher) getView().findViewById(R.id.switcher_experience);
        EditText edit_experience = (EditText) getView().findViewById(R.id.experience_edit_v);
        TextView textv_experience = (TextView) getView().findViewById(R.id.experience_text_v);


        /*Address tab starts here */

       /* Address 1 */
        ViewSwitcher switcher_address1 = (ViewSwitcher) getView().findViewById(R.id.switcher_address_1);
        EditText edit_address1 = (EditText) getView().findViewById(R.id.address_1_edit_v);
        TextView textv_address1 = (TextView) getView().findViewById(R.id.address_1_text_v);



       /* Address 2 */
        ViewSwitcher switcher_address2 = (ViewSwitcher) getView().findViewById(R.id.switcher_address_2);
        EditText edit_address2 = (EditText) getView().findViewById(R.id.address_2_edit_v);
        TextView textv_address2 = (TextView) getView().findViewById(R.id.address_2_text_v);



       /* Address 3 */
        ViewSwitcher switcher_address3 = (ViewSwitcher) getView().findViewById(R.id.switcher_address_3);
        EditText edit_address3 = (EditText) getView().findViewById(R.id.address_3_edit_v);
        TextView textv_address3 = (TextView) getView().findViewById(R.id.address_3_text_v);



       /* Address 4 */
        ViewSwitcher switcher_address4 = (ViewSwitcher) getView().findViewById(R.id.switcher_address_4);
        EditText edit_address4 = (EditText) getView().findViewById(R.id.address_4_edit_v);
        TextView textv_address4 = (TextView) getView().findViewById(R.id.address_4_text_v);



        if (editclicked) {
            switcher.showNext();
            switcher_phone.showNext();
            switcher_service.showNext();
            switcher_experience.showNext();
            switcher_address1.showNext();
            switcher_address2.showNext();
            switcher_address3.showNext();
            switcher_address4.showNext();
        }else{
            switcher.showPrevious();
            switcher_phone.showPrevious();
            switcher_service.showPrevious();
            switcher_experience.showPrevious();
            switcher_address1.showPrevious();
            switcher_address2.showPrevious();
            switcher_address3.showPrevious();
            switcher_address4.showPrevious();
        }

    }

    public void saveProfile(){
        KeyValueStore keyValueStore = Application.getDataStore().getKeyStore();
        EditText edit_name = (EditText) getView().findViewById(R.id.name_edit_v);
        EditText edit_phone = (EditText) getView().findViewById(R.id.phone_edit_v);
        EditText edit_service = (EditText) getView().findViewById(R.id.service_edit_v);
        TextView textv_experience = (TextView) getView().findViewById(R.id.experience_text_v);
        TextView textv_address1 = (TextView) getView().findViewById(R.id.address_1_text_v);
        TextView textv_address2 = (TextView) getView().findViewById(R.id.address_2_text_v);
        TextView textv_address3 = (TextView) getView().findViewById(R.id.address_3_text_v);
        TextView textv_address4 = (TextView) getView().findViewById(R.id.address_4_text_v);
        User edit_user = new User();

        edit_user.name = edit_name.getText().toString();
        edit_user.phoneNumber = edit_phone.getText().toString();

        /* Address */
        String address = "";
        if(!textv_address1.getText().toString().isEmpty()){
            address = textv_address1.getText().toString()+"++";
        }
        if(!textv_address2.getText().toString().isEmpty()){
            address += textv_address2.getText().toString()+"++";
        }
        if(!textv_address3.getText().toString().isEmpty()){
            address += textv_address3.getText().toString()+"++";
        }
        if(!textv_address4.getText().toString().isEmpty()){
            address += textv_address3.getText().toString()+"++";
        }
        edit_user.address = address;

        ObjectMapper mapper = Application.getJacksonMapper();
        ObjectWriter ow = mapper.writer();
        String userJson = null;
        try {
            userJson = ow.writeValueAsString(edit_user);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        final String userFJson = userJson;
        pDialog.setMessage("Please wait ...");
        pDialog.show();
        (new AsyncTask<Object, Object, String>() {
            HttpClientM clientM = new HttpClientM();
            @Override
            protected String doInBackground(Object... params) {
        /*        clientM.post("user", userFJson, null, null);
                return null;*/

/*                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;

                HttpPut httpPut = new HttpPut("https://sevame.in/api/" + "user");
                String authCode = Application.getDataStore().getAuthToken();
                httpPut.setHeader("X-Session-Id", authCode);

                try {

                    StringEntity se = new StringEntity(userFJson);
                    String repJson = null;
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httpPut.setEntity(se);
                    response = client.execute(httpPut);

                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        repJson = EntityUtils.toString(entity);
                    }
                    return  repJson;
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText("Erroe", "Error", "Cannot Estabilish Connection", );
                }
                return null;*/
                return clientM.put("user", userFJson, null, null);

            }
            @Override
            protected void onPostExecute(String json) {

                ObjectMapper mapper = Application.getJacksonMapper();
                User userObj = null;
                try {
                    userObj = mapper.readValue(json, User.class);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                populateProfile(userObj);
                pDialog.dismiss();
            }

        }).execute();
    }

    private void populateProfile(User user){
        // Set all others
        TextView tv = (TextView)getView().findViewById(R.id.name_text_v);
        tv.setText(user.name);
        ImageView imgView= (ImageView)getView().findViewById(R.id.s_verified);

        if(user.verified == false){
            imgView.setImageResource(R.drawable.ic_action_unv);
        }else{
            imgView.setImageResource(R.drawable.ic_verfied);
        }
    }

}
