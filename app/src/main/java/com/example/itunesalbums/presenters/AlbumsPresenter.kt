package com.example.itunesalbums.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.itunesalbums.R
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.models.TrackModel
import com.example.itunesalbums.providers.AlbumsProvider
import com.example.itunesalbums.views.AlbumsView
import timber.log.Timber
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@InjectViewState
class AlbumsPresenter: MvpPresenter<AlbumsView>() {

    var albumsProvider: AlbumsProvider
    init {
        albumsProvider = AlbumsProvider(this)
    }
    //load albums from AlbumProvider
    fun loadAlbums(name: String, offset: Int){
        Timber.d("load albums for term $name with offset $offset")
        if(name.length>0) {
            viewState.cleanData()
            viewState.showProgress()
            try {
                albumsProvider.getAlbums(name, offset)
            }
            catch (e: Exception) {
                loadingError(R.string.unknownError)
            }
        }
    }

    //calling from AlbumsProvider when loading albums is completed
    fun loadingComplete(albums: List<AlbumModel>){
        Timber.d("Loading Complete. Albums size = ${albums.size}")
        viewState.hideProgress()
        if(albums.isNotEmpty())
        viewState.update(albums)
    }

    fun loadingError(message: Int){
        Timber.d("Loading error")
        viewState.cleanData()
        viewState.hideProgress()
        viewState.showError(message)
    }

    fun setRvPosition(position:Int){
        Timber.d("Set rv position to $position")
        viewState.setRvPosition(position)
    }

    fun loadMoreItems(offset: Int){
        Timber.d("load more items with offset $offset")
        viewState.showProgress()
        albumsProvider.loadMore(offset)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
        albumsProvider.unsubscrbe()
    }

    fun showDetails(album:AlbumModel){
        Timber.d("show details for album ${album.collectionName}")
        viewState.showDetails(album)
    }
}