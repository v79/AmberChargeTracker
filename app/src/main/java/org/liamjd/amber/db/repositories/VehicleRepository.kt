package org.liamjd.amber.db.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.entities.VehicleDao
import java.time.LocalDateTime

class VehicleRepository(private val dao: VehicleDao) {

    fun getVehicleCount(): LiveData<Int> = dao.getVehicleCount()

    suspend fun getVehicleById(id: Long): Vehicle = dao.getVehicle(id)

    fun insert(vehicle: Vehicle) = dao.insert(vehicle)

    fun getVehicleIdFromRowId(rowId: Long): Long = dao.getVehiclePkWithRowId(rowId)

    suspend fun getAllVehicles(): Flow<List<Vehicle>> = dao.getAll()

    suspend fun getMostRecentVehicleId() = dao.getMostRecentVehicleId()

    suspend fun getCurrentOdometer(vehicleId: Long): Int {
        val value = dao.getCurrentOdometer(vehicleId)
        Log.i("VehicleRepository", "DAO has returned odometer $value for id $vehicleId")
        return value
    }

    suspend fun updateOdometer(vehicleId: Long, odometer: Int) {
        dao.updateOdometer(vehicleId, odometer, LocalDateTime.now())
    }

    suspend fun updatePhotoPath(vehicleId: Long, photoPath: String?) {
        dao.updatePhotoPath(vehicleId, photoPath, LocalDateTime.now())
    }

    fun updateVehicle(vehicle: Vehicle) {
        Log.i("VehicleRepository","DAO is updating vehicle $vehicle (${vehicle.id})")
        dao.update(vehicle)
    }

    suspend fun deleteVehicle(vehicle: Vehicle) {
        Log.i("VehicleRepository","DAO is deleting vehicle $vehicle (${vehicle.id})")
        dao.delete(vehicle)
    }
}