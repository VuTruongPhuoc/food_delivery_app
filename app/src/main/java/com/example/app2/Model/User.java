package com.example.app2.Model;

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String Mail;
    public User() {

    }


    public User(String name, String password, String mail) {
        Name = name;
        Password = password;
        Mail = mail;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPassword() {
        return Password;
    }
}
