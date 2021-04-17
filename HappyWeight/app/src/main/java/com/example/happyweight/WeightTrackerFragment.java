package com.example.happyweight;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyweight.models.User;
import com.example.happyweight.models.UserRecord;
import com.example.happyweight.models.WeightGoal;
import com.example.happyweight.models.WeightGoalRecord;
import com.example.happyweight.models.WeightRecord;
import com.example.happyweight.models.WeightTracker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class WeightTrackerFragment extends Fragment {

    private FirebaseAuth fAuth;
    private NavController navController;

    private WeightTracker model;
    private WeightGoal wgModel;
    private RecyclerView recyclerView;

    ArrayList dates, weights, _ids;

    private boolean goalReached;
    private int maxWeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_tracker, container, false);
        model = new WeightTracker(view.getContext());
        wgModel = new WeightGoal(view.getContext());

        recyclerView = view.findViewById(R.id.recView);
        WeightRecord[] records = model.getAllUserWeightRecords(Objects.requireNonNull(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail()));

        dates = new ArrayList();
        weights = new ArrayList();
        _ids = new ArrayList();
        for (int i = 0; i < records.length; i++){
            WeightRecord record = records[i];
            dates.add(record.getDate());
            weights.add(record.getWeight());
            _ids.add(record.getId());

            if ( record.getWeight() > maxWeight) { maxWeight = record.getWeight(); }
        }
        HelperAdapter helperAdapter = new HelperAdapter(getContext(), dates, weights, _ids);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        FloatingActionButton btnAddWeight = view.findViewById(R.id.btnAddWeight);
        btnAddWeight.setOnClickListener(this::addWeight);

    }

    public void addWeight(View v) {
        navController.navigate(WeightTrackerFragmentDirections.actionWeightTrackerFragmentToWeightRecordFragment(null, null, null));
    }
}
