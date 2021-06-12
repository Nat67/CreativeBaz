package com.example.creativebaz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.models.Order

class OrdersFragment : BaseFragment() {

    //private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }

    fun populateOrdersListInUI(orderList: ArrayList<Order>){
        hideProgressDialog()


    }

    private fun getMyOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }
}