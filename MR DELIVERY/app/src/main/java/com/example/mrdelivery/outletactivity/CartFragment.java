package com.example.mrdelivery.outletactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mrdelivery.MapsActivity;
import com.example.mrdelivery.R;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView cartText;
    private Button placeOrder;
    public static Map<String, CartItem> cartItemList = new LinkedHashMap<>();
    private int totalItems=0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        View view = inflater.inflate(R.layout.activity_cart, container, false);
        cartText = view.findViewById(R.id.cartText);
        placeOrder = view.findViewById(R.id.placeOrderBtn);

        final StringBuilder sb = new StringBuilder();

        if (cartItemList.isEmpty()) {
            cartText.setText("Your Cart is Empty !!");
            view.findViewById(R.id.textView7).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.cartText).setVisibility(View.INVISIBLE);
            placeOrder.setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.textView7).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cartEmp).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.cartText).setVisibility(View.VISIBLE);
            placeOrder.setVisibility(View.VISIBLE);
            placeOrder.setText("Proceed to payment");

            int c = 1;
            for (Map.Entry<String, CartItem> entry : cartItemList.entrySet()) {
                sb.append(c + ". " + entry.getKey() + "    ");
                sb.append("\t");
                sb.append(entry.getValue().getNumber() + " x " + entry.getValue().getPrice() + " = " + (entry.getValue().getNumber() * entry.getValue().getPrice()));
                sb.append("\n\n");
                totalItems += entry.getValue().getNumber();
                c++;
            }

            cartText.setText(sb.toString());
            Outlets.currentCustomer.setOrderReques(sb.toString());
        }

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), MapsActivity.class);
                in.putExtra("TOT_ITEMS", totalItems);
                startActivity(in);
            }
        });

        return view;
    }

    private String calculateTotal() {

        int totalAmt = 0;
        for (Map.Entry<String, CartItem> entry : cartItemList.entrySet()) {
            totalAmt += entry.getValue().getPrice();
        }
        return String.valueOf(totalAmt);

    }
}

class MyAdapterCart extends ArrayAdapter<String> {
    private Context context;

    private String[] menusaman;
    private int[] pricesaman;
    private int[] numeber;

    MyAdapterCart(Context c, String menu[], int price[]) {
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
        final View cartrow = layoutInflater.inflate(R.layout.row, parent, false);
        TextView menu = cartrow.findViewById(R.id.cartItem);
        TextView price = cartrow.findViewById(R.id.cartNumber);
        menu.setText(menusaman[position]);
        price.setText(String.valueOf(pricesaman[position]));

        return cartrow;
    }

}



