package com.example.creativebaz.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.models.Cart
import com.example.creativebaz.models.CartItem
import com.example.creativebaz.models.Product
import com.example.creativebaz.ui.adapters.CartItemsListAdapter
import com.example.creativebaz.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getProductsList()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar= supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductsList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun getSuccessCartItemsList(cartList:ArrayList<Cart>){
        hideProgressDialog()

        for (product in mProductList){
            for (cartItem in cartList){
                if(product.product_id == cartItem.product_id){

                    cartItem.stock_quantity = product.stock_quantity
                    Log.d("Stock_quantity", cartItem.stock_quantity)

                    if(product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if(mCartListItems.size > 0){

            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter

            var subtotal: Double = 0.0

            for(item in mCartListItems){

                val availableQuantity = item.stock_quantity

                if(availableQuantity != "0"){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subtotal += (price*quantity)
                }
            }
            tv_sub_total.text = "$ ${subtotal}"
            tv_shipping_charge.text = "$10.0"

            if(subtotal > 0){
                ll_checkout.visibility = View.VISIBLE

                val total = subtotal +10
                tv_total_amount.text = "$ $total"
            }else{
                ll_checkout.visibility = View.GONE
            }
        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>){
        hideProgressDialog()
        mProductList= productsList
        getCartItemsList()

    }

    private fun getCartItemsList(){
        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }


    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }
}