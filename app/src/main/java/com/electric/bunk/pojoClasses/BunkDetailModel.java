package com.electric.bunk.pojoClasses;

public class BunkDetailModel {

    String   bunkAddress,bunkName, chargingSlots, lati, longi;

    public String getBunkAddress() {
        return bunkAddress;
    }

    public void setBunkAddress(String bunkAddress) {
        this.bunkAddress = bunkAddress;
    }

    public String getBunkName() {
        return bunkName;
    }

    public void setBunkName(String bunkName) {
        this.bunkName = bunkName;
    }

    public String getChargingSlots() {
        return chargingSlots;
    }

    public void setChargingSlots(String chargingSlots) {
        this.chargingSlots = chargingSlots;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }
}
