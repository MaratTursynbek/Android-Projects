package com.marat.apps.android.pro3.Models;

import java.util.ArrayList;

public class CarWashServices {

    private int serviceId;
    private String serviceName;
    private ArrayList<Service> services;

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }
}
