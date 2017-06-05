package com.marat.apps.android.pro3.Models;

public class Service {

    private String serviceName;
    private int servicePriceId;
    private int servicePrice;
    private String serviceDescription;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServicePriceId() {
        return servicePriceId;
    }

    public void setServicePriceId(int servicePriceId) {
        this.servicePriceId = servicePriceId;
    }

    public int getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(int servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
}
