package com.aulia.idn.ojolfirebase.network

import com.aulia.idn.ojolfirebase.model.Booking
import com.google.gson.annotations.SerializedName

class RequestNotification {

    @SerializedName("to")
    var token: String? = null

    @SerializedName("data")
    var sendNotificationModel: Booking? = null
}