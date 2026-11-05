package com.firas.carcareai.utils

import java.util.Locale

/**
 * Convertit un type d'entretien technique
 * en un libellé lisible en français.
 *
 * TODO : remplacer les traductions en dur par
 * des ressources strings.xml lors de la refonte multilingue.
 */
fun maintenanceTypeLabel(type: String): String {
    return when (type) {
        "BRAKES" -> "Freinage"
        "OIL_CHANGE" -> "Vidange"
        "TIRES" -> "Pneus"
        "BATTERY" -> "Batterie"
        "TECHNICAL_INSPECTION" -> "Contrôle technique"
        "AIR_FILTER" -> "Filtre à air"
        else -> type.replace("_", " ")
            .lowercase(Locale.ROOT)
            .replaceFirstChar { it.titlecase(Locale.FRENCH) }
    }
}