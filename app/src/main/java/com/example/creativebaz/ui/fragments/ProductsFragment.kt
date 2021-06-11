package com.example.creativebaz.ui.fragments

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.models.Product
import com.example.creativebaz.ui.activities.AddProductActivity
import com.example.creativebaz.ui.activities.SettingsActivity
import com.example.creativebaz.ui.adapters.ProductListAdapter
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {

    //private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun successProductsListFromFirestore(productsList: ArrayList<Product>){
        hideProgressDialog()

        if(productsList.size > 0 ){
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            val adapterProducts = ProductListAdapter(requireActivity(), productsList, this)
            rv_my_product_items.adapter = adapterProducts
        }
        else{
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_products, container, false)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        getProductListFromFireStore()
    }

    private fun getProductListFromFireStore() {
        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this@ProductsFragment)
    }

    fun deleteProduct(productID: String){
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        getProductListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)

            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}