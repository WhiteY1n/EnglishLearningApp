package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.core.network.BackendActionResult
import com.vu.englishlearningapp.core.network.BackendResult
import com.vu.englishlearningapp.core.network.requireBackendData
import com.vu.englishlearningapp.core.network.requireBackendSuccess
import com.vu.englishlearningapp.data.remote.api.RoleApi
import com.vu.englishlearningapp.data.remote.dto.role.RoleDto
import com.vu.englishlearningapp.data.remote.dto.role.RoleRequestDto

class RoleRepository(private val roleApi: RoleApi) {
    suspend fun getRoles(): List<RoleDto> {
        val response = roleApi.getRoles()
        if (response.statusCode !in 200..299 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun createRole(request: RoleRequestDto): BackendResult<RoleDto> =
        roleApi.createRole(request).requireBackendData()

    suspend fun updateRole(id: Int, request: RoleRequestDto): BackendResult<RoleDto> =
        roleApi.updateRole(id, request).requireBackendData()

    suspend fun deleteRole(id: Int): BackendActionResult =
        roleApi.deleteRole(id).requireBackendSuccess()
}
