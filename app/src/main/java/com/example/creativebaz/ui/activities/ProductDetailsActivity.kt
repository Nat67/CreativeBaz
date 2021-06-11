package com.example.creativebaz.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.models.Product
import com.example.creativebaz.utils.Constants
import com.example.creativebaz.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_settings.*

class ProductDetailsActivity : BaseActivity() {

    private  var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product id", mProductId)
        }

        getProductDetais()
    }

    private fun getProductDetais(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productDetailsSuccess(product: Product){
        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(product.image, iv_product_detail_image)

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$ ${product.price}"
        tv_product_details_category.text = product.category
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)

        }

        toolbar_product_details_activity.setNavigationOnClickListener{ onBackPressed()}
    }
}