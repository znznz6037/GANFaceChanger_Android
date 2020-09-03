package com.example.pal_grad.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StarGANAPI {
    @GET("/result")
    fun getResult() : Call<StarGANResult>

    @Multipart
    @POST("/upload")
    fun uploadImage(
        @Part("style") style: String,
        @Part file:MultipartBody.Part):Call<StarGANPost>
}