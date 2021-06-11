package com.example.creativebaz.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import kotlinx.android.synthetic.main.dialog_progress.*


open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume(){
        super.onResume()
        getProductListFromFirestore()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialog(text:String){
        mProgressDialog  = Dialog(requireActivity())
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
    }

    private fun getProductListFromFirestore(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

}