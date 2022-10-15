package org.liamjd.amber.db.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.entities.VehicleDao

class VehicleRepository(private val vehicleDao: VehicleDao) {

    suspend fun getVehicleById(id: Int): Vehicle = vehicleDao.getVehicle(id)

    suspend fun insert(vehicle: Vehicle) = vehicleDao.insert(vehicle)

    suspend fun getVehicleCount(): Int = vehicleDao.getVehicleCount()
}