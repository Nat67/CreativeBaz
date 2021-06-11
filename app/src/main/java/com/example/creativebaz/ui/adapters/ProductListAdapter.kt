package com.example.creativebaz.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.creativebaz.R
import com.example.creativebaz.models.Product
import com.example.creativebaz.ui.activities.ProductDetailsActivity
import com.example.creativebaz.ui.fragments.ProductsFragment
import com.example.creativebaz.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

open class ProductListAdapter (
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment
    ):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.iv_item_image)
            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$ ${model.price}"

            holder.itemView.ib_delete_product.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}