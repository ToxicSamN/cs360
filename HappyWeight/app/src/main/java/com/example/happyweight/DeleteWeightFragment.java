package com.example.happyweight;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.happyweight.models.WeightTracker;


public class DeleteWeightFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_weight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get record from args
        String record_id = getArguments().getString("record_id");
        //establish new model
        WeightTracker model = new WeightTracker(view.getContext());
        // delete the record
        long numDeletedRecords = model.deleteWeightRecord(record_id);

        Navigation.findNavController(view).navigate(DeleteWeightFragmentDirections.actionDeleteWeightFragmentToWeightTrackerFragment());
    }
}