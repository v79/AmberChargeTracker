package org.liamjd.amber.db.entities

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Vehicle(val manufacturer: String, val model: String, val odometerReading: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface VehicleDao {

    // which should be suspend and which should not???
    @Insert
    fun insert(vehicle: Vehicle): Long

    @Delete
    fun delete(vehicle: Vehicle)

    @Update
    fun update(vehicle: Vehicle)

    @Query("SELECT * FROM Vehicle where id = :id LIMIT 1")
    fun getVehicle(id: Long): LiveData<Vehicle?>

    @Query("SELECT COUNT(*) FROM Vehicle")
    fun getVehicleCount(): LiveData<Int>

    @Query("SELECT MAX(id) FROM Vehicle")
    suspend fun getMostRecentVehicleId(): Long?

    @Query("SELECT id FROM Vehicle WHERE rowId = :rowId")
    fun getVehiclePkWithRowId(rowId: Long): Long
}

data class OdometerReading(@PrimaryKey(autoGenerate = true) val id: Int, val reading: Int)