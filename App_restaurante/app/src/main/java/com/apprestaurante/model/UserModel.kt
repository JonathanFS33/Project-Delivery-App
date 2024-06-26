package com.apprestaurante.model

data class UserModel(
    val name: String? = null,
    val nameOfRestaurant: String? = null,
    val email: String? = null,
    val password: String? = null,
    var phone: String? = null,
    var address: String? = null,
)
