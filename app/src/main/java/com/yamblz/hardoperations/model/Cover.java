package com.yamblz.hardoperations.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by i-sergeev on 06.07.16
 */
public class Cover
{
    @SerializedName("small")
    private final String smallImageUrl;

    @SerializedName("big")
    private final String bigImageUrl;

    public Cover(String smallImageUrl, String bigImageUrl)
    {
        this.smallImageUrl = smallImageUrl;
        this.bigImageUrl = bigImageUrl;
    }

    public String getSmallImageUrl()
    {
        return smallImageUrl;
    }

    public String getBigImageUrl()
    {
        return bigImageUrl;
    }
}
