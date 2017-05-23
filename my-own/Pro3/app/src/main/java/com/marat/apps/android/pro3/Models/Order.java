package com.marat.apps.android.pro3.Models;

public class Order {

    private int orderID;
    private String carWashName;
    private String orderCarType;
    private String orderServices;
    private String orderTime;
    private String orderPrice;
    private String orderStatus;

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getCarWashName() {
        return carWashName;
    }

    public void setCarWashName(String carWashName) {
        this.carWashName = carWashName;
    }

    public String getOrderCarType() {
        return orderCarType;
    }

    public void setOrderCarType(String orderCarType) {
        this.orderCarType = orderCarType;
    }

    public String getOrderServices() {
        return orderServices;
    }

    public void setOrderServices(String orderServices) {
        this.orderServices = orderServices;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
