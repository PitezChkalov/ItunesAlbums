package com.example.itunesalbums.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.itunesalbums.R
import com.example.itunesalbums.models.AlbumModel
import com.example.itunesalbums.presenters.AlbumsPresenter
import com.squareup.picasso.Picasso
import timber.log.Timber

class AlbumsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var albums: ArrayList<AlbumModel> = ArrayList()
    lateinit var presenter:AlbumsPresenter

    override fun getItemCount(): Int {
        return albums.count()
     }

    fun setData(albums: List<AlbumModel>){
        this.albums.addAll(albums)
        notifyDataSetChanged()
    }

    fun cleanData(){
        this.albums.clear()
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlbumsViewHolder){
            holder.bind(albums.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_album, parent,false)
        return AlbumsViewHolder(itemView, presenter)
    }

    class AlbumsViewHolder(itemView: View, val presenter: AlbumsPresenter): RecyclerView.ViewHolder(itemView){
        private var artist: TextView = itemView.findViewById(R.id.rvtracks_title)
        private var album: TextView = itemView.findViewById(R.id.rvdetails_value)
        private var photo: ImageView = itemView.findViewById(R.id.rv_tracks_photo)

        fun bind(albumModel: AlbumModel){
            artist.text = albumModel.artistName
            album.text = albumModel.collectionName
            Picasso.get().load(albumModel.artworkUrl100).into(photo)
            itemView.setOnClickListener({
                Timber.d("click on ${albumModel.collectionName}")
                presenter.showDetails(albumModel)
            })
        }
    }

}
