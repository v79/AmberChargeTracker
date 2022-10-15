package org.liamjd.amber.db.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class Vehicle(val manufacturer: String, val model: String) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

@Dao
interface VehicleDao {

    @Insert
    suspend fun insert(vehicle: Vehicle)

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Update
    suspend fun update(vehicle: Vehicle)

    @Query("SELECT * FROM Vehicle where id = :id LIMIT 1")
    suspend fun getVehicle(id: Int): Vehicle

    @Query("SELECT COUNT(*) FROM Vehicle")
    suspend fun getVehicleCount(): Int
}

data class OdometerReading(@PrimaryKey(autoGenerate = true) val id: Int, val reading: Int)