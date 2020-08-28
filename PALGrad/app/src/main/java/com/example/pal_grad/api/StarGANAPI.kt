package com.example.pal_grad.api

import retrofit2.Call
import retrofit2.http.GET

interface StarGANAPI {
    @GET("/result")
    fun getResult() : Call<StarGANResult>
}