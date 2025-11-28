package com.jitplus.loyalty.dto;

import java.util.List;

public class DashboardStats {
    
    private long totalCustomers;
    private long visitsThisWeek;
    private long totalRewardsDistributed;
    private List<TopCustomerDTO> topCustomers;
    
    public DashboardStats() {
    }
    
    public DashboardStats(long totalCustomers, long visitsThisWeek, long totalRewardsDistributed, List<TopCustomerDTO> topCustomers) {
        this.totalCustomers = totalCustomers;
        this.visitsThisWeek = visitsThisWeek;
        this.totalRewardsDistributed = totalRewardsDistributed;
        this.topCustomers = topCustomers;
    }
    
    public long getTotalCustomers() {
        return totalCustomers;
    }
    
    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
    
    public long getVisitsThisWeek() {
        return visitsThisWeek;
    }
    
    public void setVisitsThisWeek(long visitsThisWeek) {
        this.visitsThisWeek = visitsThisWeek;
    }
    
    public long getTotalRewardsDistributed() {
        return totalRewardsDistributed;
    }
    
    public void setTotalRewardsDistributed(long totalRewardsDistributed) {
        this.totalRewardsDistributed = totalRewardsDistributed;
    }
    
    public List<TopCustomerDTO> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<TopCustomerDTO> topCustomers) {
        this.topCustomers = topCustomers;
    }
}
