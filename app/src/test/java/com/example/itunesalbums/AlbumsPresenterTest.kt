package com.example.itunesalbums

import com.arellomobile.mvp.MvpView
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.presenters.AlbumsPresenter
import com.example.itunesalbums.providers.AlbumsProvider
import com.example.itunesalbums.views.AlbumsView
import com.example.itunesalbums.views.`AlbumsView$$State`
import org.mockito.MockitoAnnotations
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*


class AlbumPresenterTest {

    @Mock
    internal var albumsView: AlbumsView? = null

    @Mock
    internal var albumsViewState: `AlbumsView$$State`? = null

    @Mock
    internal var albumsProvider: AlbumsProvider? = null

    private lateinit var presenter: AlbumsPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = AlbumsPresenter()
        albumsProvider = mock(AlbumsProvider::class.java)
        presenter.albumsProvider = albumsProvider!!
        presenter.attachView(albumsView)
        presenter.setViewState(albumsViewState)
    }

    @Test
    fun showError() {
        presenter.loadingError(R.string.unknownError)
        verify(albumsViewState)!!.showError(R.string.unknownError)
    }

    @Test
    fun loadAlbums_emptyQuery() {
        presenter.loadAlbums("",0)
        verify(albumsProvider!!, never()).getAlbums("",0)
        //verify(albumsViewState, never())!!.update(ArrayList());
    }

    @Test
    fun loadAlbums_notEmptyQuery_emptyResult() {
        `when`(albumsProvider!!.getAlbums("1",0)).thenAnswer{presenter.loadingComplete(ArrayList())}
        presenter.loadAlbums("1",0)
        verify(albumsViewState)!!.showProgress()
        verify(albumsViewState)!!.cleanData()
        verify(albumsViewState!!).hideProgress()
        verify(albumsProvider!!).getAlbums("1",0)
        verify(albumsViewState, never())!!.update(ArrayList())
    }

    @Test
    fun loadAlbums_notEmptyQuery_NotEmptyResult() {
        val data = ArrayList<AlbumModel>()
        data.add(AlbumModel("","","","","","","","","","","",""))
        `when`(albumsProvider!!.getAlbums("1",0)).thenAnswer{presenter.loadingComplete(data)}
        presenter.loadAlbums("1",0)
        verify(albumsProvider!!).getAlbums("1",0)
        verify(albumsViewState!!).hideProgress()
        verify(albumsViewState)!!.update(data)
    }

    @Test
    fun loadAlbums_exception() {
        `when`(albumsProvider!!.getAlbums("1",0)).thenThrow(NullPointerException::class.java)
        presenter.loadAlbums("1",0)
        verify(albumsViewState!!, times(2)).cleanData()
        verify(albumsViewState!!).hideProgress()
        verify(albumsViewState!!).showError(R.string.unknownError)
    }

    @Test
    fun setRvPosition() {
        presenter.setRvPosition(1)
        verify(albumsViewState!!).setRvPosition(1)
    }

    @Test
    fun onDestroy() {
        presenter.onDestroy()
        verify(albumsProvider!!).unsubscrbe()
    }

    @Test
    fun showDetails() {
        val album = AlbumModel("","","","","","","","","","","","")

        presenter.showDetails(album)
        verify(albumsViewState!!).showDetails(album)
    }
}