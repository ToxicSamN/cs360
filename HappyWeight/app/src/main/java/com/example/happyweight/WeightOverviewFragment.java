package com.example.happyweight;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.happyweight.models.LoginModel;
import com.example.happyweight.models.User;
import com.example.happyweight.models.UserRecord;
import com.example.happyweight.models.WeightGoal;
import com.example.happyweight.models.WeightGoalRecord;
import com.example.happyweight.models.WeightRecord;
import com.example.happyweight.models.WeightTracker;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.firebase.auth.FirebaseAuth;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Objects;

public class WeightOverviewFragment extends Fragment {
    private FirebaseAuth fAuth;
    private NavController navController;

    private Button btnWeightTrack;
    private Button btnSetGoal;
    private TextView btnSMSChange;

    private TextView txtWeightGoal;
    private TextView txtSMSStatus;

    private GraphView graphView;

    private AlertDialog.Builder alrtBuilder;
    private LayoutInflater linflater;

    private WeightGoal wgModel;
    private WeightTracker wtModel;
    private User usrModel;
    private UserRecord usrData;
    private String user_id;

    private int goal_weight;
    private int lastWeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
        navController = Navigation.findNavController(view);

        txtSMSStatus = view.findViewById(R.id.smsStatus);

        wtModel = new WeightTracker(view.getContext());
        wgModel = new WeightGoal(view.getContext());
        usrModel = new User(view.getContext());
        usrData = usrModel.getUserRecordByUserId(fAuth.getCurrentUser().getUid());
        if (usrData.getSMS() == 0) {
            txtSMSStatus.setText(R.string.SMS_OFF);
        } else if (usrData.getSMS() == 1){
            txtSMSStatus.setText(R.string.SMS_ON);
        } else {
            txtSMSStatus.setText("");
        }

        btnSMSChange = view.findViewById(R.id.btnChngSMS);
        btnSMSChange.setOnClickListener(this::onClickChangeSMS);

        alrtBuilder = new AlertDialog.Builder(view.getContext());
        linflater = this.getLayoutInflater();

        txtWeightGoal = view.findViewById(R.id.txtGoalWeight);
        WeightGoalRecord goal_record = wgModel.getGoalRecordByUser(user_id);
        if (goal_record == null) {
            goal_weight = -1; // goal weight not set
            txtWeightGoal.setText(R.string.goal_not_set);
        } else {
            goal_weight = goal_record.getWeight();
            txtWeightGoal.setText(String.valueOf((goal_weight)));
        }

        btnSetGoal = view.findViewById(R.id.btnSetGoal);
        btnSetGoal.setOnClickListener(this::onClickSetGoal);

        graphView = (GraphView) view.findViewById(R.id.graphView);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setHumanRounding(true, true);
        graphView.setTitle("Weight Tracking Overview");
        graphView.setTitleTextSize(60);
        Viewport viewport = graphView.getViewport();
        viewport.setXAxisBoundsManual(true);
        loadLineGraphSeries();
    }

    public void onClickTracker(View v) {
        navController.navigate(WeightOverviewFragmentDirections.actionWeightOverviewFragmentToWeightTrackerFragment());
    }

    public void onClickSetGoal(View v){
        View wgDialog = linflater.inflate(R.layout.dialog_set_goal_weight, null);
        int currGoal;
        if (txtWeightGoal.getText().toString().equals("Not Set")) {
            currGoal = 100; // default picker value if none set yet
        } else {
            currGoal = Integer.parseInt(txtWeightGoal.getText().toString());
        }

        NumberPicker pkr = wgDialog.findViewById(R.id.goalWeightPicker);
        pkr.setMinValue(1);
        pkr.setMaxValue(999);
        pkr.setValue(currGoal);

        alrtBuilder.setTitle("Set Goal Weight")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goal_weight = pkr.getValue();
                        wgModel.updateWeightRecord(user_id, goal_weight);
                        txtWeightGoal.setText(String.valueOf(goal_weight));
                        loadLineGraphSeries();
                        Toast.makeText(v.getContext(), "Goal Weight Updated", Toast.LENGTH_LONG).show();
                        if (lastWeight == goal_weight && goal_weight > 0){
                            Toast.makeText(v.getContext(), "Goal Weight Reached", Toast.LENGTH_LONG).show();
                            processSMS();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .setView(wgDialog)
                .create()
                .show();
    }

    private void loadLineGraphSeries(){
        lastWeight = -1;
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> goal_series = new LineGraphSeries<DataPoint>();
        WeightGoalRecord goal_record = wgModel.getGoalRecordByUser(user_id);
        if (goal_record == null) {
            goal_record = new WeightGoalRecord();
            goal_record.setWeight(0);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        WeightRecord[] records = wtModel.getAllUserWeightRecords(user_id);
        for(int i = 0; i < records.length; i++){
            if (i == 0 || i == records.length - 1) {
                goal_series.appendData(
                        new DataPoint(
                                i,
                                goal_record.getWeight()
                        ),
                        false,
                        100
                );
            }

            WeightRecord record = records[i];
            series.appendData(
                    new DataPoint(
                            i,
                            record.getWeight()
                    ),
                    false,
                    100
            );

            // only track the latest weight entered into the system for alerting on goal weight reached
            if (i == records.length -1) {
                lastWeight = record.getWeight();
            }
        }
        graphView.getViewport().setMaxX(records.length);
        graphView.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(16);
        series.setColor(Color.BLUE);
        series.setTitle("Weight Data");

        graphView.addSeries(goal_series);
        goal_series.setColor(Color.GREEN);
        goal_series.setTitle("Goal Weight");
        goal_series.setThickness(6);

        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
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

    public void onClickChangeSMS(View v) {
        navController.navigate(SettingsFragmentDirections.actionGlobalSettingsFragment());
    }
}
