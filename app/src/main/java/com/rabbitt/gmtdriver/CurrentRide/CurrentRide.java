package com.rabbitt.gmtdriver.CurrentRide;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.rabbitt.gmtdriver.Adapter.RecycleAdapter;
import com.rabbitt.gmtdriver.DBHelper.dbHelper;
import com.rabbitt.gmtdriver.MapActivity.MapsActivity;
import com.rabbitt.gmtdriver.Preferences.prefsManager;
import com.rabbitt.gmtdriver.R;
import com.rabbitt.gmtdriver.Utils.Config;
import com.rabbitt.gmtdriver.VolleySingleton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.BOOK_ID;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.DEST_LAT;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.DEST_LNG;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.DROP;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.NOTIFY_SHARED_PREFS;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.ORI_LAT;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.ORI_LNG;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.PACKAGE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.PICKUP;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.TIME;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.TYPE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.VEHICLE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.VEHICLE_ID;
import static com.rabbitt.gmtdriver.Preferences.prefsManager.ID_KEY;
import static com.rabbitt.gmtdriver.Preferences.prefsManager.LOG_STATUS;
import static com.rabbitt.gmtdriver.Preferences.prefsManager.USER_PREFS;

public class CurrentRide extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CurrentRide";
    private static final String LOG_TAG = "MainRk";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    List<RecycleAdapter> rideAdapter = new ArrayList<>();
    RecycleAdapter model = null;
    RecyclerView recyclerView;
    dbHelper database;
    String driver_id;
    prefsManager prefsManager;


    Ringtone ringtone;
    SharedPreferences shrp;

    String book_id,type,vehicle,pickup,drop,time,package_type,ori_lat,ori_lng,dest_lat,dest_lng,vehicle_id;
    Toolbar toolbar;

    TextView book_idTxt, typeTxt, vehicleTxt, package_idTxt, pickupTxt, dropTxt, timeTxt, vehcile_no;
    RelativeLayout drop_layout, package_layout;
    View drop_line, package_line;
    CardView data_avail, no_data_avail;

    Button decline, accept;

    public static final String oriLata = "orilat", oriLnga = "orilng";
    public static final String desLata = "deslat", desLnga = "deslng";

    public static final String typeI = "type";
    public static final String vehicleI = "vehicle";
    public static final String packageI = "package";

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

        SharedPreferences shrp = Objects.requireNonNull(getActivity()).getSharedPreferences(USER_PREFS,Context.MODE_PRIVATE);
        driver_id = shrp.getString(ID_KEY, "");

        prefsManager = new prefsManager(Objects.requireNonNull(getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_ride, container, false);

//        recyclerView = view.findViewById(R.id.c_recycler);
//        rideAdapter = new ArrayList<>();

//        database = new dbHelper(getContext());

//        database.insertdata("1", "11.09.2108", "Cuddalore", "Cuddalore", "1", "CTY","04-12-2019"); 

//        rideAdapter = database.getdata();

//        updaterecyclershit(rideAdapter);
        init(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.tool_switch, menu);
//        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.tool_switch, menu);

        MenuItem item = menu.findItem(R.id.login_menu);
        item.setActionView(R.layout.switch_btn);

        //getting shared prefs for login or logout
        SharedPreferences shrp = getContext().getSharedPreferences("USER_DETAILS",MODE_PRIVATE);
        String val = shrp.getString(LOG_STATUS,"");
        Log.i(TAG, "RkBtn: "+val);

        Switch mySwitch = item.getActionView().findViewById(R.id.switchForActionBar);

        if (val.equals("1"))
            mySwitch.setChecked(true);
        else
            mySwitch.setChecked(false);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something based on isChecked
                if (isChecked)
                {
                    Log.i(TAG, "onCheckedChanged:if "+isChecked);
                    prefsManager.status("1");
                    setDriverStatus("1");
                }
                else
                {
                    Log.i(TAG, "onCheckedChanged:else "+isChecked);
                    prefsManager.status("0");
                    setDriverStatus("0");
                }
            }
        });

    }


    private void init(final View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

//        new Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        PagerAdapter adapter = new PagerAdapter(getFragmentManager());
//                        ViewPager viewPager = view.findViewById(R.id.pager);
//                        TabLayout tabLayout = view.findViewById(R.id.tablayout);
//
//                        adapter.AddFragment(new CityFragment(), getParentFragment());
//                        adapter.AddFragment(new RentalFragment(), getParentFragment());
//                        adapter.AddFragment(new OutStationFragment(), getParentFragment());
//
//                        viewPager.setAdapter(adapter);
//                        tabLayout.setupWithViewPager(viewPager);
//
//                        tabLayout.getTabAt(0).setText("City");       //setIcon(R.drawable.ic_taxi)
//                        tabLayout.getTabAt(1).setText("Rental");     //setIcon(R.drawable.ic_taxi)
//                        tabLayout.getTabAt(2).setText("Outstation"); //setIcon(R.drawable.ic_taxi)
//                    }
//                }, 100);

        toolbar.setTitle("Ride");

        //Textview initialization
        book_idTxt = view.findViewById(R.id.book_id);
        typeTxt = view.findViewById(R.id.type);
        vehicleTxt = view.findViewById(R.id.vehcile);
        package_idTxt = view.findViewById(R.id.package_id);
        pickupTxt = view.findViewById(R.id.pickup);
        dropTxt = view.findViewById(R.id.drop);
        timeTxt = view.findViewById(R.id.timeat);
        vehcile_no = view.findViewById(R.id.vehcile_no);

        //view initialization
        drop_layout = view.findViewById(R.id.drop_area);
        drop_line = view.findViewById(R.id.drop_line);
        package_layout = view.findViewById(R.id.package_area);
        package_line = view.findViewById(R.id.package_line);

        //button initialization
        decline = view.findViewById(R.id.decline);
        accept = view.findViewById(R.id.accept);

        //cardview initialization
        data_avail = view.findViewById(R.id.data_avail);
        no_data_avail = view.findViewById(R.id.not_avail);

        //getting values from the sharedprefs
        shrp = getActivity().getSharedPreferences(NOTIFY_SHARED_PREFS, MODE_PRIVATE);
        book_id = shrp.getString(BOOK_ID, "0");
        type = shrp.getString(TYPE, "0");
        vehicle = shrp.getString(VEHICLE, "0");
        pickup = shrp.getString(PICKUP, "0");
        drop = shrp.getString(DROP, "0");
        time = shrp.getString(TIME, "0");
        package_type = shrp.getString(PACKAGE, "0");
        ori_lat = shrp.getString(ORI_LAT, "0");
        ori_lng = shrp.getString(ORI_LNG, "0");
        dest_lat = shrp.getString(DEST_LAT, "0");
        dest_lng = shrp.getString(DEST_LNG, "0");
        vehicle_id = shrp.getString(VEHICLE_ID, "0");

        //setting the info to the job alert page
        book_idTxt.setText(book_id);
        typeTxt.setText(type);
        vehicleTxt.setText(vehicle);
        pickupTxt.setText(pickup);
        dropTxt.setText(drop);
        timeTxt.setText(time);
        package_idTxt.setText(package_type);
        vehcile_no.setText(vehicle_id);

        //visiblity controller
        switch (type)
        {
            case "rental":
                drop_layout.setVisibility(View.GONE);
                drop_line.setVisibility(View.GONE);
                break;
            case "city":
            case "outstation":
                package_layout.setVisibility(View.GONE);
                package_line.setVisibility(View.GONE);
                break;
        }

        Log.i(TAG, book_id+" "+type+" "+vehicle+" "+pickup+" "+drop+" "+time);

//        decline.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAvailability();
            }
        });

        if (book_id.equals("0") && type.equals("0"))
        {
            data_avail.setVisibility(View.GONE);
            no_data_avail.setVisibility(View.VISIBLE);
        }
    }

    private void checkAvailability() {

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Getting your rides", "Please wait...", false, false);

        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHECK_AVAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                if (response.equals("success"))
                {
                    Toast.makeText(getActivity(), "You can take the ride", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    intent.putExtra(oriLata, ori_lat);
                    intent.putExtra(oriLnga, ori_lng);
                    intent.putExtra(desLata, dest_lat);
                    intent.putExtra(desLnga, dest_lng);
                    intent.putExtra(typeI, type);
                    intent.putExtra(vehicleI, vehicle);
                    intent.putExtra(packageI, package_type);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getActivity(), "Sorry the ride has been taken", Toast.LENGTH_SHORT).show();
                    data_avail.setVisibility(View.GONE);
                    no_data_avail.setVisibility(View.VISIBLE);
                    SharedPreferences shrp = Objects.requireNonNull(getActivity()).getSharedPreferences(NOTIFY_SHARED_PREFS,MODE_PRIVATE);
                    shrp.edit().clear().apply();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("BOOK_ID", book_id);
                params.put("TRAVEL_TYPE", type);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onAttach(@NotNull Context context) {
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
//        mListener = null;
    }


    private void setDriverStatus(final String status) {
        Log.i(TAG, "run:   " + Thread.currentThread().getId());

        //progressdialog until the data retrieved
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Updating your status", "Please wait...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.STATUS_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //cancel the progress dialog
                        loading.dismiss();
                        if (response.equals("success"))
                        {
                            if (status.equals("1"))
                            {
                                Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "Can't update your request", Toast.LENGTH_SHORT).show();
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
                params.put("STATUS", status);
                return params;
            }
        };

        //Adding request the the queue
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
