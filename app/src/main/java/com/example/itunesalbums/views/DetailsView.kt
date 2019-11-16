package com.example.itunesalbums.views

import android.media.MediaPlayer
import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.models.TrackModel

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface DetailsView : MvpView {
    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun setTracks(tracks: ArrayList<TrackModel>)
    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showProgress()
    fun hideProgress()
    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showToast(message: Int)
    fun showMessage (message: Int)
    fun setData(album: AlbumModel)
}