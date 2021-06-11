package com.example.creativebaz.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.creativebaz.R
import com.example.creativebaz.models.Product
import com.example.creativebaz.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*

class DashboardAdapter (
        private val context: Context,
        private var list: ArrayList<Product>
    ):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_dashboard_layout,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val model = list[position]

            if(holder is MyViewHolder){
                GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_dashboard_item_image)
                holder.itemView.tv_dashboard_item_title.text = model.title
                holder.itemView.tv_dashboard_item_price.text = "$ ${model.price}"
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}