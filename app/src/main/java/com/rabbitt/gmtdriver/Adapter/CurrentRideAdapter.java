package com.rabbitt.gmtdriver.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.gmtdriver.CurrentRide.CurrentRide;
import com.rabbitt.gmtdriver.R;
import com.ramotion.foldingcell.FoldingCell;

import java.util.List;

public class CurrentRideAdapter extends RecyclerView.Adapter<CurrentRideAdapter.holder> {

    private static final String TAG = "CurrentRideAdapter";
    private List<RecycleAdapter> dataModelArrayList;
    private CurrentRide context;
    private OnRecycleItemListener mOnRecycleItemListener;

    public CurrentRideAdapter(List<RecycleAdapter> productAdapter, CurrentRide context, OnRecycleItemListener onRecycleItemListener)
    {
        this.dataModelArrayList = productAdapter;
        this.context = context;
        this.mOnRecycleItemListener = onRecycleItemListener;
        Log.i(TAG, dataModelArrayList.toString());
    }

    @NonNull
    @Override
    public CurrentRideAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_recycler_item, null);
        return new holder(view, mOnRecycleItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentRideAdapter.holder holder, int i) {
        RecycleAdapter dataModel = dataModelArrayList.get(i);
        holder.item_content.setText(dataModel.getContent());
        holder.item_title.setText(dataModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnRecycleItemListener onRecycleItemListener;
        TextView item_title, item_content;
        FoldingCell foldingCell;
        holder(@NonNull View itemView, OnRecycleItemListener onRecycleItemListener) {
            super(itemView);
            this.onRecycleItemListener = onRecycleItemListener;
            item_title = itemView.findViewById(R.id.c_title);
            item_content = itemView.findViewById(R.id.c_content);
            foldingCell = itemView.findViewById(R.id.folding_cell);
            foldingCell.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: "+v.getId());
            foldingCell.toggle(false);
            onRecycleItemListener.OnItemClick(getAdapterPosition());
        }
    }

    public interface OnRecycleItemListener {
        void OnItemClick(int position);
    }
}
