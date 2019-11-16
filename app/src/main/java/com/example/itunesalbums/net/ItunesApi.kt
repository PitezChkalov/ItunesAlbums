package com.example.itunesalbums.net

import android.text.SpannableString
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.models.TrackModel
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?")
    fun getData(
        @Query("term") term: String,
        @Query("entity") entity: String,
        @Query("limit") limit: String,
        @Query("media") media: String,
        @Query("attribute") attribute: String
    ): Single<AlbumModel.Response>

    @GET("/search?")
    fun getTracks(
        @Query(value = "media") media: String,
        @Query("term") term: String,
        @Query("entity") entity: String,
        @Query("attribute") attribute: String
    ): Single<TrackModel.Response>
}