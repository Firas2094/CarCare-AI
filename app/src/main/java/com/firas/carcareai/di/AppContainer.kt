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

class AppContainer(context: Context) {

    private val database: CarCareDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            CarCareDatabase::class.java,
            "carcare_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    /**
     *  DAOs
     *
     */

    private val vehicleDao = database.vehicleDao()
    private val maintenanceDao = database.maintenanceDao()

    /**
     *  Repositories
     *
     */

    val vehicleRepository: VehicleRepository =
        VehicleRepositoryImpl(vehicleDao)

    val maintenanceRepository: MaintenanceRepository =
        MaintenanceRepositoryImpl(maintenanceDao)

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
}

