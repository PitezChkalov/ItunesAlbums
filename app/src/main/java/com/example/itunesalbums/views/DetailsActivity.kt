package com.example.itunesalbums.views

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.itunesalbums.models.AlbumModel

import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itunesalbums.R
import com.example.itunesalbums.adapters.TracksAdapter
import com.example.itunesalbums.models.TrackModel
import com.example.itunesalbums.presenters.DetailsPresenter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import timber.log.Timber
import kotlin.collections.ArrayList


class DetailsActivity : MvpAppCompatActivity(), DetailsView {

    private lateinit var adapter: TracksAdapter
    private lateinit var image: ImageView
    private lateinit var artistName: TextView
    private lateinit var albumName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewError: TextView
    private lateinit var textViewGenre: TextView
    private lateinit var textViewCopyright: TextView


    lateinit var album: AlbumModel
    @InjectPresenter
    lateinit var presenter: DetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        image = findViewById(R.id.album_image_details)
        artistName = findViewById(R.id.textView_artist_details)
        albumName = findViewById(R.id.textView_album_details)
        recyclerView = findViewById(R.id.rv_details)
        textViewError = findViewById(R.id.textView_error_tracks)
        progressBar = findViewById(R.id.progressBar_tracks)
        textViewGenre = findViewById(R.id.textView_genre_details)
        textViewCopyright = findViewById(R.id.textView_copyright_details)

        //in cases when intent will not contain ParcelableExtra
        if (intent.getParcelableExtra<AlbumModel>("album") != null) {
            //save album in presenter for cases when intent will not contain Parcelable
            presenter.setData(intent.getParcelableExtra("album"))
        }
    }

    override fun setData(album: AlbumModel) {
        Timber.d("set layout data for album: ${album.collectionName}")
        this.album = album
        Picasso.get().load(album.artworkUrl100).into(image)
        image.setOnClickListener {
            Timber.d("Show collection in browser")
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(album.collectionViewUrl))
            startActivity(intent)
        }

        albumName.text = album.collectionName
        artistName.text = album.artistName
        val s =
            "${album.primaryGenreName} ${getResources().getString(R.string.dot)} ${album.getReleaseDateFormat()}"
        textViewGenre.text = s
        textViewCopyright.text = album.copyright
        adapter = TracksAdapter(presenter)
        recyclerView.adapter = adapter
        val lm = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = lm
        recyclerView.setHasFixedSize(true)

        //load track list for current album
        presenter.loadTracks(album.collectionName)
    }


    override fun setTracks(tracks: ArrayList<TrackModel>) {
        Timber.d("setTracks track list size = ${tracks.size}")
        textViewError.visibility = View.GONE
        adapter.tracks = tracks
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        Timber.d("onBackPressed")
        super.onBackPressed()
        //release MediaPlayer
        presenter.releaseMp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showProgress() {
        Timber.d("showProgress")
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        Timber.d("hideProgress")
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: Int) {
        Timber.d("showToast message: ${getResources().getString(message)}")
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: Int) {
        Timber.d("showMessage message: ${getResources().getString(message)}")
        recyclerView.visibility = View.GONE
        textViewError.visibility = View.VISIBLE
        textViewError.text = getResources().getString(message)
    }


    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

}
