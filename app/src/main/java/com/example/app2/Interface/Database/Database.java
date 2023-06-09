package com.example.app2.Interface.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.app2.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "EatItDB.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }


    @SuppressLint("Range")
    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ProductId", "ProductName", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null,null);
        final List<Order> result = new ArrayList<>();
        if(c != null)
        {
            if(c.moveToFirst()) {
                do {
                    result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                            c.getString(c.getColumnIndex("ProductName")),
                            c.getString(c.getColumnIndex("Quantity")),
                            c.getString(c.getColumnIndex("Price")),
                            c.getString(c.getColumnIndex("Discount"))
                    ));
                } while(c.moveToNext());
            }
        }
        return result;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount) VALUES ('%s', '%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }
    public void updateCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = '%s', Price = '%s' WHERE ProductId = '%s'",
                order.getQuantity(),
                order.getPrice(),
                order.getProductId());
        db.execSQL(query);
    }
    public void deleteCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE ProductId='%s'", order.getProductId());
        db.execSQL(query);
    }
    public void cleanCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

}
