package com.example.itunesalbums.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.itunesalbums.R
import com.example.itunesalbums.adapters.AlbumsAdapter
import com.example.itunesalbums.adapters.EndlessRecyclerViewScrollListener
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.presenters.AlbumsPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

import java.util.concurrent.TimeUnit
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber


class MainActivity : MvpAppCompatActivity(), AlbumsView {

    private lateinit var textViewEmpty: TextView
    private lateinit var disposables: CompositeDisposable
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var rv: RecyclerView
    private lateinit var adapter: AlbumsAdapter
    private var searchText: String = ""
    @InjectPresenter
    lateinit var albumsPresenter: AlbumsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setContentView(R.layout.activity_albums)
        progressBar = findViewById(R.id.progressBar_albums)
        textViewEmpty = findViewById(R.id.albums_empty)
        textViewEmpty.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        disposables = CompositeDisposable()
        adapter = AlbumsAdapter()
        adapter.presenter = albumsPresenter
        rv = findViewById(R.id.rv_albums)
        rv.adapter = this.adapter
        val lm = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rv.layoutManager = lm
        rv.setHasFixedSize(true)
        //set endless scroll listener for recyclerView
        rv.addOnScrollListener(object : EndlessRecyclerViewScrollListener(lm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Timber.d("onLoadMore totalItemsCount=$totalItemsCount")
                //load new items with offset equals total items count
                albumsPresenter.loadMoreItems(totalItemsCount)
            }

        })
    }

    override fun hideProgress() {
        Timber.d("hideProgress")
        progressBar.visibility = View.GONE
    }

    //load new albums in adapter
    override fun update(albums: List<AlbumModel>) {
        Timber.d("update albums size = ${albums.size}")
        rv.post {
            adapter.setData(albums)
        }
    }

    //scroll recyclerView to position when activity was recreated
    override fun setRvPosition(rvState: Int) {
        Timber.d("setRvPosition position = $rvState")
        rv.post {
            rv.scrollToPosition(rvState)
        }
     }

    override fun showProgress() {
        Timber.d("showProgress")
        if(textViewEmpty.isVisible)
            textViewEmpty.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun cleanData() {
        Timber.d("cleanData")
        adapter.cleanData()
    }

    override fun showError(message: Int) {
        Timber.d("showError message = ${getResources().getText(message)}")
        Toast.makeText(this, getResources().getText(message), Toast.LENGTH_LONG).show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.d("onCreateOptionsMenu")
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)

        val myActionMenuItem: MenuItem = menu.findItem(R.id.action_search)

        val searchView: SearchView = myActionMenuItem.actionView as SearchView
        searchView.setQueryHint(getResources().getString(R.string.searchHint))

        //set OnQueryTextListener for searchView. We will receive items every 500ms
        val disposable = Observable.create<String> { emitter ->
            val listener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null)
                        emitter.onNext(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!emitter.isDisposed && newText != null) { //если еще не отписались
                        emitter.onNext(newText) //отправляем текущее состояние
                        return true
                    }
                    return false
                }
            }
            searchView.setOnQueryTextListener(listener)
        }
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("OnQueryTextListener onNext text = ${it.toString()}")
                albumsPresenter.loadAlbums(it.toString(), 0)
            })
        //add disposable to CompositeDisposable to dispose it later
        disposables.add(disposable)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        val layoutManager = rv.getLayoutManager() as LinearLayoutManager
        //save rv position
        albumsPresenter.setRvPosition(layoutManager.findFirstVisibleItemPosition())
        disposables.dispose()
    }

    override fun showDetails(album: AlbumModel) {
        Timber.d("showDetails for album ${album.collectionName}")
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("album", album)
        startActivity(intent)
    }

}
