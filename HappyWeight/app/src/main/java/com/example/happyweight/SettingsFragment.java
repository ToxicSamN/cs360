package com.example.happyweight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import android.os.Trace;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.happyweight.models.User;
import com.example.happyweight.models.UserRecord;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "Settings";

    private FirebaseAuth fAuth;

    private SwitchCompat enableSMS;
    private TextInputLayout phoneNo;
    private Button btnSave;

    private User usrModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fAuth = FirebaseAuth.getInstance();

        enableSMS = view.findViewById(R.id.enableSMS);

        usrModel = new User(view.getContext());
        UserRecord record = usrModel.getUserRecordByUserId(fAuth.getCurrentUser().getUid());

        enableSMS.setChecked(record.getSMS() == 1);
        enableSMS.setOnCheckedChangeListener(onCheckedChanged());

        btnSave = view.findViewById(R.id.btnSettingSave);

        phoneNo = view.findViewById(R.id.etxtPhoneNo);
        if (record.getTelNo() != null){
            phoneNo.getEditText().setText(record.getTelNo());
            phoneNo.setEnabled(true);
        } else { phoneNo.setEnabled(false); }
        phoneNo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSave.setEnabled(true);
            }
        });
        phoneNo.getEditText().addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verify telephone - output error if not valid
                String telNo = phoneNo.getEditText().getText().toString();

                if (telNo != null && !telNo.equals("")) {
                    // update database
                    updateModel(enableSMS.isChecked(), telNo);
                    // disable button
                    btnSave.setEnabled(false);
                } else {
                    phoneNo.setError("Phone Number Required");
                    Toast.makeText(getContext(), "MUST SUPPLY MOBILE PHONE NUMBER", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnSave.setEnabled(false);

    }

    private CompoundButton.OnCheckedChangeListener onCheckedChanged() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.enableSMS:
                        checkForSmsPermission(isChecked);
                        if (isChecked) { phoneNo.setEnabled(true); }
                        else { phoneNo.setEnabled(false); }
                        btnSave.setEnabled(true);
                        break;
                }
            }
        };
    }

    private void checkForSmsPermission(boolean isChecked) {
        if (isChecked && (ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED)) {

                Log.d(TAG, getString(R.string.permission_not_granted));
                // Permission not yet granted. Use requestPermissions().
                // MY_PERMISSIONS_REQUEST_SEND_SMS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        1);
        }
    }

    private void updateModel(boolean isChecked, String telephone){
        // set the database SMSEnabled field = isChecked
        int sms = isChecked ? 1 : 0;
        usrModel.updateUserRecord(fAuth.getCurrentUser().getUid(), sms, telephone);
    }
}