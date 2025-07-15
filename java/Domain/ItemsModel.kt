package com.uilover.project2252.Domain

import java.io.Serializable

data class ItemsModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = ""
) : Serializable {

    // Constructor without 'id'
    constructor(
        title: String,
        description: String,
        picUrl: ArrayList<String>,
        price: Double,
        rating: Double,
        numberInCart: Int,
        extra: String
    ) : this(
        id = "",  // Default id is set to an empty string
        title = title,
        description = description,
        picUrl = picUrl,
        price = price,
        rating = rating,
        numberInCart = numberInCart,
        extra = extra
    )
}
