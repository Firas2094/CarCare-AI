package com.firas.carcareai.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Créer la nouvelle table avec les nouveaux champs.
        db.execSQL(
            """
            CREATE TABLE maintenances_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                vehicleId INTEGER NOT NULL,
                type TEXT NOT NULL,
                date INTEGER NOT NULL,
                mileage INTEGER NOT NULL,
                costCents INTEGER,
                garageName TEXT,
                notes TEXT,
                FOREIGN KEY(vehicleId)
                    REFERENCES vehicles(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // 2. Copier les données existantes.
        // ROUND convertit les montants en centimes.
        db.execSQL(
            """
            INSERT INTO maintenances_new (
                id,
                vehicleId,
                type,
                date,
                mileage,
                costCents,
                garageName,
                notes
            )
            SELECT
                id,
                vehicleId,
                type,
                date,
                mileage,
                CASE
                    WHEN cost IS NULL THEN NULL
                    ELSE CAST(ROUND(cost * 100) AS INTEGER)
                END,
                NULL,
                notes
            FROM maintenances
            """.trimIndent()
        )

        // 3. Supprimer l'ancienne table.
        db.execSQL("DROP TABLE maintenances")

        // 4. Renommer la nouvelle table.
        db.execSQL(
            "ALTER TABLE maintenances_new RENAME TO maintenances"
        )

        // 5. Recréer l'index utilisé par Room.
        db.execSQL(
            """
            CREATE INDEX index_maintenances_vehicleId
            ON maintenances(vehicleId)
            """.trimIndent()
        )
    }
}
