@file:Suppress("DEPRECATION")

package com.a2zdukhana.store.services

import com.google.firebase.iid.FirebaseInstanceIdService

class GettingToken : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        //Log.e("Device Token: ",FirebaseInstanceId.getInstance().getToken())
    }
}