package com.example.demoApp;

public class sendVID {
    private String VehicleID,Name,Address,School,presentStatus,inVehicle,outVehicle;

    public sendVID() {
    }

    public String getVehicleID() {
        return VehicleID;
    }

    public void setVehicleID(String vehicleID) {
        VehicleID = vehicleID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
    }

    public String getPresentStatus() {
        return presentStatus;
    }

    public void setPresentStatus(String presentStatus) {
        this.presentStatus = presentStatus;
    }

    public String getInVehicle() {
        return inVehicle;
    }

    public void setInVehicle(String inVehicle) {
        this.inVehicle = inVehicle;
    }

    public String getOutVehicle() {
        return outVehicle;
    }

    public void setOutVehicle(String outVehicle) {
        this.outVehicle = outVehicle;
    }
}
