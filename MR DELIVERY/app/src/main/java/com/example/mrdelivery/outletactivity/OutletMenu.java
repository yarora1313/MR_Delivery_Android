package com.example.mrdelivery.outletactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mrdelivery.CartActivity;
import com.example.mrdelivery.R;

import java.util.Arrays;

public class OutletMenu extends AppCompatActivity {

    ListView listView;
    ImageView cartBtn;

    String[] menudosapalace = {"Plain Dosa", "Butter Dosa", "Masala Dosa", "Rawa Dosa", "Idli", "Rawa Idli", "Uttapam", "Vegetable Uttapam",
            "Onion Uttapam", "Sambhar Wada", "Ice Tea", "Cold Coffee"};
    int[] pricesdosapalace = {25, 40, 50, 45, 25, 45, 30, 60, 40, 35, 30, 35};

    String[] menuhomecooked = {"Jeera Aloo (Dry)", "Palak Aloo (dry)", "Potate Cabbage (Dry)", "Aloo Palak (Gravy)", "Bottle Guard (Gravy)", "Sweet Guard with Chana (Gravy)", "Yellow Dal", "Yellow Dal with tadka",
            "Steamed rice", "Jeera Rice", "Roti", "Butter Roti", "Paratha", "Poha", "Upma", "Lassi", "Butter Milk"};
    int[] priceshomecooked = {60, 75, 65, 65, 60, 75, 60, 70, 40, 50, 10, 15, 15, 45, 45, 25, 25};

    String[] menuFruitwizard = {"Mixed Fruit Bowl", "Pineapple Bowl", "Watermelon Bowl", "Strawberry Smoothie", "Chilled Lemon Soda", "Watermelon juice", "Mango Milkshake", "Apple MilkShake",
            "SweetLime Juice", "Litchi Juice", "Mixed Fruit Chat", "Mint Mojito"};
    int[] pricesFruitwizard = {35, 35, 35, 60, 15, 25, 50, 50, 25, 25, 50, 20};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_menu);

        final String outletType = getIntent().getStringExtra("OUTLET_TYPE");
        listView = findViewById(R.id.listyummpys);
        cartBtn = findViewById(R.id.cartGoToButton);
        MyAdapter adapter = null;

        Outlets.currentCustomer.setCurrentRest(outletType);
        CartFragment.cartItemList.clear();
        CartActivity.cartItemList.clear();
        assert outletType != null;
        if (outletType.equalsIgnoreCase("YUMMPYS")) {
            adapter = new MyAdapter(this, menudosapalace, pricesdosapalace);
        } else if (outletType.equalsIgnoreCase("C3")) {
            adapter = new MyAdapter(this, menuhomecooked, priceshomecooked);
        } else if (outletType.equalsIgnoreCase("FRUITFUL")) {
            adapter = new MyAdapter(this, menuFruitwizard, pricesFruitwizard);
        }
        listView.setAdapter(adapter);
//            final MyAdapter finalAdapter = adapter;
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent in = new Intent(getApplicationContext() , CartActivity.class);
//                    startActivity(in);
//            }
//        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartBtnFunc();
            }
        });
    }

    private void cartBtnFunc() {
        Intent in = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(in);
    }
}

class MyAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] menusaman;
    private int[] pricesaman;
    private int[] numeber;

    MyAdapter(Context c, String menu[], int price[]) {
        super(c, R.layout.row, R.id.cheez, menu);
        this.context = c;
        this.menusaman = menu;
        this.pricesaman = price;
        numeber = new int[pricesaman.length];
        Arrays.fill(this.numeber, 0);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        final View row = layoutInflater.inflate(R.layout.row, parent, false);
        TextView menu = row.findViewById(R.id.cheez);
        TextView price = row.findViewById(R.id.paisa);
        Button addBtn = row.findViewById(R.id.addButt);
        Button minusBtn = row.findViewById(R.id.minusButt);
        final TextView num = row.findViewById(R.id.number);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                numeber[position]++;
                num.setText(String.valueOf(numeber[position]));
                CartFragment.cartItemList.put(menusaman[position], new CartItem(menusaman[position], pricesaman[position], numeber[position]));
                CartActivity.cartItemList.put(menusaman[position],new CartItem(menusaman[position],pricesaman[position],numeber[position]));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numeber[position] == 0) {
                    Toast.makeText(context, "Can't remove more", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Removed from Cart", Toast.LENGTH_SHORT).show();
                    numeber[position]--;
                    num.setText(String.valueOf(numeber[position]));
                    if (numeber[position] > 0) {
                        CartFragment.cartItemList.put(menusaman[position], new CartItem(menusaman[position], pricesaman[position], numeber[position]));
                        CartActivity.cartItemList.put(menusaman[position],new CartItem(menusaman[position],pricesaman[position],numeber[position]));
                    } else {
                        CartFragment.cartItemList.remove(menusaman[position]);
                        CartActivity.cartItemList.remove(menusaman[position]);
                    }

                }

            }
        });
        num.setText(String.valueOf(numeber[position]));
        menu.setText(menusaman[position]);
        price.setText(String.valueOf(pricesaman[position]));

        return row;
    }

    public String getMenusaman(int pos) {
        return menusaman[pos];
    }

    public int getPricesaman(int pos) {
        return pricesaman[pos];
    }

    public int getNumeber(int pos) {
        return numeber[pos];
    }
}
