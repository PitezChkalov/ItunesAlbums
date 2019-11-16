package com.example.itunesalbums.presenters

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.itunesalbums.R
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.models.TrackModel
import com.example.itunesalbums.providers.DetailsProvider
import com.example.itunesalbums.views.DetailsView
import timber.log.Timber
import java.lang.Exception

@InjectViewState
class DetailsPresenter: MvpPresenter<DetailsView>() {

    val detailsProvider = DetailsProvider(this)
    val mediaPlayer: MediaPlayer = MediaPlayer()

    lateinit var album: AlbumModel
    //load tracks from DetailsProvider
    fun loadTracks(term: String){
        Timber.d("loadTracks for term: $term")
        viewState.showProgress()
        try {
            detailsProvider.getTracks(term)
        }catch (e:Exception){
            Timber.d(e)
            viewState.showToast(R.string.unknownError)
        }
    }

    //save album here for recreated activity
    fun setData(album: AlbumModel){
        Timber.d("setData for album ${album.collectionName}")
        this.album = album
        viewState.setData(this.album)
    }

    //calling when loading in DetailProvider is completed
    fun loadingComplete(tracks: ArrayList<TrackModel>){
        Timber.d("loading complete. track list size = ${tracks.size}")
        viewState.hideProgress()
        if(!tracks.isEmpty())
            viewState.setTracks(tracks)
        else viewState.showMessage(R.string.no_tracks)

    }

    //play new track
    fun playTrack(uri: String){
        Timber.d("playTrack with uri = $uri")
        mediaPlayer.reset()
        mediaPlayer.setDataSource(uri)
        mediaPlayer.setOnPreparedListener {
            viewState.hideProgress()
            it!!.start()
        }
        viewState.showProgress()
        mediaPlayer.prepareAsync()
    }

    fun releaseMp(){
        Timber.d("releaseMp")
        mediaPlayer.release()
    }

    fun stopTrack(){
        Timber.d("stopTrack")
        mediaPlayer.reset()
    }

    fun loadingError (message: Int){
        Timber.d("loadingError")
        viewState.showToast(message)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
        mediaPlayer.release()
        detailsProvider.unsubscrbe()
    }

    //save only tracks from current album
    fun filterTracks(tracks: ArrayList<TrackModel>): ArrayList<TrackModel>{
        Timber.d("filterTracks. tracks size = ${tracks.size}")
        return tracks.filter{ it.collectionId == album.collectionId } as ArrayList
    }
}