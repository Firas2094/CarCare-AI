package com.firas.carcareai.utils

/**
 * Formate une échéance kilométrique.
 */
fun formatRemainingKm(remainingKm: Int): String {
    return when {
        remainingKm < 0 ->
            "${-remainingKm.toLong()} km de retard"

        remainingKm == 0 ->
            "Échéance kilométrique atteinte"

        else ->
            "$remainingKm km restants"
    }
}

/**
 * Formate une échéance temporelle.
 */
fun formatRemainingDays(remainingDays: Long): String {
    return when {
        remainingDays < 0 ->
            "${-remainingDays} jours de retard"

        remainingDays == 0L ->
            "Échéance aujourd'hui"

        remainingDays == 1L ->
            "1 jour restant"

        else ->
            "$remainingDays jours restants"
    }
}