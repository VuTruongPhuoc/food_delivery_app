package com.example.app2.ViewHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.app2.Cart;
import com.example.app2.Interface.ItemClickListener;
import com.example.app2.Interface.Database.Database;
import com.example.app2.Interface.OnCartItemChangedListener;
import com.example.app2.Model.Order;
import com.example.app2.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txt_cart_name, txt_price, txt_cart_size, txtTotalPrice;
    public ImageView img_cart_count;
    public ElegantNumberButton enbutton;
    private ItemClickListener itemClickListener;


    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_cart_size = itemView.findViewById(R.id.card_item_Size);
        txt_price = itemView.findViewById(R.id.cart_item_Price);
        enbutton = itemView.findViewById(R.id.e_number_button);
        txtTotalPrice = itemView.findViewById(R.id.total);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(itemClickListener != null)
        {
            itemClickListener.onClick(view, getAdapterPosition(), false);
            txtTotalPrice.setText("");
        }
    }
}
public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private List<Order> listData = new ArrayList<>();
    private Context context;
    private TextView txtTP;
    private OnCartItemChangedListener listener;
    public CartAdapter(List<Order> listData, Context context, TextView txtTotalPrice, OnCartItemChangedListener listener) {
        this.listData = listData;
        this.context = context;
        this.txtTP = txtTotalPrice;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRect(""+ listData.get(position).getQuantity(), Color.TRANSPARENT);
//        holder.img_cart_count.setImageDrawable(drawable);
        holder.enbutton.setNumber(listData.get(position).getQuantity());
        holder.enbutton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order updatedOrder = listData.get(position);
                updatedOrder.setQuantity(String.valueOf(newValue));
                updatedOrder.setPrice(String.valueOf(Double.parseDouble(listData.get(position).getPrice())));
                // cập nhật giá trị mới vào database
                new Database(view.getContext()).updateCart(updatedOrder);
                // cập nhật hiển thị cho người dùng
                Locale locale = new Locale("vi", "VN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                double price = newValue * Double.parseDouble(updatedOrder.getPrice());
                holder.txt_price.setText(fmt.format(price));
                Toast.makeText(view.getContext(), "Số lượng: " + newValue, Toast.LENGTH_SHORT).show();
                double total = 0;
                for (Order order : listData) {
                    total += ((Double.parseDouble(order.getPrice())) * (Double.parseDouble(order.getQuantity())));
                }
                txtTP.setText(fmt.format(total));
                if (listener != null) {
                    listener.onCartItemChanged();
                }
                if( newValue == 0) {
                    Order deleteOrder = listData.get(position);
                    new Database(view.getContext()).deleteCart(deleteOrder);
                    listData.remove(position);
                    notifyDataSetChanged();
                    if (listener != null) {
                        listener.onCartItemChanged();
                    }
                }
            }
        });
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        double price = (Double.parseDouble(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }
    @Override
    public int getItemCount() {
        return listData.size();
    }
}
