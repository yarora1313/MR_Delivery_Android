package com.example.mrdelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mrdelivery.outletactivity.CartItem;
import com.example.mrdelivery.outletactivity.Outlets;

import java.util.LinkedHashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    TextView cartText ;
    Button placeOrder;
    public static Map<String , CartItem> cartItemList = new LinkedHashMap<>();
    private int totalItems=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartText =findViewById(R.id.cartText);
        placeOrder=findViewById(R.id.placeOrderBtn);


        final StringBuilder sb = new StringBuilder();
        if(cartItemList.isEmpty()){
            cartText.setText("Your Cart is Empty !!");
            findViewById(R.id.textView7).setVisibility(View.INVISIBLE);
            findViewById(R.id.cartText).setVisibility(View.INVISIBLE);
            placeOrder.setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.textView7).setVisibility(View.VISIBLE);
            findViewById(R.id.cartEmp).setVisibility(View.INVISIBLE);
            findViewById(R.id.cartText).setVisibility(View.VISIBLE);
            placeOrder.setVisibility(View.VISIBLE);
            placeOrder.setText("Proceed to payment");

            int c=1;
            for(Map.Entry<String,CartItem> entry : cartItemList.entrySet()){
                sb.append(c+". "+entry.getKey()+"    ");
                sb.append("\t");
                sb.append(entry.getValue().getNumber()+" x "+ entry.getValue().getPrice() + " = "+ (entry.getValue().getNumber()*entry.getValue().getPrice()));
                sb.append("\n\n");
                totalItems += entry.getValue().getNumber();
                c++;
            }

            cartText.setText(sb.toString());
            Outlets.currentCustomer.setOrderReques(sb.toString());
        }



        /***      IMPORTANT
         *   MAP PAGE NEXT INTENT
         * Ask Location on Map page
         * and Store it in Outlets.currentCustomer.setLocation(location);
         * change the data type as per need
         * Delete Below Line After completing Location
         *   ALSO SEE PAYMENTACTIVITY.JAVA Line:138 (for DATATYPE TO BE STORED IN DATABASE)
         * ***/

            placeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(),MapsActivity.class);
                    in.putExtra("TOT_ITEMS", totalItems);
                    startActivity(in);
                }
            });
    }

    private String calculateTotal(){

        int totalAmt=0;
        for(Map.Entry<String,CartItem> entry : cartItemList.entrySet()){
            totalAmt+=entry.getValue().getNumber()*entry.getValue().getPrice();
        }
        return String.valueOf(totalAmt);
    }


}

/*public class CartItem{

    private String name;
    private int price;
    private int number;

    public CartItem(String name, int price, int number) {
        this.name = name;
        this.price = price;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getNumber() {
        return number;
    }
}*/

/*class MyAdapterCart extends ArrayAdapter<String>
{
    private Context context;

    private String[] menusaman;
    private int[] pricesaman;
    private int[] numeber ;

    MyAdapterCart(Context c,String menu[],int price[])
    {
        super(c,R.layout.row,R.id.cheez,menu);
        this.context=c;
        this.menusaman=menu;
        this.pricesaman=price;
        numeber= new int[pricesaman.length];
        Arrays.fill(this.numeber, 0);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater=(LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        final View cartrow = layoutInflater.inflate(R.layout.row,parent,false);
        TextView menu=cartrow.findViewById(R.id.cartItem);
        TextView price=cartrow.findViewById(R.id.cartNumber);
        menu.setText(menusaman[position]);
        price.setText(String.valueOf(pricesaman[position]));

        return cartrow;
    }

}*/
