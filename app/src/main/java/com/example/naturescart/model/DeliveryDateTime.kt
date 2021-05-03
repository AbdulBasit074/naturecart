package com.example.naturescart.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class DeliveryDateTime(
    @SerializedName("dates") @Expose var dates: ArrayList<Date> = ArrayList(),
    @SerializedName("time_slots") @Expose var timeSlots: ArrayList<TimeSlot> = ArrayList()
) {
    class Date(
        @SerializedName("date_key") @Expose var dateKey: String? = "",
        @SerializedName("date") @Expose var date: String? = ""
    ) {


    }

    class TimeSlot(
        @SerializedName("time_key") @Expose var timeKey: String? = "",
        @SerializedName("time") @Expose var time: String? = ""
    ) {
    }


}