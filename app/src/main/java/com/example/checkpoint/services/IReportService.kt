package com.example.checkpoint.services

import com.example.checkpoint.RetrofitClientReport
import com.example.checkpoint.dao.IReportDAO
import com.example.checkpoint.dto.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

interface IReportService {
    suspend fun fetchReports(): List<Report>?
}

class ReportService : IReportService {
    override suspend fun fetchReports(): List<Report>? {
        return withContext(Dispatchers.IO) {
            val retrofit = RetrofitClientReport.retrofitInstance?.create(IReportDAO::class.java)
            val reports = async { retrofit?.getAllReports() }
            var result = reports.await()?.awaitResponse()?.body()
            return@withContext result
        }
    }
}