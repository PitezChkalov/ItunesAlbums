package com.example.itunesalbums.models

import android.os.Parcel
import android.os.Parcelable
import com.example.itunesalbums.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



data class AlbumModel(
    val artistName: String,
    val collectionName: String,
    val artworkUrl100: String,
    val collectionPrice: String,
    val currency: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val trackCount: String,
    val copyright:String,
    val country:String,
    val collectionViewUrl:String,
    val collectionId: String
    ) : Parcelable {

    fun getReleaseDateFormat():String{
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(releaseDate)
        return SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    fun formatDate():String{
        var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date = dateFormat.parse(releaseDate)
        dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(date).toString()
    }

    fun getContent(): ArrayList<Content> {
        val content = ArrayList<Content>()
        content.add(Content(Titles.PRICE.title, "$collectionPrice $currency"))
        content.add(Content(Titles.DATE.title, formatDate()))
        content.add(Content(Titles.GENRE.title, primaryGenreName))
        content.add(Content(Titles.TRACKCOUNT.title, trackCount))
        content.add(Content(Titles.COUNTRY.title, country))
        content.add(Content(Titles.COPYRIGHT.title, copyright))
        return content
    }

    enum class Titles(val title: Int) {
        PRICE(R.string.price), GENRE(R.string.genre),
        DATE(R.string.date), TRACKCOUNT(R.string.trackCount),
        COPYRIGHT(R.string.copyright), COUNTRY(R.string.country)
    }
    class Content(val title:Int, val value:String)

    data class Response(val resultCount:Int, val results: ArrayList<AlbumModel>)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(artistName)
        parcel.writeString(collectionName)
        parcel.writeString(artworkUrl100)
        parcel.writeString(collectionPrice)
        parcel.writeString(currency)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(trackCount)
        parcel.writeString(copyright)
        parcel.writeString(country)
        parcel.writeString(collectionViewUrl)
        parcel.writeString(collectionId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumModel> {
        override fun createFromParcel(parcel: Parcel): AlbumModel {
            return AlbumModel(parcel)
        }

        override fun newArray(size: Int): Array<AlbumModel?> {
            return arrayOfNulls(size)
        }
    }
}