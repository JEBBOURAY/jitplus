package com.jitplus.merchant.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jitplus.merchant.R
import com.jitplus.merchant.data.model.TopCustomer

class TopCustomerAdapter : RecyclerView.Adapter<TopCustomerAdapter.ViewHolder>() {

    private var customers: List<TopCustomer> = emptyList()

    fun submitList(list: List<TopCustomer>) {
        customers = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_customer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customers[position], position + 1)
    }

    override fun getItemCount(): Int = customers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRank: TextView = itemView.findViewById(R.id.tv_rank)
        private val tvName: TextView = itemView.findViewById(R.id.tv_customer_name)
        private val tvPhone: TextView = itemView.findViewById(R.id.tv_customer_phone)
        private val tvVisits: TextView = itemView.findViewById(R.id.tv_visits_count)
        private val tvRedemptions: TextView = itemView.findViewById(R.id.tv_redemptions_count)

        fun bind(customer: TopCustomer, rank: Int) {
            tvRank.text = rank.toString()
            tvName.text = customer.name
            tvPhone.text = customer.phone
            tvVisits.text = itemView.context.getString(R.string.visits_count, customer.visits)
            tvRedemptions.text = itemView.context.getString(R.string.redemptions_count, customer.redemptions)
        }
    }
}
