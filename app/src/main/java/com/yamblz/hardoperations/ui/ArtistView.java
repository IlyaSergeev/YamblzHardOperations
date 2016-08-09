package com.yamblz.hardoperations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by i-sergeev on 06.07.16
 */
public class ArtistView extends View
{
    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final int PALETTE_POPULATION = 100;

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;
    private int defaultTextColor;
    private int defaultBackgroundColor;

    private Artist artist;
    private Bitmap posterBitmap;
    private Palette palette;

    private Palette defaultPalette;

    private ImageLoadTarget imageLoadTarget;
    private Picasso picasso;
    private AsyncTask<Bitmap, Void, Palette> paletteAsyncTask;

    private AsyncTask<Palette.Swatch, Void, Palette> defaultPaletteAsyncTask;

    private Paint mBitmapPaint = new Paint();
    private Paint mRectPaint = new Paint();

    private int posterTopPadding;
    private int imageHeight;
    private int posterTextMargin;
    private int textLRPadding;
    private int titleDescMargin;
    private int bottomPadding;

    private String albumsCount;
    private String tracksCount;
    private String artistDescription;

    private StaticLayout titleStaticLayout;
    private StaticLayout descriptionStaticLayout;

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

        //noinspection deprecation
        defaultTextColor = resources.getColor(R.color.default_text_color);
        //noinspection deprecation
        defaultBackgroundColor = resources.getColor(R.color.default_background_color);

        float titleFontSize = resources.getDimensionPixelSize(R.dimen.artist_card_title_font_size);
        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextSize(titleFontSize);
        titlePaint.setColor(defaultTextColor);

        float descriptionFontSize = resources.getDimensionPixelSize(R.dimen.artist_card_font_size);
        descriptionPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        descriptionPaint.setTextSize(descriptionFontSize);
        descriptionPaint.setColor(defaultTextColor);

        posterTopPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        imageHeight = getResources().getDimensionPixelOffset(R.dimen.poster_height);
        posterTextMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        textLRPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
        titleDescMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);
        bottomPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_bottom_padding);
    }

    public void setArtist(Artist artist)
    {
        this.artist = artist;
        invalidate();
        requestLayout();
        albumsCount = getResources().getQuantityString(R.plurals.artistAlbums,
                artist.getAlbumsCount(),
                artist.getAlbumsCount());
        tracksCount = getResources().getQuantityString(R.plurals.artistTracks,
                artist.getTracksCount(),
                artist.getTracksCount());
        artistDescription = null;

        if (imageLoadTarget != null)
        {
            Picasso.with(getContext()).cancelRequest(imageLoadTarget);
            imageLoadTarget = null;
            posterBitmap = null;
        }
        if(paletteAsyncTask != null)
        {
            paletteAsyncTask.cancel(true);
            paletteAsyncTask = null;
            palette = null;
        }
        imageLoadTarget = new ImageLoadTarget();
        picasso.load(artist.getCover().getBigImageUrl()).into(imageLoadTarget);
    }

    private void onPosterBitmapLoaded(Bitmap bitmap)
    {
        this.posterBitmap = bitmap;
        invalidate();
    }

    private void onPaletteCreated(Palette palette) {
        this.palette = palette;
        invalidate();
    }

    private void onDefaultPaletteCreated(Palette palette) {
        this.defaultPalette = palette;
        invalidate();
    }

    private void drawBackground(Canvas canvas)
    {
        if(palette == null)
        {
            return;
        }

        canvas.drawRect(0, 0, getWidth(),
                getHeight(),
                getRectPaint(palette.getLightVibrantColor(defaultBackgroundColor)));
    }

    private void setTextColors()
    {
        if(palette == null)
        {
            return;
        }

        int textColor = palette.getDarkMutedColor(defaultTextColor);
        titlePaint.setColor(palette.getDarkMutedColor(textColor));
        descriptionPaint.setColor(textColor);
    }

    private void drawPosterBitmap(Canvas canvas)
    {
        if (posterBitmap == null)
        {
            canvas.drawRect(posterTopPadding,
                    posterTopPadding,
                    getWidth() - posterTopPadding,
                    imageHeight,
                    getRectPaint(WHITE_COLOR));
        }
        else
        {
            Bitmap scaledBitmap = BitmapUtils.fitToCenterBitmap(posterBitmap,
                    getWidth() - (2 * posterTopPadding),
                    imageHeight);
            canvas.drawBitmap(scaledBitmap,
                    posterTopPadding,
                    posterTopPadding,
                    getBitmapPaint());
            scaledBitmap.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (artist == null)
        {
            return;
        }

        drawBackground(canvas);

        setTextColors();

        drawPosterBitmap(canvas);

        //draw title
        float titleTextHeight = getTextHeight(artist.getName(), getWidth(), titlePaint);

        canvas.save();
        canvas.translate(textLRPadding, posterTopPadding + imageHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        //draw description
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

        int width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);

        int textWidth = width - (2 * textLRPadding);

        int height = 0;
        height += imageHeight;
        height += getTextHeight(artist.getName(), textWidth, titlePaint);
        height += getTextHeight(getArtistDescription(), textWidth, descriptionPaint);

        height += posterTopPadding;
        height += bottomPadding;
        height += posterTextMargin;
        height += titleDescMargin;

        setMeasuredDimension(width, height);

        titleStaticLayout = getStaticLayout(artist.getName(), getMeasuredWidth() - textLRPadding,
                titlePaint);
        descriptionStaticLayout = getStaticLayout(getArtistDescription(), getMeasuredWidth() - textLRPadding,
                descriptionPaint);

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
        mRectPaint.reset();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(color);
        return mRectPaint;
    }

    private Paint getBitmapPaint()
    {
        mBitmapPaint.reset();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);
        return mBitmapPaint;
    }

    private String getArtistDescription()
    {
        if (artist == null)
        {
            return "";
        }
        if(artistDescription == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(artist.getDescription()).append("\n").append("\n").append(albumsCount)
                    .append("\n").append(tracksCount);
            artistDescription = sb.toString();
        }

        return artistDescription;
    }

    private void createPalette()
    {
        if (posterBitmap != null && !posterBitmap.isRecycled())
        {
            paletteAsyncTask = Palette.from(posterBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    onPaletteCreated(palette);
                }
            });

        }
        else
        {
            createDefaultPalette();
        }
    }

    private void createDefaultPalette()
    {
        if(defaultPalette == null && defaultPaletteAsyncTask == null) {
            defaultPaletteAsyncTask = new AsyncTask<Palette.Swatch, Void, Palette>() {
                @Override
                protected Palette doInBackground(Palette.Swatch... swatches) {
                    return Palette.from(Arrays.asList(swatches));
                }

                @Override
                protected void onPostExecute(Palette palette) {
                    onDefaultPaletteCreated(palette);

                }
            }.execute(new Palette.Swatch(WHITE_COLOR, PALETTE_POPULATION));
        }

    }



    private final class ImageLoadTarget implements Target
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            imageLoadTarget = null;
            createPalette();
            onPosterBitmapLoaded(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable)
        {
            imageLoadTarget = null;
            createPalette();
            onPosterBitmapLoaded(null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable)
        {
            createPalette();
            onPosterBitmapLoaded(null);
        }
    }
}
