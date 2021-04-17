package com.example.happyweight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyweight.models.User;
import com.example.happyweight.models.UserRecord;
import com.example.happyweight.models.WeightGoal;
import com.example.happyweight.models.WeightGoalRecord;
import com.example.happyweight.models.WeightRecord;
import com.example.happyweight.models.WeightTracker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class WeightRecordFragment extends Fragment {
    private NumberPicker weightPicker;
    private DatePicker datePicker;
    private TextView txtDBRecordId;

    private Button btnAdd;
    private Button btnCancel;

    private NavController navController;
    private FirebaseAuth fAuth;

    private WeightTracker model;
    private User userModel;
    private WeightGoal goalModel;
    private int goal_weight;

    // args
    private String in_date, in_weight, db_record;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            in_date = getArguments().getString("inputDate");
            in_weight = getArguments().getString("inputWeight");
            db_record = getArguments().getString("db_record");
        }

        navController = Navigation.findNavController(view);
        model = new WeightTracker(view.getContext());
        userModel = new User(view.getContext());
        goalModel = new WeightGoal(view.getContext());
        WeightGoalRecord goal_record = goalModel.getGoalRecordByUser(fAuth.getCurrentUser().getEmail());
        if (goal_record == null) {
            goal_weight = -1;
        } else {
            goal_weight = goal_record.getWeight();
        }


        datePicker = view.findViewById(R.id.datePicker);
        if (in_date != null) {

            Date date = model.convertStringToDate(in_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int m = cal.get(Calendar.MONTH);
            int d = cal.get(Calendar.DAY_OF_MONTH);
            int y = cal.get(Calendar.YEAR);
            datePicker.init(y,m,d,null);
        }

        weightPicker = view.findViewById(R.id.weightPicker);
        weightPicker.setMinValue(1);
        weightPicker.setMaxValue(999);
        if (in_weight != null){
            weightPicker.setValue(Integer.parseInt(in_weight));
        } else {
            // set some default value
            weightPicker.setValue(175);
        }

        btnAdd = view.findViewById(R.id.btnSubmitWeightAdd);
        if (db_record != null) {
            // change button text to say edit instead of add
            btnAdd.setText(R.string.wt_add_btnEdit);
        }
        btnAdd.setOnClickListener(this::onclickSubmit);

        btnCancel = view.findViewById(R.id.btnCancelWeightAdd);
        btnCancel.setOnClickListener(this::onclickCancel);
    }

    public void onclickCancel(View v) {
        // navigate to weight tracker
        navController.navigate(WeightRecordFragmentDirections.actionWeightRecordFragmentToWeightTrackerFragment());
    }

    public void onclickSubmit(View v){
        // add weight data to model/database
        String user_id = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
        String date = "" + (datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear();
        int weight = weightPicker.getValue();

        long id;
        assert user_id != null;
        if (db_record != null) {
             id = model.updateWeightRecord(db_record, user_id, date, weight);
        }else {
             id = model.addWeightRecord(user_id, date, weight);
        }
        UserRecord usrData = userModel.getUserRecordByUserId(fAuth.getCurrentUser().getUid());
        if (weight == goal_weight && weight > 0 && goal_weight > 0) {
            Toast.makeText(v.getContext(), "Goal Weight Reached", Toast.LENGTH_LONG).show();
            processSMS();
        }

        // navigate back to weight tracker
        navController.navigate(WeightRecordFragmentDirections.actionWeightRecordFragmentToWeightTrackerFragment());
    }

    private void processSMS(){
        User usrModel = new User(getContext());
        UserRecord usrData = usrModel.getUserRecordByUserId(fAuth.getCurrentUser().getUid());

        String message = "Happy Weight: Congratulations you have reached your goal weight of " + goal_weight + "!!";
        String ph = PhoneNumberUtils.normalizeNumber(usrData.getTelNo());

        if (checkSMSPermissions() && usrData.getTelNo() != null && usrData.getSMS() == 1) {
            // permission granted
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(PhoneNumberUtils.normalizeNumber(usrData.getTelNo()), null, message, null, null);
        }
    }

    private boolean checkSMSPermissions(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.SEND_SMS},1);
            }
        } else {
            return true;
        }

        // after prompting the user for permission, check if user allowed
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}