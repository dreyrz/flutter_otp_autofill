package com.example.otp_autofill


import android.content.ContentValues.TAG
import android.util.Log

class OtpRetriever {
    fun retrieveOtpFromMessage(message: String?): String? {
        if(message==null){
            return null
        }
        val regex = Regex("(\\d{6})")
        val match = regex.find(message)
        Log.w(TAG, match?.value?:"no match")
        return match?.value
    }
}