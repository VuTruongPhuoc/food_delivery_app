package com.example.app2;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app2.Common.Common;
import com.example.app2.Interface.ItemClickListener;
import com.example.app2.Model.Food;
import com.example.app2.Service.ListenOrder;
import com.example.app2.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference foodlist;
    TextView txtfullName;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    RecyclerView recycler_item;
    RecyclerView.LayoutManager layoutManager;
    EditText edtSearch;
    Button btnSearch;
    ImageView ivAvatar;
    private ConnectReceiver receiver;
    IntentFilter intentFilter;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        foodlist = database.getReference("Foods");
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.Open, R.string.Close);
        drawer.setDrawerListener(toggle);

        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
        )
                .setDrawerLayout(drawer)
                .build();
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ivAvatar = headerView.findViewById(R.id.ivAvatar);
        txtfullName = headerView.findViewById(R.id.txtfullName);
        txtfullName.setText(Common.currentUser.getName());
        txtfullName.setTextSize(20);

        recycler_item = (RecyclerView) findViewById(R.id.recycler_item);
        recycler_item.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_item.setLayoutManager(layoutManager);

        loadfoodlist();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadsearchfood(edtSearch.getText().toString());
                if(edtSearch.getText().toString().equals("")){
                    loadfoodlist();
                }
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadsearchfood(edtSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });
        receiver = new ConnectReceiver();
        intentFilter = new IntentFilter("com.example.food_delivery_app.SOME_ACTION");
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver,intentFilter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                // Hiển thị hình ảnh đã chọn trong ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadsearchfood(String item) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder> (Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("name").startAt(item).endAt(item + "\uf8ff")) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.foodImage);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(Home.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });

            }

        };
        recycler_item.setAdapter(adapter);
    }
    private void loadfoodlist() {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder> (Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist)
        {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.foodImage);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(Home.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });

            }

        };
        recycler_item.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(receiver,intentFilter);
//    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @SuppressWarnings("Statements With empty Body")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);
        }
        else if (id == R.id.nav_menu) {
            Intent menuIntent = new Intent(Home.this, MenuList.class);
            startActivity(menuIntent);
        }
        else if (id == R.id.nav_orders) {
//            Toast.makeText(getApplicationContext(),"in home",Toast.LENGTH_SHORT);
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
        }else if(id == R.id.nav_contacts) {
            Intent contact = new Intent(Home.this, Contacts.class);
            startActivity(contact);
        }
        else {
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
