package com.yamblz.hardoperations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yamblz.hardoperations.R;
import com.yamblz.hardoperations.model.Artist;
import com.yamblz.hardoperations.utils.BitmapUtils;

import java.util.Collections;

/**
 * Created by i-sergeev on 06.07.16
 */
public class ArtistView extends View
{
    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final int PALETTE_POPULATION = 100;

    private TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint descriptionPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int defaultTextColor;
    private int defaultBackgroundColor;

    private Artist artist;
    private Bitmap posterBitmap;
    private ImageLoadTarget imageLoadTarget;
    private Picasso picasso;

    private Palette palette;
    private Palette defaultPalette;
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bitmapPaint= new Paint();;

    private int posterTopPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
    private int posterLRPosterPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
    private int imageHeight = getResources().getDimensionPixelOffset(R.dimen.poster_height);
    private int posterTextMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
    private int textLRPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
    private int titleDescMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

    int posterLRTextPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);

    private int d = posterTopPadding + imageHeight + posterTextMargin;
    private int e = posterTopPadding + imageHeight + posterTextMargin + titleDescMargin;
    private int f = getResources().getDimensionPixelOffset(R.dimen.poster_height) +
            getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding) +
            getResources().getDimensionPixelOffset(R.dimen.artist_card_bottom_padding) +
            getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin) +
            getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

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

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context)
    {
        picasso = Picasso.with(context);

        Resources resources = getResources();

        defaultTextColor = resources.getColor(R.color.default_text_color);
        defaultBackgroundColor = resources.getColor(R.color.default_background_color);

        float titleFontSize = resources.getDimensionPixelSize(R.dimen.artist_card_title_font_size);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextSize(titleFontSize);
        titlePaint.setColor(defaultTextColor);

        float descriptionFontSize = resources.getDimensionPixelSize(R.dimen.artist_card_font_size);
        descriptionPaint.setTextSize(descriptionFontSize);
        descriptionPaint.setColor(defaultTextColor);

        Palette.Swatch swatch = new Palette.Swatch(WHITE_COLOR, PALETTE_POPULATION);
        defaultPalette = Palette.from(Collections.singletonList(swatch));

        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);
    }

    public void setArtist(Artist artist)
    {
        this.artist = artist;
        invalidate();
        requestLayout();

        if (imageLoadTarget != null)
        {
            Picasso.with(getContext()).cancelRequest(imageLoadTarget);
            imageLoadTarget = null;
        }
        imageLoadTarget = new ImageLoadTarget();
        picasso.load(artist.getCover().getBigImageUrl()).into(imageLoadTarget);
    }

    private void setPosterBitmap(Bitmap bitmap)
    {
        posterBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (artist == null)
        {
            return;
        }

        Palette palette = getPalette();
        canvas.drawRect(0, 0, getWidth(), getHeight(), getRectPaint(palette.getLightVibrantColor(defaultBackgroundColor)));

        int textColor = palette.getDarkMutedColor(defaultTextColor);
        titlePaint.setColor(palette.getDarkMutedColor(textColor));
        descriptionPaint.setColor(textColor);

        if (posterBitmap == null)
        {
            canvas.drawRect(posterLRPosterPadding,
                            posterTopPadding,
                            getWidth() - posterLRPosterPadding,
                            imageHeight,
                            getRectPaint(WHITE_COLOR));
        }
        else
        {
            Bitmap scaledBitmap = BitmapUtils.fitToCenterBitmap(posterBitmap,
                                                                getWidth() - (posterLRPosterPadding<<2),
                                                                imageHeight);
            canvas.drawBitmap(scaledBitmap,
                              posterLRPosterPadding,
                              posterTopPadding,
                              bitmapPaint);
            scaledBitmap.recycle();
        }


        float titleTextHeight = getTextHeight(artist.getName(), getWidth(), titlePaint);
        int a = getWidth() - textLRPadding;

        StaticLayout titleStaticLayout = getStaticLayout(artist.getName(),
                                                         a,
                                                         titlePaint);
        canvas.save();
        canvas.translate(textLRPadding, d);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        StaticLayout descriptionStaticLayout = getStaticLayout(getArtistDescription(),
                                                               a,
                                                               descriptionPaint);
        canvas.save();
        canvas.translate(textLRPadding, e + titleTextHeight);
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

        int textWidth = width - (2 * posterLRTextPadding);

        int height = f +
                (int) getTextHeight(artist.getName(), textWidth, titlePaint) +
                (int) getTextHeight(getArtistDescription(), textWidth, descriptionPaint);

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

    private Paint getRectPaint(int color)
    {
        rectPaint.setColor(color);
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

    private Palette getPalette()
    {
        if (posterBitmap != null && !posterBitmap.isRecycled()) {
            if (palette == null) palette = Palette.from(posterBitmap).generate();
            return palette;
        } else {
            return defaultPalette;
        }
    }

    private final class ImageLoadTarget implements Target
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            imageLoadTarget = null;
            setPosterBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable)
        {
            imageLoadTarget = null;
            setPosterBitmap(null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable)
        {
            setPosterBitmap(null);
        }
    }
}
