package com.yamblz.hardoperations.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yamblz.hardoperations.R;
import com.yamblz.hardoperations.model.Artist;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by i-sergeev on 06.07.16
 */
public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistVH>
{
    @NonNull
    private final Artist[] artists;

    public ArtistsAdapter(@Nullable Artist[] artists)
    {
        if (artists == null)
        {
            artists = new Artist[0];
        }
        this.artists = artists;
    }

    @Override
    public ArtistVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.artist_card, parent, false);
        return new ArtistVH(view);
    }

    @Override
    public void onBindViewHolder(ArtistVH holder, int position)
    {
        holder.bind(artists[position]);
    }

    @Override
    public int getItemCount()
    {
        return artists.length;
    }

    public class ArtistVH extends RecyclerView.ViewHolder
    {
        @BindView(R.id.artist_root_view)
        ArtistView artistView;

        public ArtistVH(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull Artist artist)
        {
            artistView.setArtist(artist);
        }
    }
}
