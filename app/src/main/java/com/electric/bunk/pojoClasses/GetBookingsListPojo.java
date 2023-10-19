package com.electric.bunk.pojoClasses;

public class GetBookingsListPojo {

    String addressBunk;
    String bunkName;
    String date;
    String time;
    String userRegId;
    String userUid;
    String vendorRegId;
    String vendorUid;
    String timeStamp;
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    String userPhone;
    int feedbackStatus, status;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAddressBunk() {
        return addressBunk;
    }

    public void setAddressBunk(String addressBunk) {
        this.addressBunk = addressBunk;
    }

    public String getBunkName() {
        return bunkName;
    }

    public void setBunkName(String bunkName) {
        this.bunkName = bunkName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserRegId() {
        return userRegId;
    }

    public void setUserRegId(String userRegId) {
        this.userRegId = userRegId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getVendorRegId() {
        return vendorRegId;
    }

    public void setVendorRegId(String vendorRegId) {
        this.vendorRegId = vendorRegId;
    }

    public String getVendorUid() {
        return vendorUid;
    }

    public void setVendorUid(String vendorUid) {
        this.vendorUid = vendorUid;
    }

    public int getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(int feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
