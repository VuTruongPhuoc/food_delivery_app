package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;

import com.example.app2.Interface.ItemClickListener;
import com.example.app2.Model.Food;
import com.example.app2.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodlist;
    String CategoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        database = FirebaseDatabase.getInstance();
        foodlist = database.getReference("Foods");

        recycler_menu = (RecyclerView) findViewById(R.id.recycler_food);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        if(getIntent() != null) {
            CategoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!CategoryId.isEmpty() && CategoryId != null) {
            loadfoodlist(CategoryId);
        }
    }
    private void loadfoodlist(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder> (Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("menuId").equalTo(categoryId)) {

                @Override
                protected void populateViewHolder(FoodViewHolder foodviewHolder, Food food, int i) {
                    foodviewHolder.foodName.setText(food.getName());
                    Picasso.with(getBaseContext()).load(food.getImage()).into(foodviewHolder.foodImage);
                    final Food local = food;
                    foodviewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent intentDetails = new Intent(FoodList.this, FoodDetails.class);
                            intentDetails.putExtra("FoodId", adapter.getRef(position).getKey());
                            startActivity(intentDetails);
                        }
                    });

                }

        };
        recycler_menu.setAdapter(adapter);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        MenuInflater inflater = new MenuInflater(this);
//        inflater.inflate(R.menu.action_menu,menu);
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }
}
