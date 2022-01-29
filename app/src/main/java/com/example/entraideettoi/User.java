package com.example.entraideettoi;

import java.util.Date;

public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String city;
    private String postalCode;
    private String pseudo;
    private String tel;
    private String birth;

    //  Constructor

    /**
     * Constructor if User() initialise the atribut for this class
     */

    public User() {
        this.firstName = "firstName";
        this.lastName = "lastName";
        this.email = "email";
        this.password = "password";
        this.address = "address";
        this.city = "city";
        this.postalCode = "postalCode";
        this.pseudo = "pseudo";
        this.tel = "tel";
        this.birth ="1900, 00, 11";
    }

    public User(String firstName, String lastName, String email, String password, String address, String city, String postalCode, String pseudo, String tel, String birth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.pseudo = pseudo;
        this.tel = tel;
        this.birth = birth;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Assessor


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }


}
