package com.example.itunesalbums.models

data class TrackModel(val artworkUrl100: String,
                      val trackPrice: String,
                      val releaseDate: String,
                      val currency:String,
                      val trackName: String,
                      val previewUrl: String,
                      val trackNumber: Int,
                      val collectionId: String) {

    data class Response(val resultCount:Int, val results: ArrayList<TrackModel>)
}