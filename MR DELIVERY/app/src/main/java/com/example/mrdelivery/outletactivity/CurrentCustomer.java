package com.example.mrdelivery.outletactivity;

public class CurrentCustomer{
    private String name ;
    private String nameID ;
    private String email;
    private String mobile;
    private double latitude,longitude;
    private String orderReques;
    private String currRest;



    public CurrentCustomer(String nameID, String name, String email, String mobile) {
        this.name = name;
        this.nameID = nameID;
        this.email = email;
        this.mobile = mobile;
    }

    public void getterLatitude(double x)
    {
        latitude=x;
    }

    public void getterLongitude(double y)
    {
        longitude=y;
    }
    public double setLat()
    {
        return latitude;
    }
    public double setLongi()
    {
        return longitude;
    }
    public String getOrderReques() {
        return orderReques;
    }

    public void setOrderReques(String orderReques) {
        this.orderReques = orderReques;
    }

    public String getName() {
        return name;
    }
    public String getNameID() {
        return nameID;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setCurrentRest(String rest){
        currRest = rest;
    }

    public String getCurrentRest(){
        return currRest;
    }
}
