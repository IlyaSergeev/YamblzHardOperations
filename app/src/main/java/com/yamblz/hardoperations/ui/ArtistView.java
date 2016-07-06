package com.yamblz.hardoperations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        int imageHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.poster_height);
        canvas.drawRect(0, 0, getWidth(), imageHeight, getRectPaint());

        float titleTextHeight = getTextHeight(artist.getName(), getWidth());

        StaticLayout titleStaticLayout = new StaticLayout(artist.getName(), getTextPaint(), getWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        canvas.save();
        canvas.translate(0, imageHeight);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        StaticLayout descriptionStaticLayout = new StaticLayout(getArtistDescription(), getTextPaint(), getWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        canvas.save();
        canvas.translate(0, imageHeight + titleTextHeight);
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

        int width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);

        //TODO add paddings
        int height = 0;
        height += getContext().getResources().getDimensionPixelOffset(R.dimen.poster_height);
        height += getTextHeight(artist.getName(), width);
        height += getTextHeight(getArtistDescription(), width);

        setMeasuredDimension(width, height);
    }

    private float getTextHeight(String text, int maxWidth)
    {
        if (text == null)
        {
            text = "";
        }
        StaticLayout textLayout = new StaticLayout(text, getTextPaint(), maxWidth, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        return textLayout.getHeight();
    }

    private TextPaint getTextPaint()
    {
        float fontSize = getContext().getResources().getDimensionPixelSize(R.dimen.card_font_size);
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        paint.setTextSize(fontSize);
        return paint;
    }

    private Paint getRectPaint()
    {
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.rgb(0xff, 0, 0));
        rectPaint.setStrokeWidth(10);
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
