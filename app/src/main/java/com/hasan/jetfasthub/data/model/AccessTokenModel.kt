package com.hasan.jetfasthub.data.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName

//class AccessTokenModel private constructor(parcel: Parcel) : Parcelable {
//    private val id: Long
//    private val token: String?
//    private val hashedToken: String?
//    private val accessToken: String?
//    private val tokenType: String?
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeLong(id)
//        dest.writeString(token)
//        dest.writeString(hashedToken)
//        dest.writeString(accessToken)
//        dest.writeString(tokenType)
//    }
//
//    init {
//        id = parcel.readLong()
//        token = parcel.readString()
//        hashedToken = parcel.readString()
//        accessToken = parcel.readString()
//        tokenType = parcel.readString()
//    }
//
//    companion object CREATOR : Creator<AccessTokenModel> {
//        override fun createFromParcel(parcel: Parcel): AccessTokenModel {
//            return AccessTokenModel(parcel)
//        }
//
//        override fun newArray(size: Int): Array<AccessTokenModel?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//}

//data class AccessTokenModel(
//    private val id: Long,
//    private val token: String = "",
//    private val hashedToken: String = "",
//    private val accessToken: String = "",
//    private val tokenType: String = "",
//)

data class AccessTokenModel(
    @SerializedName("access_token")
    val access_token: String ? ,
)
