package org.liamjd.amber.db.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.entities.VehicleDao
import java.time.LocalDateTime

interface VehicleRepositoryIF // TODO

class VehicleRepository(private val vehicleDao: VehicleDao) {

    fun getVehicleCount(): LiveData<Int> = vehicleDao.getVehicleCount()

    suspend fun getVehicleById(id: Long): Vehicle = vehicleDao.getVehicle(id)

    fun insert(vehicle: Vehicle) = vehicleDao.insert(vehicle)

    fun getVehicleIdFromRowId(rowId: Long): Long = vehicleDao.getVehiclePkWithRowId(rowId)

    suspend fun getAllVehicles(): Flow<List<Vehicle>> = vehicleDao.getAll()

    suspend fun getMostRecentVehicleId() = vehicleDao.getMostRecentVehicleId()

    suspend fun getCurrentOdometer(vehicleId: Long): Int {
        val value = vehicleDao.getCurrentOdometer(vehicleId)
        Log.i("VehicleRepository", "DAO has returned odometer $value for id $vehicleId")
        return value
    }

    suspend fun updateOdometer(vehicleId: Long, odometer: Int) {
        vehicleDao.updateOdometer(vehicleId, odometer, LocalDateTime.now())
    }

    suspend fun updatePhotoPath(vehicleId: Long, photoPath: String?) {
        vehicleDao.updatePhotoPath(vehicleId, photoPath, LocalDateTime.now())
    }

    fun updateVehicle(vehicle: Vehicle) {
        Log.i("VehicleRepository","DAO is updating vehicle $vehicle (${vehicle.id})")
        vehicleDao.update(vehicle)
    }
}