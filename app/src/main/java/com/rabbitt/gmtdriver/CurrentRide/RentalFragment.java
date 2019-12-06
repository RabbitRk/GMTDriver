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
import com.rabbitt.gmtdriver.Adapter.CityRideAdapter;
import com.rabbitt.gmtdriver.Adapter.RecycleAdapter;
import com.rabbitt.gmtdriver.Adapter.RentalRideAdapter;
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

import static com.rabbitt.gmtdriver.Preferences.prefsManager.USER_PREFS;
import static com.rabbitt.gmtdriver.SplashScreen.LOG_TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RentalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RentalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RentalFragment extends Fragment implements RentalRideAdapter.OnRecycleItemListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "RkRental";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private List<RecycleAdapter> data = new ArrayList<>();
    private RecycleAdapter model = null;
    private String driver_id;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RentalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RentalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RentalFragment newInstance(String param1, String param2) {
        RentalFragment fragment = new RentalFragment();
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

        SharedPreferences shrp = Objects.requireNonNull(getActivity()).getSharedPreferences(USER_PREFS,Context.MODE_PRIVATE);
        driver_id = shrp.getString("","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rental, container, false);
        init(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.rentalRecycler);
//        productAdapter = new ArrayList<>();
        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
//        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.item_city, 8);
//
//        skeleton.showSkeleton();

        //setting title for toolbar

        getRentalRide();
        updaterecyclershit(data);
    }

    private void updaterecyclershit(List<RecycleAdapter> data) {
        if (data != null) {
            RentalRideAdapter rentalRideAdapter = new RentalRideAdapter(data, this, this);
            Log.i("HIteshdata", "" + data);
            RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(reLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(rentalRideAdapter);
//            onDataLoaded();
        }
    }

    private void getRentalRide() {
        Log.i(TAG, "run:   " + Thread.currentThread().getId());

        //progressdialog until the data retrieved
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Getting your rides", "Please wait...", false, false);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_RENTALRIDE,
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
                                model.setTimeat(jb.getString("time_at"));
                                model.setPackage_id(jb.getString("package"));
//                                model.setPrefix("CTY");

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        mListener = null;
    }

    @Override
    public void OnItemClick(int position) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
