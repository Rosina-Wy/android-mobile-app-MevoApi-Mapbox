package com.example.mapexercise.networking

//allows to check formation of current network
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkChecker(private val connectivityManager: ConnectivityManager){

    fun performAction(action : () ->Unit){
        if(hasValidInternetConnection()){
            action()
        }
    }

    private fun hasValidInternetConnection(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)



        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true



    }
}