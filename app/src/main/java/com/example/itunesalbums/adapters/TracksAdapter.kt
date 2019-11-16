package com.example.itunesalbums.adapters

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.example.itunesalbums.R
import com.example.itunesalbums.models.TrackModel
import com.example.itunesalbums.presenters.DetailsPresenter
import com.squareup.picasso.Picasso
import okio.`-DeprecatedOkio`
import timber.log.Timber
import java.lang.Exception

class TracksAdapter(var presenter: DetailsPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var tracks: ArrayList<TrackModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_track, parent, false)
        return DetailsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailsViewHolder)
            holder.bind(tracks[position], presenter)
    }

    class DetailsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewTitle = itemView.findViewById<TextView>(R.id.rvtracks_title)
        private val picture = itemView.findViewById<ImageView>(R.id.rv_tracks_photo)

        fun bind(content: TrackModel, presenter: DetailsPresenter) {
            textViewTitle.text = content.trackName
            Picasso.get().load(content.artworkUrl100).into(picture)
            itemView.setOnClickListener {
                Timber.d("click on item in recyclerView: ${content.trackName}")
                presenter.playTrack(content.previewUrl)
            }
        }


    }

}