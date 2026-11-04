package com.firas.data.mapper

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.data.local.entity.MaintenanceEntity
import com.firas.carcareai.data.mapper.toDomain
import com.firas.carcareai.data.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests unitaires des conversions entre Maintenance
 * et MaintenanceEntity.
 */
class MaintenanceMapperTest {

    private val maintenanceType = MaintenanceType.entries.first()

    @Test
    fun `toEntity conserve les informations de depense`() {
        // GIVEN : un entretien avec coût, garage et notes.
        val maintenance = Maintenance(
            id = 1L,
            vehicleId = 10L,
            type = maintenanceType,
            date = 1730000000000L,
            mileage = 125000,
            costCents = 12550L,
            garageName = "Norauto",
            notes = "Vidange et filtre à huile"
        )

        // WHEN : conversion vers l'entité Room.
        val entity = maintenance.toEntity()

        // THEN : toutes les propriétés sont conservées.
        assertEquals(maintenance.id, entity.id)
        assertEquals(maintenance.vehicleId, entity.vehicleId)
        assertEquals(maintenance.type.name, entity.type)
        assertEquals(maintenance.date, entity.date)
        assertEquals(maintenance.mileage, entity.mileage)
        assertEquals(12550L, entity.costCents)
        assertEquals("Norauto", entity.garageName)
        assertEquals("Vidange et filtre à huile", entity.notes)
    }

    @Test
    fun `toDomain conserve les informations de depense`() {
        // GIVEN : une entité Room contenant des dépenses.
        val entity = MaintenanceEntity(
            id = 2L,
            vehicleId = 10L,
            type = maintenanceType.name,
            date = 1730000000000L,
            mileage = 130000,
            costCents = 8999L,
            garageName = "Feu Vert",
            notes = "Remplacement des filtres"
        )

        // WHEN : conversion vers le modèle métier.
        val maintenance = entity.toDomain()

        // THEN : les données sont correctement restaurées.
        assertEquals(entity.id, maintenance.id)
        assertEquals(entity.vehicleId, maintenance.vehicleId)
        assertEquals(maintenanceType, maintenance.type)
        assertEquals(entity.date, maintenance.date)
        assertEquals(entity.mileage, maintenance.mileage)
        assertEquals(8999L, maintenance.costCents)
        assertEquals("Feu Vert", maintenance.garageName)
        assertEquals("Remplacement des filtres", maintenance.notes)
    }

    @Test
    fun `toEntity conserve les champs optionnels absents`() {
        // GIVEN : un entretien sans coût ni garage.
        val maintenance = Maintenance(
            vehicleId = 10L,
            type = maintenanceType,
            date = 1730000000000L,
            mileage = 125000
        )

        // WHEN : conversion vers Room.
        val entity = maintenance.toEntity()

        // THEN : les champs optionnels restent null.
        assertNull(entity.costCents)
        assertNull(entity.garageName)
        assertNull(entity.notes)
    }

    @Test
    fun `toDomain conserve les champs optionnels absents`() {
        // GIVEN : une entité sans informations financières.
        val entity = MaintenanceEntity(
            vehicleId = 10L,
            type = maintenanceType.name,
            date = 1730000000000L,
            mileage = 125000
        )

        // WHEN : conversion vers le modèle métier.
        val maintenance = entity.toDomain()

        // THEN : les valeurs null sont conservées.
        assertNull(maintenance.costCents)
        assertNull(maintenance.garageName)
        assertNull(maintenance.notes)
    }

    @Test
    fun `conversion aller retour conserve toutes les donnees`() {
        // GIVEN : un entretien complet.
        val original = Maintenance(
            id = 3L,
            vehicleId = 20L,
            type = maintenanceType,
            date = 1730000000000L,
            mileage = 145000,
            costCents = 25075L,
            garageName = "Garage Central",
            notes = "Révision complète"
        )

        // WHEN : conversion Domain -> Entity -> Domain.
        val restored = original.toEntity().toDomain()

        // THEN : le modèle reste identique.
        assertEquals(original, restored)
    }

    @Test
    fun `conversion aller retour conserve un cout de zero`() {
        // GIVEN : un entretien gratuit.
        val original = Maintenance(
            vehicleId = 20L,
            type = maintenanceType,
            date = 1730000000000L,
            mileage = 145000,
            costCents = 0L
        )

        // WHEN : conversion aller-retour.
        val restored = original.toEntity().toDomain()

        // THEN : zéro reste distinct de null.
        assertEquals(0L, restored.costCents)
    }
}


