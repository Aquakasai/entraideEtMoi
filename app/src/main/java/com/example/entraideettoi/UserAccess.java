package com.example.entraideettoi;

import java.util.Date;

/**
 * @version 1.0
 * @author Dumondelle Maxime
 * @author Artillan Dorian
 * @author Carlini Olivier
 *
 * Permet de rendre la classe
 */
public final class UserAccess {

    static String id = "default";
    static String firstName = "firstName";
    static String lastName = "lastName";
    static String email = "email";
    static String address = "address";
    static String city = "city";
    static String postalCode = "postalCode";
    static String pseudo = "pseudo";
    static String tel = "tel";
    static Date birth = new Date(1944, 10, 16);
    static boolean admin = false;


    public UserAccess() {
    }


    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        UserAccess.id = id;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        UserAccess.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        UserAccess.lastName = lastName;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserAccess.email = email;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        UserAccess.address = address;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        UserAccess.city = city;
    }

    public static String getPostalCode() {
        return postalCode;
    }

    public static void setPostalCode(String postalCode) {
        UserAccess.postalCode = postalCode;
    }

    public static String getPseudo() {
        return pseudo;
    }

    public static void setPseudo(String pseudo) {
        UserAccess.pseudo = pseudo;
    }

    public static String getTel() {
        return tel;
    }

    public static void setTel(String tel) {
        UserAccess.tel = tel;
    }

    public static Date getBirth() {
        return birth;
    }

    public static void setBirth(Date birth) {
        UserAccess.birth = birth;
    }

    public static boolean isAdmin() {
        return admin;
    }

    public static void setAdmin(boolean admin) {
        UserAccess.admin = admin;
    }

    public static int getBirthYearInt(){
        return getBirth().getYear() + 1900;
    }
}
