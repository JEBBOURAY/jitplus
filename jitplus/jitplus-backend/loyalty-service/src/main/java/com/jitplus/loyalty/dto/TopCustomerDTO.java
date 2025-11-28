package com.jitplus.loyalty.dto;

public class TopCustomerDTO {
    private String name;
    private String phone;
    private int visits;
    private int redemptions;

    public TopCustomerDTO(String name, String phone, int visits, int redemptions) {
        this.name = name;
        this.phone = phone;
        this.visits = visits;
        this.redemptions = redemptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }
    
    public int getRedemptions() {
        return redemptions;
    }

    public void setRedemptions(int redemptions) {
        this.redemptions = redemptions;
    }
}
