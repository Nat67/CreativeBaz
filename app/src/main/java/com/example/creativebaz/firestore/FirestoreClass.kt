package com.example.creativebaz.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.creativebaz.models.Address
import com.example.creativebaz.models.CartItem
import com.example.creativebaz.models.Product
import com.example.creativebaz.models.User
import com.example.creativebaz.ui.activities.*
import com.example.creativebaz.ui.fragments.DashboardFragment
import com.example.creativebaz.ui.fragments.ProductsFragment
import com.example.creativebaz.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address){
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error al añadir la dirección", e)
            }
    }

    fun getAddressesList(activity: AddressListActivity){
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents){
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error al obtener las direcciones"
                )
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String){
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String){
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the Address.",
                    e
                )
            }
    }

    fun registerUser(activity: RegisterActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.RegistrationSucceeded()
            }
            .addOnFailureListener{e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error al registrar al usuario"
                )
            }
    }

    fun getCurrentUserId() : String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getCurrentUser(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.CREATIVEBAZ_PREFS,
                        Context.MODE_PRIVATE
                    )

                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.name}"
                )
                editor.apply()

                when(activity){
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity ->{
                        activity.userDetailSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                when (activity){
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity){
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error al cambiar los detalles del usuario",
                    e
                )
            }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType:String){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )
        sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Dowloadable Image URL", uri.toString())
                    when(activity){
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{ exception ->
                when(activity){
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun registerProduct(activity: AddProductActivity, productInfo: Product){
        mFireStore.collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.productUploadSuccess()
                }
                .addOnFailureListener{e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error al registrar el producto"
                    )
                }
    }

    fun removeItemFromCart(context: Context, cart_id: String){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun getAllProductsList(activity: CartListActivity){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()
                for ( i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }
                activity.successProductsListFromFireStore(productList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Get product list", "Error al obtener lso productos")
            }
    }

    fun getProductsList(fragment: Fragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products list:", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }

                when(fragment){
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }
    }

    fun getDasbhoardItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products list:", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener {
                e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error al obtener los ítems", e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val product = document.toObject(Product::class.java)

                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error al obtener los detalles de este producto", e)
            }

    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem){
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }.addOnFailureListener{
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error al crear el documento para agregar al carrito", e)
            }
    }

    fun getCartList(activity: Activity){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val list:ArrayList<CartItem> = ArrayList()
                for(i in document.documents){
                    var cartItem = i.toObject(CartItem:: class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }
                when(activity){
                    is CartListActivity -> {
                        activity.getSuccessCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener{
                e ->
                when(activity){
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error al obtener los productos del carrito", e)
            }
    }

    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                if(document.documents.size > 0){
                    activity.productExistsInCart()
                }else{
                    activity.hideProgressDialog()
                }
            }.addOnFailureListener{
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error al revisar carrito", e)
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

}