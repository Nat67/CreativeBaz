package com.example.creativebaz.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (

    val id: String ="",
    val name : String = "",
    val email : String = "",
    val image : String = "",
    val mobile : Long = 0,
    val bio: String = "",
    val profession: String="",
    val profileCompleted : Int  = 0

): Parcelable
