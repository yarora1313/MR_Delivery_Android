package com.example.mrdelivery.outletactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.mrdelivery.MainActivity;
import com.example.mrdelivery.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutletsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*Yumpies -> Dosa
Fruitful -> Fruit Wizard
c3 -> Home cooked*/

public class OutletsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static CurrentCustomer currentUser;
    private CardView yummpys,c3,fruitful;
    private MaterialButton dosaRate, fruitRate, homeRate;
    private HashMap<String, RatingBar> outletRatings = new HashMap<>();
    private TextView nameText,settingText;

    public OutletsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutletsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutletsFragment newInstance(String param1, String param2) {
        OutletsFragment fragment = new OutletsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outlets, container, false);
        fruitful = view.findViewById(R.id.FruitFul);
        c3 = view.findViewById(R.id.c3);
        yummpys = view.findViewById(R.id.Dosa);

        outletRatings.put("Dosa Palace", (RatingBar) view.findViewById(R.id.dosaRate));
        outletRatings.put("Fruit Wizard", (RatingBar) view.findViewById(R.id.fruitRate));
        outletRatings.put("Home Cooked", (RatingBar) view.findViewById(R.id.homeRate));
        Rating.getRatings(outletRatings);

        dosaRate = view.findViewById(R.id.dosaRateButt);
        fruitRate = view.findViewById(R.id.fruitRateButt);
        homeRate = view.findViewById(R.id.homeRateButt);

        dosaRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingPrompt("Dosa Palace");
            }
        });

        fruitRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingPrompt("Fruit Wizard");
            }
        });

        homeRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingPrompt("Home Cooked");
            }
        });


        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), OutletMenu.class);
                in.putExtra("OUTLET_TYPE", "C3");
                startActivity(in);
            }
        });
        fruitful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), OutletMenu.class);
                in.putExtra("OUTLET_TYPE", "FRUITFUL");
                startActivity(in);
            }
        });

        yummpys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), OutletMenu.class);
                in.putExtra("OUTLET_TYPE", "YUMMPYS");
                startActivity(in);
            }
        });

        return view;
    }

    private void showRatingPrompt(final String outlet)
    {
        AlertDialog.Builder setRating = new AlertDialog.Builder(getContext());
        setRating.setTitle("Rate outlet:");

        LinearLayout linLay = new LinearLayout(getContext());

        final RatingBar rate = new RatingBar(getContext());
        rate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        rate.setStepSize(0.1f);
        rate.setScaleX(0.7f);
        rate.setScaleY(0.7f);
        rate.setNumStars(5);

        linLay.addView(rate);
        setRating.setView(linLay);


        setRating.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateRating(outlet, rate.getRating());
                Toast.makeText(getContext(),"Rating added",Toast.LENGTH_SHORT).show();
            }
        });
        setRating.show();
    }

    private void updateRating(String outlet, final float rating){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Outlets").child(outlet);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long currNum = (long) dataSnapshot.child("Rate Num").getValue();
                double currRating = (double) dataSnapshot.child("Rating").getValue();

                currRating = ((currRating * currNum) + (double) rating)/(currNum + 1);

                rootRef.child("Rate Num").setValue(currNum + 1);
                rootRef.child("Rating").setValue(currRating);

                Rating.getRatings(outletRatings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
