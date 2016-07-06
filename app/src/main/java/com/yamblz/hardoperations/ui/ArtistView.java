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

    private final Palette defaultPalette = Palette.from(Collections.singletonList(new Palette.Swatch(
            WHITE_COLOR,
            PALETTE_POPULATION)));

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;
    private Paint posterPaint;

    private int defaultTextColor;
    private int defaultBackgroundColor;

    private Bitmap posterBitmap;
    private ImageLoadTarget imageLoadTarget;
    private Picasso picasso;

    private Artist artist;
    private String descriptionText;

    private int cardTopPadding;
    private int cardBottomPadding;
    private int posterTextMargin;
    private int titleDescriptionMargin;
    private int posterLRPosterPadding;
    private int posterHeight;
    private int textLRPadding;

    private float titleTextHeight = 0f;

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

        posterHeight = resources.getDimensionPixelOffset(R.dimen.poster_height);
        cardTopPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        cardBottomPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_bottom_padding);
        posterTextMargin = resources.getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        titleDescriptionMargin = resources.getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);
        posterLRPosterPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_lr_poster_padding);
        textLRPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);


        posterPaint = new Paint();
        posterPaint.setAntiAlias(true);
        posterPaint.setFilterBitmap(true);
        posterPaint.setDither(true);
    }

    public void setArtist(@NonNull Artist artist)
    {
        this.artist = artist;

        descriptionText = artist.getDescription() + "\n";
        descriptionText += "\n" + getResources().getQuantityString(R.plurals.artistAlbums,
                                                                   artist.getAlbumsCount(),
                                                                   artist.getAlbumsCount());
        descriptionText += "\n" + getResources().getQuantityString(R.plurals.artistTracks,
                                                                   artist.getTracksCount(),
                                                                   artist.getTracksCount());

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

        //Draw background
        Palette palette = getPalette();
        canvas.drawRect(0, 0, getWidth(), getHeight(), getRectPaint(palette.getLightVibrantColor(
                defaultBackgroundColor)));

        int textColor = palette.getDarkMutedColor(defaultTextColor);
        titlePaint.setColor(palette.getDarkMutedColor(textColor));
        descriptionPaint.setColor(textColor);

        //draw poster
        if (posterBitmap == null)
        {
            canvas.drawRect(posterLRPosterPadding,
                            cardTopPadding,
                            getWidth() - posterLRPosterPadding,
                            posterHeight,
                            getRectPaint(WHITE_COLOR));
        }
        else
        {
            Bitmap scaledBitmap = BitmapUtils.fitToCenterBitmap(posterBitmap,
                                                                getWidth() - (2 * posterLRPosterPadding),
                                                                posterHeight);
            canvas.drawBitmap(scaledBitmap,
                              posterLRPosterPadding,
                              cardTopPadding,
                              posterPaint);
            scaledBitmap.recycle();
        }

        //draw title
        StaticLayout titleStaticLayout = getStaticLayout(artist.getName(),
                                                         getWidth() - textLRPadding,
                                                         titlePaint);
        canvas.save();
        canvas.translate(textLRPadding, cardTopPadding + posterHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        //draw description
        StaticLayout descriptionStaticLayout = getStaticLayout(descriptionText,
                                                               getWidth() - textLRPadding,
                                                               descriptionPaint);
        canvas.save();
        canvas.translate(textLRPadding,
                         cardTopPadding + posterHeight + posterTextMargin + titleTextHeight + titleDescriptionMargin);
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

        titleTextHeight = getStaticLayout(artist.getName(), textWidth, titlePaint).getHeight();
        float descriptionTextHeight = getStaticLayout(descriptionText,
                                                      textWidth,
                                                      descriptionPaint)
                .getHeight();

        int height = 0;
        height += posterHeight;
        height += titleTextHeight;
        height += descriptionTextHeight;

        height += cardTopPadding;
        height += cardBottomPadding;
        height += posterTextMargin;
        height += titleDescriptionMargin;

        setMeasuredDimension(width, height);
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
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(color);
        return rectPaint;
    }

    private Palette getPalette()
    {
        if (posterBitmap != null && !posterBitmap.isRecycled())
        {
            //TODO need optimize
            return Palette.from(posterBitmap).generate();
        }
        else
        {
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
