package com.example.happyweight;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

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

        // navigate back to weight tracker
        navController.navigate(WeightRecordFragmentDirections.actionWeightRecordFragmentToWeightTrackerFragment());
    }
}