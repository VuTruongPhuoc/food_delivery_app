package com.example.app2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.app2.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    MaterialEditText medtname, medtpassword, medtphone, medtmail;
    Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        medtname = (MaterialEditText) findViewById(R.id.edtname);
        medtpassword = (MaterialEditText) findViewById(R.id.edtpassword);
        medtphone = (MaterialEditText) findViewById(R.id.edtphone);
        medtmail = (MaterialEditText) findViewById(R.id.edtmail);
        signup = (Button) findViewById(R.id.signUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference customer = database.getReference("User");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(medtname.getText().toString().isEmpty() || medtphone.getText().toString().isEmpty() || medtmail.getText().toString().isEmpty() || medtpassword.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                customer.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!isPhoneNumberValid(medtphone.getText().toString())) {
                            Toast.makeText(SignUp.this, "Số điện thoại không hợp lệ.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(dataSnapshot.hasChild(medtphone.getText().toString())) {
                            Toast.makeText(SignUp.this, "Số điện thoại này đã được đăng ký.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            if(!isEmailValid((medtmail.getText().toString())))
                            {
                                Toast.makeText(SignUp.this, "Địa chỉ email không hợp lệ.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else if(!isPasswordValid(medtpassword.getText().toString())) {
                                Toast.makeText(SignUp.this, "Mật khẩu không hợp lệ. Mật khẩu bao gồm: 1 chữ số, 1 chữ thường, 1 chữ hoa, 1 ký hiệu đặc biệt, độ dài tối thiểu là 6 ký tự", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else {
                                User user = new User(medtname.getText().toString(), medtpassword.getText().toString(), medtmail.getText().toString());
                                    customer.child(medtphone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Bạn đã đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                    public boolean isPhoneNumberValid(final String phoneNumber){
                        Pattern pattern = Pattern.compile("^0\\d{9}$");
                        Matcher matcher = pattern.matcher(phoneNumber);
                        return matcher.matches();
                    }
                    public boolean isPasswordValid (final String password){
                        Pattern pattern = Pattern.compile(".{6,15}");
                        Matcher matcher = pattern.matcher(password);
                        return matcher.matches();
                    }

                    boolean isEmailValid (CharSequence email)
                    {
                        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
                    }
                });
            }
        });
    }
}