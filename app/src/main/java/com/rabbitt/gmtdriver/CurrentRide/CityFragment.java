package com.rabbitt.gmtdriver.CurrentRide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.rabbitt.gmtdriver.Adapter.CityRideAdapter;
import com.rabbitt.gmtdriver.Adapter.RecycleAdapter;
import com.rabbitt.gmtdriver.R;
import com.rabbitt.gmtdriver.Utils.Config;
import com.rabbitt.gmtdriver.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.rabbitt.gmtdriver.Preferences.prefsManager.ID_KEY;
import static com.rabbitt.gmtdriver.Preferences.prefsManager.USER_PREFS;
import static com.rabbitt.gmtdriver.SplashScreen.LOG_TAG;

public class CityFragment extends Fragment implements CityRideAdapter.OnRecycleItemListener {
    private static final String TAG = "RkCity";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private List<RecycleAdapter> data = new ArrayList<>();
    private RecycleAdapter model = null;
    private String driver_id;
    private Skeleton skeleton;


    public CityFragment() {
        // Required empty public constructor
    }


//    public static CityFragment newInstance(String param1, String param2) {
//        CityFragment fragment = new CityFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        SharedPreferences shrp = Objects.requireNonNull(getActivity()).getSharedPreferences(USER_PREFS,Context.MODE_PRIVATE);
        driver_id = shrp.getString(ID_KEY,"");


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_city, container, false);

//        skeleton = view.findViewById(R.id.skeletonLayout);

        // or create a new SkeletonLayout from a given View
//        skeleton = SkeletonLayoutUtils.createSkeleton(view);


        init(view);

        return view;
    }

    private void init(View view) {

        recyclerView = view.findViewById(R.id.cityRecycler);
//        productAdapter = new ArrayList<>();
        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
//        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.item_city, 8);
//
//        skeleton.showSkeleton();

        //setting title for toolbar

        getCityRide();
        updaterecyclershit(data);

    }



    private void getCityRide() {
        Log.i(TAG, "run:   " + Thread.currentThread().getId());

        //progressdialog until the data retrieved
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Getting your rides", "Please wait...", false, false);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_CITYRIDE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //cancel the progress dialog
                        loading.dismiss();

                        Log.i(LOG_TAG, "Responce.............." + response);
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject jb;// = arr.getJSONObject(0);
                            int n = arr.length();

                            for (int i = 0; i < n; i++) {
                                jb = arr.getJSONObject(i);
                                model = new RecycleAdapter();

                                model.setBook_id(jb.getString("book_id"));
                                model.setOrigin(jb.getString("origin"));
                                model.setDestin(jb.getString("distin"));
                                model.setTimeat(jb.getString("time_at"));
                                model.setDistance(jb.getString("distance"));
                                model.setPrefix("CTY");

                                data.clear();
                                data.add(model);
                            }
                            updaterecyclershit(data);
                        } catch (JSONException e) {
                            Log.i(LOG_TAG, "Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getActivity(), "Server is not responding", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("DRI_ID", driver_id);
                Log.i(LOG_TAG, "DRI_ID" + driver_id);
                return params;
            }
        };

        //Adding request the the queue
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

//    private void onDataLoaded() {
//        skeleton.showOriginal();
//    }
    private void updaterecyclershit(List<RecycleAdapter> data) {
        if (data != null) {
            CityRideAdapter currentRideAdapter = new CityRideAdapter(data, this, this);
            Log.i("HIteshdata", "" + data);
            RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(reLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(currentRideAdapter);
//            onDataLoaded();
        }
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        OnFragmentInteractionListener mListener = null;
    }

    @Override
    public void OnItemClick(int position) {
        Toast.makeText(getActivity(), "posi..."+position, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
