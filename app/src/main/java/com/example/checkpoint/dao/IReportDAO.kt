package com.example.checkpoint.dao

import com.example.checkpoint.dto.Report
import retrofit2.Call
import retrofit2.http.GET

interface IReportDAO {
    @GET("/Newbtree45/data/main/report.md")
    fun getAllReports() : Call<List<Report>>
}