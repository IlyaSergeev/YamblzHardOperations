package com.yamblz.hardoperations.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by i-sergeev on 06.07.16
 */
public class Artist
{
    @SerializedName("id")
    private final String id;

    @SerializedName("name")
    private final String name;

    @SerializedName("genres")
    private final String[] genres;

    @SerializedName("tracks")
    private final int tracksCount;

    @SerializedName("albums")
    private final int albumsCount;

    @SerializedName("link")
    private final String webCite;

    @SerializedName("description")
    private final String description;

    @SerializedName("cover")
    private final Cover cover;

    public Artist(String id,
                  String name,
                  String[] genres,
                  int tracksCount,
                  int albumsCount,
                  String webCite, String description, Cover cover)
    {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracksCount = tracksCount;
        this.albumsCount = albumsCount;
        this.webCite = webCite;
        this.description = description;
        this.cover = cover;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String[] getGenres()
    {
        return genres;
    }

    public int getTracksCount()
    {
        return tracksCount;
    }

    public int getAlbumsCount()
    {
        return albumsCount;
    }

    public String getWebCite()
    {
        return webCite;
    }

    public String getDescription()
    {
        return description;
    }

    public Cover getCover()
    {
        return cover;
    }
}
