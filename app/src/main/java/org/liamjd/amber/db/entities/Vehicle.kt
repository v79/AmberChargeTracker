package org.liamjd.amber.db.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Entity
data class Vehicle(
    val manufacturer: String,
    val model: String,
    val odometerReading: Int,
    val registration: String,
    val lastUpdated: LocalDateTime,
    var photoPath: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Dao
interface VehicleDao {

    // which should be suspend and which should not???
    @Insert
    fun insert(vehicle: Vehicle): Long

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Update
    fun update(vehicle: Vehicle)

    @Query("SELECT * FROM Vehicle where id = :id LIMIT 1")
    suspend fun getVehicle(id: Long): Vehicle

    @Query("SELECT COUNT(*) FROM Vehicle")
     fun getVehicleCount(): LiveData<Int>

    @Query("SELECT MAX(id) FROM Vehicle")
    suspend fun getMostRecentVehicleId(): Long?

    @Query("SELECT id FROM Vehicle WHERE rowId = :rowId")
    fun getVehiclePkWithRowId(rowId: Long): Long

    @Query("SELECT odometerReading FROM Vehicle WHERE id = :id")
    suspend fun getCurrentOdometer(id: Long): Int

    @Query("UPDATE Vehicle SET odometerReading = :odometer, lastUpdated = :now WHERE id = :vehicleId")
    suspend fun updateOdometer(
        vehicleId: Long, odometer: Int, now: LocalDateTime
    )

    @Query("UPDATE Vehicle SET photoPath = :photoPath, lastUpdated = :now WHERE id = :vehicleId")
    suspend fun updatePhotoPath(vehicleId: Long, photoPath: String?, now: LocalDateTime)

    @Query("SELECT * FROM Vehicle ORDER BY Manufacturer")
    fun getAll(): Flow<List<Vehicle>>

}

data class OdometerReading(@PrimaryKey(autoGenerate = true) val id: Int, val reading: Int)