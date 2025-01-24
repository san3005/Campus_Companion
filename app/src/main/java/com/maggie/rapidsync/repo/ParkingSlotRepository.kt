package com.maggie.rapidsync.repo

import com.maggie.rapidsync.model.network.ApiService
import com.maggie.rapidsync.model.pojo.ParkingSlot
import retrofit2.Response
import javax.inject.Inject

class ParkingSlotRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getAll(): Response<List<ParkingSlot>> {
        return apiService.getParkingSlots()
    }

    suspend fun updateParkingSlot(id: String, parkingSlot: ParkingSlot): Response<ParkingSlot> {
        return apiService.updateParkingSlot(id, parkingSlot)
    }

}