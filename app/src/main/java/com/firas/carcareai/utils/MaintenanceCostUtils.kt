package com.firas.carcareai.utils

import java.math.BigDecimal

/**
 * Convertit un montant en euros saisi par l'utilisateur
 * vers un montant en centimes.
 *
 * Exemples :
 * "125,50" -> 12550L
 * "25.99"  -> 2599L
 * ""       -> null
 * "-10"    -> null
 * "12,345" -> null
 */
fun parseCostToCents(input: String): Long? {
    val normalized = input.trim().replace(",", ".")

    if (normalized.isEmpty()) return null

    val amount = normalized.toBigDecimalOrNull()
        ?.takeIf { it.signum() >= 0 }
        ?: return null

    return runCatching {
        amount.movePointRight(2).longValueExact()
    }.getOrNull()
}

/**
 * Convertit les centimes en une chaîne en euros.
 *
 * Exemple : 12550L -> "125,50 €"
 */
fun formatCostFromCents(cents: Long?): String {
    if (cents == null) return "Non renseigné"

    val euros = BigDecimal.valueOf(cents, 2)

    return "${euros.toPlainString().replace('.', ',')} €"
}
