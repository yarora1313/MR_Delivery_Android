package com.example.mrdelivery.outletactivity;

import android.widget.RatingBar;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

class Rating {
    static void getRatings(final HashMap<String, RatingBar> ratings){

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Outlets");

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                    RatingBar rateRest = ratings.get(snapShot.getKey());
                    double rate = (double) snapShot.child("Rating").getValue();
                    float frate = (float) rate;

                    assert rateRest != null;
                    rateRest.setStepSize(0.01f);
                    rateRest.setRating(frate);
                    rateRest.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
