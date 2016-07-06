package com.yamblz.hardoperations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.yamblz.hardoperations.R;
import com.yamblz.hardoperations.model.Artist;

/**
 * Created by i-sergeev on 06.07.16
 */
public class ArtistView extends View
{
    private Artist artist;

    public ArtistView(Context context)
    {
        super(context);
        init(context);
    }

    public ArtistView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context)
    {
        //TODO some initialization here
    }


    public void setArtist(Artist artist)
    {
        this.artist = artist;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (artist == null)
        {
            return;
        }
        //draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), getRectPaint());

        //draw poster
        int posterLRPosterPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        int posterTopPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);

        int imageHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.poster_height);
        canvas.drawRect(posterLRPosterPadding, posterTopPadding, getWidth() - posterLRPosterPadding, imageHeight, getRectPaint());

        //draw title
        float titleTextHeight = getTextHeight(artist.getName(), getWidth(), getTitlePaint());
        int posterTextMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        int textLRPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);

        StaticLayout titleStaticLayout = new StaticLayout(artist.getName(), getTitlePaint(), getWidth() - textLRPadding, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        canvas.save();
        canvas.translate(textLRPadding, posterTopPadding + imageHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        //draw description
        int titleDescMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

        StaticLayout descriptionStaticLayout = new StaticLayout(getArtistDescription(), getDescriptionPaint(), getWidth() - textLRPadding, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        canvas.save();
        canvas.translate(textLRPadding, posterTopPadding + imageHeight + posterTextMargin + titleTextHeight + titleDescMargin);
        descriptionStaticLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (artist == null)
        {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int posterLRTextPadding = getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
        int width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);

        int textWidth = width - (2 * posterLRTextPadding);
        //TODO add paddings
        int height = 0;
        height += getContext().getResources().getDimensionPixelOffset(R.dimen.poster_height);
        height += getTextHeight(artist.getName(), textWidth, getTitlePaint());
        height += getTextHeight(getArtistDescription(), textWidth, getDescriptionPaint());

        height += getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        height += getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_bottom_padding);
        height += getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        height += getContext().getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

        setMeasuredDimension(width, height);
    }

    private float getTextHeight(String text, int maxWidth, TextPaint textPaint)
    {
        if (text == null)
        {
            text = "";
        }
        StaticLayout textLayout = new StaticLayout(text, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        return textLayout.getHeight();
    }

    private TextPaint getDescriptionPaint()
    {
        float fontSize = getContext().getResources().getDimensionPixelSize(R.dimen.artist_card_font_size);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        paint.setTextSize(fontSize);
        return paint;
    }

    private TextPaint getTitlePaint()
    {
        float fontSize = getContext().getResources().getDimensionPixelSize(R.dimen.artist_card_font_size);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(fontSize);
        return paint;
    }

    private Paint getRectPaint()
    {
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.argb(0x60, 0xff, 0x00, 0x00));
        return rectPaint;
    }

    private String getArtistDescription()
    {
        if (artist == null)
        {
            return "";
        }
        String descriptionText = artist.getDescription() + "\n";
        descriptionText += "\n" + getContext().getResources().getQuantityString(R.plurals.artistAlbums, artist.getAlbumsCount(), artist.getAlbumsCount());
        descriptionText += "\n" + getContext().getResources().getQuantityString(R.plurals.artistTracks, artist.getTracksCount(), artist.getTracksCount());
        return descriptionText;
    }
}
