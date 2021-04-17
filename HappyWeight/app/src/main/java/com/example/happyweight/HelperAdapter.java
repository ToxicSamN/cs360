package com.example.happyweight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HelperAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList dates, weights, ids;

    private int focusedItem = 0;

    // constructor for the recycle view helper adapter
    public HelperAdapter(Context context, ArrayList dates, ArrayList weights, ArrayList ids){
        this.context = context;
        this.dates = dates;
        this.weights = weights;
        this.ids = ids;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_weights, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.txtDates.setText(dates.get(position).toString());
        viewHolder.txtWeights.setText(weights.get(position).toString());
        viewHolder._ids.setText(ids.get(position).toString());

        viewHolder.itemView.setSelected(focusedItem == position); // <===
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnDelete;
        TextView txtDates, txtWeights, _ids;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDates = (TextView)itemView.findViewById(R.id.txtDate);
            txtWeights = (TextView)itemView.findViewById(R.id.txtWeight);
            _ids = (TextView)itemView.findViewById(R.id.view_recordId);

            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(this::onClickEdit);
            btnDelete.setOnClickListener(this::onClickDelete);
        }

        public void onClickEdit(View v) { // <===
            NavController navController = Navigation.findNavController(v);

            notifyItemChanged(focusedItem);
            focusedItem = getLayoutPosition();
            notifyItemChanged(focusedItem);

            Toast.makeText(v.getContext(), "EDIT " + txtDates.getText() + " position = " + focusedItem + ", " + txtWeights.getText(), Toast.LENGTH_SHORT).show();
            // open edit screen
            navController.navigate(WeightTrackerFragmentDirections.actionWeightTrackerFragmentToWeightRecordFragment(txtDates.getText().toString(), txtWeights.getText().toString(), _ids.getText().toString()));
        }

        public void onClickDelete(View v) { // <===
            NavController navController = Navigation.findNavController(v);

            notifyItemChanged(focusedItem);
            focusedItem = getLayoutPosition();
            notifyItemChanged(focusedItem);

            Toast.makeText(v.getContext(), "DELETE " + txtDates.getText() + " position = " + focusedItem + ", " + txtWeights.getText(), Toast.LENGTH_SHORT).show();
            // delete from database
            navController.navigate(WeightTrackerFragmentDirections.actionWeightTrackerFragmentToDeleteWeightFragment(_ids.getText().toString()));
        }
    }
}
