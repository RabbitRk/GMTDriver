package com.rabbitt.gmtdriver.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.gmtdriver.CurrentRide.RentalFragment;
import com.rabbitt.gmtdriver.R;
import com.ramotion.foldingcell.FoldingCell;

import java.util.List;

public class RentalRideAdapter extends RecyclerView.Adapter<RentalRideAdapter.holder> {

    private static final String TAG = "CurrentRideAdapter";
    private List<RecycleAdapter> dataModelArrayList;
    private RentalFragment context;
    private OnRecycleItemListener mOnRecycleItemListener;

    public RentalRideAdapter(List<RecycleAdapter> productAdapter, RentalFragment context, OnRecycleItemListener onRecycleItemListener)
    {
        this.dataModelArrayList = productAdapter;
        this.context = context;
        this.mOnRecycleItemListener = onRecycleItemListener;
        Log.i(TAG, dataModelArrayList.toString());
    }

    @NonNull
    @Override
    public RentalRideAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental, parent,false);
        return new holder(view, mOnRecycleItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalRideAdapter.holder holder, int i) {
        RecycleAdapter dataModel = dataModelArrayList.get(i);

        Log.i(TAG, "onBindViewHolder: "+dataModel.getBook_id()+"  -  "+dataModelArrayList.get(i));
        holder.book_id_t.setText(dataModel.getBook_id());
//        holder.prefix_t.setText(dataModel.getPrefix());
        holder.origin_t.setText(dataModel.getOrigin());
        holder.pick_time_t.setText(dataModel.getTimeat());

        holder.book_id_c.setText(dataModel.getBook_id());
//        holder.prefix_c.setText(dataModel.getPrefix());
        holder.origin_c.setText(dataModel.getOrigin());
//        holder.destin_c.setText(dataModel.getDestin());
        holder.pick_time_c.setText(dataModel.getTimeat());
        holder.package_c.setText(dataModel.getPackage_id());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    class holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnRecycleItemListener onRecycleItemListener;
        TextView book_id_t, pick_time_t, origin_t;
        TextView book_id_c, pick_time_c, origin_c, package_c;
        FoldingCell foldingCell;

        holder(@NonNull View itemView, OnRecycleItemListener onRecycleItemListener) {
            super(itemView);
            this.onRecycleItemListener = onRecycleItemListener;

            book_id_t = itemView.findViewById(R.id.book_id_);
            origin_t = itemView.findViewById(R.id.origin_);
            pick_time_t = itemView.findViewById(R.id.timeat_);

            book_id_c = itemView.findViewById(R.id.book_id__);
            origin_c = itemView.findViewById(R.id.origin__);
            pick_time_c = itemView.findViewById(R.id.timeat__);
            package_c = itemView.findViewById(R.id.package__);

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
