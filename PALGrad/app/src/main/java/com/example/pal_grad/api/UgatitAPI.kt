package com.example.pal_grad.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

class UgatitAPI{
    companion object {
        const val BASE_URL = "https://psbgrad.duckdns.org:5000/"
    }
    private fun retrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    }
    fun instance() : UgatitAPIInterface {
        return retrofit().create(UgatitAPIInterface::class.java)
    }
}

interface UgatitAPIInterface {
    @GET("animeResult")
    fun getResult() : Call<UgatitResult>

    @Multipart
    @POST("uploadAnime")
    fun upload(
        @Part file: MultipartBody.Part): Call<UgatitPost>
}