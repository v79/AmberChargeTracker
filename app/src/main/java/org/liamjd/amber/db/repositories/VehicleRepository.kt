package org.liamjd.amber.db.repositories

import androidx.lifecycle.LiveData
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.entities.VehicleDao

interface VehicleRepositoryIF // TODO

class VehicleRepository(private val vehicleDao: VehicleDao) {

    fun getVehicleCount(): LiveData<Int> = vehicleDao.getVehicleCount()

    fun getVehicleById(id: Long): LiveData<Vehicle?> = vehicleDao.getVehicle(id)

    fun insert(vehicle: Vehicle) = vehicleDao.insert(vehicle)

    fun getVehicleIdFromRowId(rowId: Long): Long = vehicleDao.getVehiclePkWithRowId(rowId)

    suspend fun getMostRecentVehicleId() = vehicleDao.getMostRecentVehicleId()

}