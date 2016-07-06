package com.yamblz.hardoperations.communication;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.yamblz.hardoperations.R;
import com.yamblz.hardoperations.model.Artist;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by i-sergeev on 06.07.16
 */
public class Api
{
    @NonNull
    private final Context context;
    private final Gson gson = new Gson();


    public Api(@NonNull Context context)
    {
        this.context = context;
    }

    public Artist[] getArtists()
    {

        InputStream inStream = context.getResources().openRawResource(R.raw.artists);
        InputStreamReader inStreamReader = new InputStreamReader(inStream);

        return gson.fromJson(inStreamReader, Artist[].class);
    }
}
