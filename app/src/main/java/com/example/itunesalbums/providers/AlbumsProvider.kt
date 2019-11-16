package com.example.itunesalbums.providers

import com.example.itunesalbums.R
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.net.ItunesApi
import com.example.itunesalbums.presenters.AlbumsPresenter
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import java.io.IOException


class AlbumsProvider//build retrofit api
    (private var presenter: AlbumsPresenter) {
    private var itunesApi: ItunesApi

    //constants for query
    private val baseUrl: String = "https://itunes.apple.com"
    private val entityQuery: String = "album"
    private val limitQuery: String = "200"
    private val attributeQuery: String = "albumTerm"
    private val media: String = "music"

    private var disposables: CompositeDisposable

    private val limit: Int = 20
    private lateinit var data: ArrayList<AlbumModel> //saved data

    init {
        Timber.d("Create new AlbumProvider")
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = (HttpLoggingInterceptor.Level.BASIC)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        disposables = CompositeDisposable()
        itunesApi = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(ItunesApi::class.java)
    }

    //selector for sort data
    fun selector(p: AlbumModel): String = p.collectionName

    fun getAlbums(name: String, offset: Int) {
        Timber.d("itunesApi.getData for term $name and offset: $offset")

        val disposable = itunesApi.getData(attribute = attributeQuery, term = name, entity = entityQuery, limit = limitQuery, media = media)
            .onErrorResumeNext { exception -> Single.error(exception) }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<AlbumModel.Response>() {
                override fun onSuccess(t: AlbumModel.Response) {
                    Timber.d("getData onSuccess. response = $t. albums size = ${t.results.size}")
                    t.results.sortBy({ selector(it) })
                    data = t.results
                    loadMore(offset)
                }
                override fun onError(e: Throwable) {
                    Timber.e(e)
                    if (e is IOException || e is NullPointerException) { //irrelevant network problem or API that throws on cancellation
                        presenter.loadingError(R.string.networkError)
                    } else {
                        presenter.loadingError(R.string.unknownError)
                    }
                }
            })
        disposables.add(disposable)
    }

    //RX unsubscribe
    fun unsubscrbe(){
        Timber.d("unsubscrbe. disposables size = ${disposables.size()}")
        disposables.dispose()
    }

    //get data from saved array with offset
    fun loadMore(offset: Int) {
        Timber.d("loadMore with offset = $offset")
        presenter.loadingComplete(data.subList(offset, calculateToIndex(offset)))
    }

    //calculating end index for sublist
    fun calculateToIndex(offset: Int): Int {
        if (offset + limit <= data.size)
            return offset + limit
        else return data.size
    }
}
