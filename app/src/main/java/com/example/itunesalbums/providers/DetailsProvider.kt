package com.example.itunesalbums.providers

import android.text.SpannableString
import com.example.itunesalbums.MyApplication
import com.example.itunesalbums.R
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.models.TrackModel
import com.example.itunesalbums.net.ItunesApi
import com.example.itunesalbums.presenters.AlbumsPresenter
import com.example.itunesalbums.presenters.DetailsPresenter
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Observable
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.observers.DisposableSingleObserver
import java.io.IOException
import java.lang.Exception
import java.net.SocketException
import java.net.UnknownHostException


class DetailsProvider//build retrofit api
    (private var presenter: DetailsPresenter) {
    private var itunesApi: ItunesApi

    //constants for query
    private val baseUrl: String = "https://itunes.apple.com"
    private val entityQuery: String = "musicTrack"
    private val attributeQuery: String = "albumTerm"
    private val mediaQuery: String = "music"

    private var disposables: CompositeDisposable //save disposables to unsubscribe later
    private lateinit var tracks:  ArrayList<TrackModel> //saved data
    private var loadingComplete = false //flag for determine if data is loaded

    init {
        Timber.d("Create new DetailsProvider")
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
    fun selector(p: TrackModel): Int = p.trackNumber

    fun getTracks(name: String) {
        Timber.d("getTracks for term $name")

        //download tracks if it was not loaded earlier
        if (!loadingComplete) {
            Timber.d("itunesApi.getTracks term $name")
            val disposable = itunesApi.getTracks(
                media = mediaQuery,
                term = name,
                entity = entityQuery,
                attribute = attributeQuery
            )
                .onErrorResumeNext { exception -> Single.error(exception) }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TrackModel.Response>() {
                    override fun onSuccess(t: TrackModel.Response) {
                        Timber.d("itunesApi.getTracks onSuccess. Track list size = ${t.results.size}")
                        t.results.distinctBy { it.trackNumber } //remove tracks with equals number
                        Timber.d("Track list size after distinct = ${t.results.size}")
                        t.results.sortBy { selector(it) } //sort tracks by number
                        tracks = presenter.filterTracks(t.results) //remove tracks from another album
                        Timber.d("Track list size after filter = ${tracks.size}")
                        presenter.loadingComplete(tracks)
                        loadingComplete = true
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
        else {
            Timber.e("getTracks return saved tracks")
            presenter.loadingComplete(tracks)
        }
    }

    //RX unsubscribe
    fun unsubscrbe(){
        Timber.d("unsubscrbe. disposables size = ${disposables.size()}")
        disposables.dispose()
    }
}
