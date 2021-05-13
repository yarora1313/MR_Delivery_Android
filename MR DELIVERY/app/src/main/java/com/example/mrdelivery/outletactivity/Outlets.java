package com.example.mrdelivery.outletactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mrdelivery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Outlets extends AppCompatActivity {

    public static CurrentCustomer currentCustomer;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outlet_nav);
        bottomNav = findViewById(R.id.bottom_nav);

        BottomNavigationView.OnNavigationItemSelectedListener navSelectListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_outlets:
                            openFragment(OutletsFragment.newInstance("",""));
                            return true;

                        case R.id.nav_cart:
                            openFragment(CartFragment.newInstance("",""));
                            return true;

                        case R.id.nav_account_settings:
                            Bundle bundle = new Bundle();
                            bundle.putString("USER_TYPE", "CUSTOMER");
                            AccountFragment obj = AccountFragment.newInstance("","");
                            obj.setArguments(bundle);
                            openFragment(obj);
                            return true;
                    }
                    return false;
                }
            };

        bottomNav.setOnNavigationItemSelectedListener(navSelectListener);
        openFragment(OutletsFragment.newInstance("",""));
    }

    private void setProfile(){
        //nameText.setText("Hi! "+currentCustomer.getName());
    }

    private void openFragment(Fragment frag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.outletContainer, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static int getWeightCost(int items){
        if(items <= 2){
            return 20;
        }
        else if(items > 2 && items < 6){
            return 10;
        }
        else{
            return 0;
        }
    }
}

