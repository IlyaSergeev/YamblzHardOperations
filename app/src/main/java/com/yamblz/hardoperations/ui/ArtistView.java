package com.yamblz.hardoperations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
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

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;


    public ArtistView(Context context)
    {
        super(context);
        init();
    }

    public ArtistView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        float titleFontSize = getResources().getDimensionPixelSize(R.dimen.artist_card_title_font_size);
        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextSize(titleFontSize);

        float descriptionFontSize = getResources().getDimensionPixelSize(R.dimen.artist_card_font_size);
        descriptionPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        descriptionPaint.setTextSize(descriptionFontSize);
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
        int posterLRPosterPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        int posterTopPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);

        int imageHeight = getResources().getDimensionPixelOffset(R.dimen.poster_height);
        canvas.drawRect(posterLRPosterPadding,
                        posterTopPadding,
                        getWidth() - posterLRPosterPadding,
                        imageHeight,
                        getRectPaint());

        //draw title
        float titleTextHeight = getTextHeight(artist.getName(), getWidth(), titlePaint);
        int posterTextMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        int textLRPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);

        StaticLayout titleStaticLayout = getStaticLayout(artist.getName(),
                                                         getWidth() - textLRPadding,
                                                         titlePaint);
        canvas.save();
        canvas.translate(textLRPadding, posterTopPadding + imageHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        //draw description
        int titleDescMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

        StaticLayout descriptionStaticLayout = getStaticLayout(getArtistDescription(),
                                                               getWidth() - textLRPadding,
                                                               descriptionPaint);
        canvas.save();
        canvas.translate(textLRPadding,
                         posterTopPadding + imageHeight + posterTextMargin + titleTextHeight + titleDescMargin);
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

        int posterLRTextPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
        int width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);

        int textWidth = width - (2 * posterLRTextPadding);
        //TODO add paddings
        int height = 0;
        height += getResources().getDimensionPixelOffset(R.dimen.poster_height);
        height += getTextHeight(artist.getName(), textWidth, titlePaint);
        height += getTextHeight(getArtistDescription(), textWidth, descriptionPaint);

        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_bottom_padding);
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

        setMeasuredDimension(width, height);
    }

    private float getTextHeight(String text, int width, TextPaint textPaint)
    {
        return getStaticLayout(text, width, textPaint).getHeight();
    }

    private StaticLayout getStaticLayout(String text, int width, TextPaint textPaint)
    {
        if (text == null)
        {
            text = "";
        }
        return new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
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
        descriptionText += "\n" + getResources().getQuantityString(R.plurals.artistAlbums,
                                                                   artist.getAlbumsCount(),
                                                                   artist.getAlbumsCount());
        descriptionText += "\n" + getResources().getQuantityString(R.plurals.artistTracks,
                                                                   artist.getTracksCount(),
                                                                   artist.getTracksCount());
        return descriptionText;
    }
}
