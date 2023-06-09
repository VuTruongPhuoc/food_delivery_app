package com.example.app2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.app2.Interface.Database.Database;
import com.example.app2.Model.Food;
import com.example.app2.Model.Order;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    Button btnCart;
    ElegantNumberButton elegantNumberButton;
    String foodId;
    Food currentFood;
    FirebaseDatabase database;
    DatabaseReference foods;

    RadioGroup radioFoodSize;
    RadioButton radioFoodButton;
    String price = "Chọn size";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        elegantNumberButton = findViewById(R.id.e_number_button);
        btnCart = findViewById(R.id.btnCart);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(FoodDetails.this, Cart.class);
                startActivity(cartIntent);
            }
        });
        radioFoodSize = findViewById(R.id.food_size);
        radioFoodSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioFoodButton = findViewById(checkedId);
                if(radioFoodButton.getText().equals("Nhỏ"))
                {
                    price = currentFood.getPrice();
                }
                else if(radioFoodButton.getText().equals("Vừa"))
                {
                    price = String.valueOf(Double.parseDouble(currentFood.getPrice())*1.5);
                }
                else
                    price = String.valueOf(Double.parseDouble(currentFood.getPrice())*2);
                getDetailFood(foodId);
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioFoodSize.getCheckedRadioButtonId();
                radioFoodButton = findViewById(selectedId);
                if(radioFoodButton == null)
                {
                    Toast.makeText(FoodDetails.this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                }
                else{
                    new Database(getBaseContext()).addToCart(new Order(
                            foodId,
                            currentFood.getName(),
                            elegantNumberButton.getNumber(),
                            price,
                            currentFood.getDiscount()
                    ));
                    Toast.makeText(FoodDetails.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                }

            }
        });
        food_description = findViewById(R.id.food_description);
        food_image = findViewById(R.id.img_food);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);

        if(getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");
        }

        if(!foodId.isEmpty()) {
            getDetailFood(foodId);
        }
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                Picasso.get().load(currentFood.getImage()).into(food_image);
                if(price.charAt(0) == 'C')
                    food_price.setText(price);
                else
                    food_price.setText(price + '\u20AB');
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
