package com.firas.carcareai.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.firas.carcareai.data.local.CarCareDatabase
import com.firas.carcareai.data.repository.MaintenanceRepositoryImpl
import com.firas.carcareai.data.repository.VehicleRepositoryImpl
import com.firas.carcareai.domain.repository.MaintenanceRepository
import com.firas.carcareai.domain.repository.VehicleRepository
import com.firas.carcareai.domain.usecase.maintenance.AddMaintenanceUseCase
import com.firas.carcareai.domain.usecase.maintenance.CalculateMaintenanceScheduleUseCase
import com.firas.carcareai.domain.usecase.maintenance.DeleteMaintenanceUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceByIdUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenancesByVehicleUseCase
import com.firas.carcareai.domain.usecase.maintenance.UpdateMaintenanceUseCase
import com.firas.carcareai.domain.usecase.vehicle.AddVehicleUseCase
import com.firas.carcareai.domain.usecase.vehicle.DeleteVehicleUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehicleByIdUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehiclesUseCase
import com.firas.carcareai.domain.usecase.vehicle.UpdateVehicleUseCase
import com.firas.carcareai.data.repository.MaintenanceIntervalRepositoryImpl
import com.firas.carcareai.domain.repository.MaintenanceIntervalRepository
import com.firas.carcareai.domain.usecase.maintenance.DeleteMaintenanceIntervalUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceIntervalsUseCase
import com.firas.carcareai.domain.usecase.maintenance.SaveMaintenanceIntervalUseCase

private val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS maintenances (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                vehicleId INTEGER NOT NULL,
                type TEXT NOT NULL,
                date INTEGER NOT NULL,
                mileage INTEGER NOT NULL,
                cost REAL,
                notes TEXT,
                FOREIGN KEY(vehicleId)
                    REFERENCES vehicles(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_maintenances_vehicleId
            ON maintenances(vehicleId)
            """.trimIndent()
        )
    }
}

/**
 * Migration de la version 2 vers la version 3.
 *
 * Ajoute la table des intervalles personnalisés.
 *
 * Chaque véhicule peut définir ses propres
 * intervalles d'entretien :
 * - En kilomètres
 * - En mois
 * - Ou les deux
 *
 * Les véhicules et entretiens existants sont conservés.
 */
private val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS maintenance_intervals (
                vehicleId INTEGER NOT NULL,
                type TEXT NOT NULL,
                intervalKm INTEGER,
                intervalMonths INTEGER,
                PRIMARY KEY(vehicleId, type),
                FOREIGN KEY(vehicleId)
                    REFERENCES vehicles(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_maintenance_intervals_vehicleId
            ON maintenance_intervals(vehicleId)
            """.trimIndent()
        )
    }
}

/**
 * Conteneur principal des dépendances de CarCare AI.
 *
 * Responsabilités :
 * - Initialiser la base Room
 * - Enregistrer les migrations
 * - Fournir les DAOs
 * - Construire les repositories
 * - Fournir les UseCases métier
 */

class AppContainer(context: Context) {

    /**
     * Base de données Room.
     *
     * Les migrations permettent de mettre à jour
     * le schéma sans supprimer les données existantes.
     */

    private val database: CarCareDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CarCareDatabase::class.java,
            "carcare_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            )
            .build()

    /**
     *  DAOs
     *
     */

    private val vehicleDao = database.vehicleDao()
    private val maintenanceDao = database.maintenanceDao()
    private val maintenanceIntervalDao = database.maintenanceIntervalDao()

    /**
     *  Repositories
     *
     */

    val vehicleRepository: VehicleRepository =
        VehicleRepositoryImpl(vehicleDao)

    val maintenanceRepository: MaintenanceRepository =
        MaintenanceRepositoryImpl(maintenanceDao)

    val maintenanceIntervalRepository: MaintenanceIntervalRepository =
        MaintenanceIntervalRepositoryImpl(maintenanceIntervalDao)

    /**
     *  Vehicles UseCase
     *
     */

    val getVehiclesUseCase =
        GetVehiclesUseCase(vehicleRepository)

    val addVehicleUseCase =
        AddVehicleUseCase(vehicleRepository)

    val getVehicleByIdUseCase =
        GetVehicleByIdUseCase(vehicleRepository)

    val deleteVehicleUseCase =
        DeleteVehicleUseCase(vehicleRepository)

    val updateVehicleUseCase =
        UpdateVehicleUseCase(vehicleRepository)


    /**
     *  Maintenances UseCase
     *
     */

    val getMaintenancesByVehicleUseCase =
        GetMaintenancesByVehicleUseCase(maintenanceRepository)

    val getMaintenanceByIdUseCase =
        GetMaintenanceByIdUseCase(maintenanceRepository)

    val addMaintenanceUseCase =
        AddMaintenanceUseCase(maintenanceRepository)

    val updateMaintenanceUseCase =
        UpdateMaintenanceUseCase(maintenanceRepository)

    val deleteMaintenanceUseCase =
        DeleteMaintenanceUseCase(maintenanceRepository)


    /**
     *  Maintenance Schedule UseCase
     *
     */

    val calculateMaintenanceScheduleUseCase =
        CalculateMaintenanceScheduleUseCase()


    /**
     * Maintenance Intervals UseCases
     *
     * Gestion des configurations personnalisées
     * des intervalles d'entretien.
     */
    val getMaintenanceIntervalsUseCase =
        GetMaintenanceIntervalsUseCase(maintenanceIntervalRepository)

    val saveMaintenanceIntervalUseCase =
        SaveMaintenanceIntervalUseCase(maintenanceIntervalRepository)

    val deleteMaintenanceIntervalUseCase =
        DeleteMaintenanceIntervalUseCase(maintenanceIntervalRepository)
}

