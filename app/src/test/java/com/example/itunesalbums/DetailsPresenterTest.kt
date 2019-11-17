package com.example.itunesalbums

import com.arellomobile.mvp.MvpView
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.presenters.AlbumsPresenter
import com.example.itunesalbums.presenters.DetailsPresenter
import com.example.itunesalbums.providers.AlbumsProvider
import com.example.itunesalbums.providers.DetailsProvider
import com.example.itunesalbums.views.AlbumsView
import com.example.itunesalbums.views.DetailsView
import com.example.itunesalbums.views.`AlbumsView$$State`
import com.example.itunesalbums.views.`DetailsView$$State`
import org.mockito.MockitoAnnotations
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*


class DetailsPresenterTest {

    @Mock
    internal var detailsView: DetailsView? = null

    @Mock
    internal var detailsViewState: `DetailsView$$State`? = null

    @Mock
    internal var detailsProvider: DetailsProvider? = null

    private lateinit var presenter: DetailsPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = DetailsPresenter()
        detailsProvider = mock(DetailsProvider::class.java)
        presenter.detailsProvider = detailsProvider!!
        presenter.attachView(detailsView)
        presenter.setViewState(detailsViewState)
    }

    @Test
    fun showError() {
        presenter.loadingError(R.string.unknownError)
        verify(detailsViewState)!!.showToast(R.string.unknownError)
    }

    @Test
    fun loadingCompleteEmptyResult() {
        presenter.loadingComplete(ArrayList())
        verify(detailsViewState)!!.hideProgress()
        verify(detailsViewState)!!.showMessage(R.string.no_tracks)
    }
}