package com.jitplus.merchant.data.model

data class DashboardStats(
    val totalCustomers: Long,
    val visitsThisWeek: Long,
    val totalRewardsDistributed: Long,
    val topCustomers: List<TopCustomer> = emptyList()
)
