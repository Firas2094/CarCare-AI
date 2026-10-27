package com.firas.carcareai.core.model

data class Vehicle(
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val fuelType: FuelType,
    val licensePlate: String? = null
)

enum class FuelType {
    GASOLINE,
    DIESEL,
    HYBRID,
    ELECTRIC,
    LPG,
    OTHER
    }
