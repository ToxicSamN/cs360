package com.example.happyweight;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.happyweight.models.WeightGoal;
import com.example.happyweight.models.WeightGoalRecord;
import com.example.happyweight.models.WeightRecord;
import com.example.happyweight.models.WeightTracker;
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

    private TextView txtWeightGoal;

    private GraphView graphView;

    private AlertDialog.Builder alrtBuilder;
    private LayoutInflater linflater;

    private WeightGoal wgModel;
    private WeightTracker wtModel;
    private String user_id;

    private SwitchCompat smsSwitch;

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

        wtModel = new WeightTracker(view.getContext());
        wgModel = new WeightGoal(view.getContext());

        alrtBuilder = new AlertDialog.Builder(view.getContext());
        linflater = this.getLayoutInflater();

        txtWeightGoal = view.findViewById(R.id.txtGoalWeight);
        WeightGoalRecord goal_record = wgModel.getGoalRecordByUser(user_id);
        if (goal_record == null) {
            txtWeightGoal.setText("0");
        } else {
            txtWeightGoal.setText(String.valueOf((goal_record.getWeight())));
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

        int currGoal = Integer.parseInt(txtWeightGoal.getText().toString());
        if (currGoal == 0) { currGoal = 100; } // default picker value if none set yet
        NumberPicker pkr = wgDialog.findViewById(R.id.goalWeightPicker);
        pkr.setMinValue(1);
        pkr.setMaxValue(999);
        pkr.setValue(currGoal);

        alrtBuilder.setTitle("Set Goal Weight")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int weight = pkr.getValue();
                        wgModel.updateWeightRecord(user_id, weight);
                        txtWeightGoal.setText(String.valueOf(weight));
                        loadLineGraphSeries();
                        Toast.makeText(v.getContext(), "Goal Weight Updated", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setView(wgDialog)
                .create()
                .show();
    }

    private void loadLineGraphSeries(){
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> goal_series = new LineGraphSeries<DataPoint>();
        WeightGoalRecord goal_record = wgModel.getGoalRecordByUser(user_id);
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
}
