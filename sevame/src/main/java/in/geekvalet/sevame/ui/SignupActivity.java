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
import android.widget.*;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.service.CreateServiceProvider;
import in.geekvalet.sevame.service.VerifyServiceProvider;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends ActionBarActivity {

    private static final String LOG_TAG = SignupActivity.class.getName();
    private static final String sevame_OTP_REGEX = "Otp for registration with sevame";
    private Button signupButton;
    private TextView progressText;
    private EditText phoneNumber;
    private EditText name;
    private ProgressBar spinner;
    private String otp = null;
    private ServiceProvider serviceProvider;
    private boolean verified = false;
    private SmsReceiver smsReceiver = null;

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
        setContentView(R.layout.activity_signup);

        signupButton = (Button) findViewById(R.id.signup_button);
        progressText = (TextView) findViewById(R.id.progress_text);
        phoneNumber = (EditText) findViewById(R.id.phone_number);
        name = (EditText) findViewById(R.id.name);
        spinner = (ProgressBar) findViewById(R.id.progress_spinner);

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

                signup();
            }
        });
    }

    private void verifyServiceProvider() {
        if(!verified) {
            progressText.setText("Submitting otp for verification");

            new AsyncTask<Object, Object, Boolean>() {

                @Override
                protected Boolean doInBackground(Object... params) {
                    return new VerifyServiceProvider(serviceProvider, otp).invoke();
                }

                @Override
                protected void onPostExecute(Boolean successful) {
                    verified = successful;

                    if(verified) {
                        Intent intent = new Intent(SignupActivity.this, SelectSkillSetActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        fail();
                    }
                }
            }.execute();
        }
    }

    private void signup() {
        new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                CreateServiceProvider createServiceProvider = new CreateServiceProvider();
                createServiceProvider.setPhoneNumber(phoneNumber.getText().toString());
                createServiceProvider.setName(name.getText().toString());
                createServiceProvider.setInitiateVerification(true);

                serviceProvider = createServiceProvider.invoke();

                return serviceProvider != null;
            }

            @Override
            protected void onPostExecute(Boolean successful) {
                if(!successful) {
                    fail();
                } else if(otp == null) {
                    progressText.setText("Waiting for SMS");
                } else {
                    verifyServiceProvider();
                }
            }
        }.execute();

    }

    private void fail() {
        Toast.makeText(getApplicationContext(), "Signup failed. Please try again",
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
        fetchExistingOtp();
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
        return !(validatePhoneNumber() || validateName());
    }

    private boolean validateName() {
        boolean validName = name.getText().toString().length() > 0;

        if(!validName) {
            name.setError("Name can't be empty");
            return true;
        }
        return false;
    }

    private boolean validatePhoneNumber() {
        boolean validPhoneNumber = android.util.Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches();

        if(!validPhoneNumber) {
            phoneNumber.setError("Phone number is invalid");
            return true;
        }
        return false;
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
        if(msgBody.toUpperCase().contains(sevame_OTP_REGEX.toUpperCase())) {
            Pattern pattern = Pattern.compile("\\d{6}");
            Matcher matcher = pattern.matcher(msgBody);

            if(matcher.find()) {
                otp = matcher.group(0);

                if(serviceProvider != null) {
                    verifyServiceProvider();
                }
            }
        }
    }
}
