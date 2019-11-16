package com.example.itunesalbums.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.*
import com.example.itunesalbums.models.AlbumModel

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface AlbumsView: MvpView {
    fun showProgress()
    fun hideProgress()
    @StateStrategyType(value = AddToEndStrategy::class)
    fun update(albums:List<AlbumModel>)
    fun setRvPosition(rvState: Int)
    @StateStrategyType(value = SingleStateStrategy::class)
    fun cleanData()
    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showError(message: Int)
    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showDetails(album: AlbumModel)
}