package com.rabbitt.gmtdriver.CurrentRide;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rabbitt.gmtdriver.Adapter.CurrentRideAdapter;
import com.rabbitt.gmtdriver.Adapter.RecycleAdapter;
import com.rabbitt.gmtdriver.DBHelper.dbHelper;
import com.rabbitt.gmtdriver.DBHelper.recycleAdapter;
import com.rabbitt.gmtdriver.R;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;

public class CurrentRide extends Fragment implements CurrentRideAdapter.OnRecycleItemListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CurrentRide";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    List<RecycleAdapter> rideAdapter = new ArrayList<>();
    RecycleAdapter model = null;
    CurrentRideAdapter recycleadapter;
    RecyclerView recyclerView;
    dbHelper database;

    public CurrentRide() {
        // Required empty public constructor
    }

    public static CurrentRide newInstance(String param1, String param2) {
        CurrentRide fragment = new CurrentRide();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_ride, container, false);
        recyclerView = view.findViewById(R.id.c_recycler);
        rideAdapter = new ArrayList<>();

        database = new dbHelper(getContext());

//        database.insertdata("1", "11.09.2108", "Cuddalore", "Cuddalore", "1", "CTY","04-12-2019"); 

        rideAdapter = database.getdata();

//        final FoldingCell fc = view.findViewById(R.id.folding_cell);
//
//
//        // attach click listener to folding cell
//        fc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fc.toggle(false);
//            }
//        });

        updaterecyclershit(rideAdapter);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updaterecyclershit(List<RecycleAdapter> datam) {

//        for (int i = 0; i < 5; i++) {
//            model = new RecycleAdapter();
//            model.setTitle(String.valueOf(i));
//            //url to be included
//            model.setContent(String.valueOf(i));
//            data.add(model);
//        }
        Log.i(TAG, "Current thread:update " + Thread.currentThread().getId());
        if (datam != null) {

            recycleadapter = new CurrentRideAdapter(datam, this, this);
            Log.i("HIteshdata", "" + datam);
            LinearLayoutManager reLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(reLayoutManager);
            reLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(recycleadapter);
            recycleadapter.notifyDataSetChanged();
        }
    }

    @Override
    public void OnItemClick(int position) {
        Log.i(TAG, "OnItemClick: "+position);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
