package in.geekvalet.sevame.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.service.SevaMeService;
import retrofit.RetrofitError;

public class VerifyMobileActivity extends ActionBarActivity {
    private static final String LOG_TAG = VerifyMobileActivity.class.getName();
    private static final String SEVAME_OTP_REGEX = "OTP for registration with Sevame is";
    private Button signupButton;
    private TextView progressText;
    private EditText phoneNumber;
    private EditText name;
    private ProgressBar spinner;
    private String otp = null;
    private boolean verified = false;
    private SmsReceiver smsReceiver = null;
    private boolean otpRequested = false;

    public class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msgFrom;
                if (bundle != null){
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msgFrom = msgs[i].getOriginatingAddress().toUpperCase();
                        String msgBody = msgs[i].getMessageBody().toUpperCase();
                        Log.d(LOG_TAG, "Message is from " + msgFrom + " and body is " + msgBody);

                        tryExtractOtp(msgBody);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        signupButton = (Button) findViewById(R.id.signup_button);
        progressText = (TextView) findViewById(R.id.progress_text);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        name = (EditText) findViewById(R.id.name);
        spinner = (ProgressBar) findViewById(R.id.progress_spinner);


        ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();
        name.setText(serviceProvider.getName());
        name.setEnabled(false);

        if(phoneNumber.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()) {
                    return;
                }

                disableForm();

                progressText.setText("Sending SMS to verify your phone");
                progressText.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);

                requestOTP();
            }
        });
    }

    private void submitOTP(final String otp) {
        if(!verified) {
            progressText.setText("Submitting otp for verification");

            new AsyncTask<Object, Object, Boolean>() {

                @Override
                protected Boolean doInBackground(Object... params) {
                    ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();
                    SevaMeService sevaMeService = Application.getSevaMeService();

                    try {
                        sevaMeService.verifyServiceProvider(serviceProvider.getId(), otp);
                    } catch (RetrofitError retrofitError) {
                        return false;
                    }

                    return true;
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    verified = successful;

                    if(verified) {
                        ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();
                        serviceProvider.markAsVerified();
                        Application.getDataStore().saveServiceProvider(serviceProvider);

                        Intent intent = new Intent(VerifyMobileActivity.this, SelectSkillSetActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        fail();
                    }
                }
            }.execute();
        }
    }

    private void requestOTP() {
        new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();
                String phoneNumber1 = VerifyMobileActivity.this.phoneNumber.getText().toString();

                try {
                    Application.getSevaMeService().requestOTP(serviceProvider.getId(), phoneNumber1);
                } catch (RetrofitError error) {
                    Log.e(LOG_TAG, "Failed to request otp");
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                if(!successful) {
                    fail();
                } else if(otp == null) {
                    otpRequested = true;
                    progressText.setText("Waiting for SMS");
                } else {
                    submitOTP(otp);
                }
            }
        }.execute();

    }

    private void fail() {
        Toast.makeText(getApplicationContext(), "Mobile verification failed. Please try again",
                Toast.LENGTH_LONG).show();
        resetForm();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSMSReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSMSReceiver();

        if(otpRequested) {
            fetchExistingOtp();
        }
    }

    private void registerSMSReceiver() {
        IntentFilter iff = new IntentFilter();
        try {
            if(this.smsReceiver == null) {
                this.smsReceiver = new SmsReceiver();
                iff.addAction("android.provider.Telephony.SMS_RECEIVED");
                registerReceiver(this.smsReceiver, iff);
            }
        } catch (Throwable e){
            Log.w(LOG_TAG, "Failed to register SMS broadcast receiver (Ignoring)", e);
        }
    }

    private void unregisterSMSReceiver() {
        try {
            if(this.smsReceiver != null) {
                unregisterReceiver(this.smsReceiver);
                this.smsReceiver = null;
            }
        } catch (Throwable e) {
            Log.w(LOG_TAG, "Failed to unregister receiver (Ignoring)", e);
        }
    }

    private void resetForm() {
        signupButton.setEnabled(true);
        phoneNumber.setEnabled(true);
        name.setEnabled(true);

        progressText.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
    }

    private void disableForm() {
        signupButton.setEnabled(false);
        phoneNumber.setEnabled(false);
        name.setEnabled(false);
    }

    private boolean validate() {
        return validatePhoneNumber();
    }


    private boolean validatePhoneNumber() {
        boolean validPhoneNumber = android.util.Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches();

        if(!validPhoneNumber) {
            phoneNumber.setError("Phone number is invalid");
            return false;
        }
        return true;
    }

    public void fetchExistingOtp() {
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "body", "date"};
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -90 );
        String WHERE_CONDITION = String.format("read = 0 and date > ?");
        Log.d(LOG_TAG, WHERE_CONDITION);
        String SORT_ORDER = "date DESC";
        Cursor cur = null;
        try {
            cur = getContentResolver().query(uriSMSURI, reqCols, WHERE_CONDITION,
                    new String[]{"" + cal.getTime().getTime()}, SORT_ORDER);

            String sms = "";
            while (cur.moveToNext()) {
                sms = "From: " + cur.getString(1) + " : " + cur.getString(2) + " " + cur.getLong(3);
                Log.d(LOG_TAG, sms);
                String msgBody = cur.getString(2);

                tryExtractOtp(msgBody);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception while trying to read from Inbox: ", e);
        } finally {
            if(cur != null) {
                cur.close();
            }
        }
    }

    private void tryExtractOtp(String msgBody) {
        if(msgBody.toUpperCase().contains(SEVAME_OTP_REGEX.toUpperCase())) {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(msgBody);

            if(matcher.find()) {
                otp = matcher.group(0);
                ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();

                if(serviceProvider != null) {
                    submitOTP(otp);
                }
            }
        } else {
            Log.w(LOG_TAG, "Failed to match sms body");
        }
    }
}
