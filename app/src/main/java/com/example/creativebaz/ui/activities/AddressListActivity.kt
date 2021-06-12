package com.example.creativebaz.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.models.Address
import com.example.creativebaz.ui.adapters.AddressListAdapter
import com.example.creativebaz.utils.Constants
import com.example.creativebaz.utils.SwipeToDeleteCallback
import com.example.creativebaz.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : BaseActivity() {

    private var mSelectedAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()

        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        getAddressList()

        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectedAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if(mSelectedAddress){
            tv_title_address.text = resources.getString(R.string.title_select_address)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }
        toolbar_address_list_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>){
        hideProgressDialog()
        if(addressList.size > 0 ) {
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)

            val addresAdapter = AddressListAdapter(this, addressList, mSelectedAddress)
            rv_address_list.adapter = addresAdapter

            if(!mSelectedAddress){
                val editSwipeHandler = object : SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().deleteAddress(this@AddressListActivity, addressList[viewHolder.adapterPosition].id)
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }

        }
        else{
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess(){
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.err_your_address_deleted_successfully), Toast.LENGTH_SHORT).show()
        getAddressList()
    }
}