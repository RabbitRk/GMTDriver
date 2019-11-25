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

        holder.book_id_t.setText(dataModel.getBook_id());
        holder.prefix_t.setText(dataModel.getPrefix());
        holder.origin_t.setText(dataModel.getOrigin());
        holder.pickup_t.setText(dataModel.getTimeat());

        holder.book_id_c.setText(dataModel.getBook_id());
        holder.prefix_c.setText(dataModel.getPrefix());
        holder.origin_c.setText(dataModel.getOrigin());
        holder.dest_c.setText(dataModel.getDestin());
        holder.pickup_c.setText(dataModel.getTimeat());
        holder.package_c.setText(dataModel.getPackage_id());
        holder.returnd_c.setText(dataModel.getReturnD());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnRecycleItemListener onRecycleItemListener;
        TextView book_id_t, prefix_t, origin_t, pickup_t, book_id_c, prefix_c, origin_c, pickup_c, dest_c, package_c, returnd_c;
        FoldingCell foldingCell;
        holder(@NonNull View itemView, OnRecycleItemListener onRecycleItemListener) {
            super(itemView);
            this.onRecycleItemListener = onRecycleItemListener;

            book_id_t = itemView.findViewById(R.id.book_id_);
            prefix_t = itemView.findViewById(R.id.prefix_);
            origin_t = itemView.findViewById(R.id.origin_);
            pickup_t = itemView.findViewById(R.id.timeat_);

            book_id_c = itemView.findViewById(R.id.book_id__);
            prefix_c = itemView.findViewById(R.id.prefix__);
            origin_c = itemView.findViewById(R.id.origin__);
            pickup_c = itemView.findViewById(R.id.timeat__);
            dest_c = itemView.findViewById(R.id.dest_);
            package_c = itemView.findViewById(R.id.package__);
            returnd_c = itemView.findViewById(R.id.returnd_);

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
