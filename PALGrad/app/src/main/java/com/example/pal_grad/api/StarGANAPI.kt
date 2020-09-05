package com.example.pal_grad.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


class StarGANAPI{

    companion object {
        // base url dari end point.
        const val BASE_URL = "https://psbgrad.duckdns.org:5000/"
    }
    // ini retrofit
    private fun retrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    }
    // buat sebuah instance untuk call sebuah interface dari retrofit.
    fun instance() : StarGANAPIInterface {
        return retrofit().create(StarGANAPIInterface::class.java)
    }
}

var okHttpClient: OkHttpClient? = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

val gson : Gson = GsonBuilder()
    .setLenient()
    .create()

interface StarGANAPIInterface {
    @GET("result")
    fun getResult() : Call<StarGANResult>

    @Multipart
    @POST("upload")
    fun upload(
        @Part("style") style: String,
        @Part file:MultipartBody.Part):Call<StarGANPost>
}