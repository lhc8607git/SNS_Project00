package com.example.sns_project00;

public class UserInfo {
    private String name;
    private String phonenum;
    private String birthday;
    private String address;
    private String photoUrl;

    public UserInfo(String name, String phonenum, String birthday, String address, String photoUrl) {
        this.name = name;
        this.phonenum = phonenum;
        this.birthday = birthday;
        this.address = address;
        this.photoUrl=photoUrl;
    }

    public UserInfo(String name, String phonenum, String birthday, String address) {
        this.name = name;
        this.phonenum = phonenum;
        this.birthday = birthday;
        this.address = address;

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhonenum() {
        return phonenum;
    }
    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
