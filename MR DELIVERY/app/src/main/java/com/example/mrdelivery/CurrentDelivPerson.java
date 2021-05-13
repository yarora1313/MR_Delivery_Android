package com.example.mrdelivery;

public class CurrentDelivPerson{
    private String nameID;
    private String name ;
    private String email;
    private String mobile;
    private String location;
    private String orderReques;



    public CurrentDelivPerson(String nameID , String name, String email, String mobile) {
        this.nameID=nameID;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
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

    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobile() {
        return mobile;
    }


}
