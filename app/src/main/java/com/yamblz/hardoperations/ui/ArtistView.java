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
public class ArtistView extends View {
    private static final int WHITE_COLOR = 0xFFFFFF;
    private static final int PALETTE_POPULATION = 100;

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;

    private int defaultTextColor;
    private int defaultBackgroundColor;

    private Artist artist;
    private Bitmap posterBitmap;
    private ImageLoadTarget imageLoadTarget;
    private Picasso picasso;

    private Paint rectPaint;
    private Paint bitmapPaint;
    private StaticLayout titleStaticLayout;
    private StaticLayout descriptionStaticLayout;
    private float titleTextHeight;
    private int posterLRPosterPadding;
    private int imageHeight;
    private int posterTopPadding;
    private int textLRPadding;
    private int posterTextMargin;
    private int titleDescMargin;

    //region constructors
    public ArtistView(Context context) {
        super(context);
        init(context);
    }

    public ArtistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArtistView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    //endregion

    private void init(@NonNull Context context) {
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);

        initResources();
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        requestLayout();

        if (imageLoadTarget != null) {
            Picasso.with(getContext()).cancelRequest(imageLoadTarget);
            imageLoadTarget = null;
        }
        imageLoadTarget = new ImageLoadTarget();
        picasso.load(artist.getCover().getBigImageUrl()).into(imageLoadTarget);
        if (getWidth() != 0) {
            updateText();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateText();
    }

    private void updateText() {
        titleStaticLayout = getStaticLayout(artist.getName(),
                getWidth() - textLRPadding,
                titlePaint);
        descriptionStaticLayout = getStaticLayout(getArtistDescription(),
                getWidth() - textLRPadding,
                descriptionPaint);
        Palette palette = getPalette();
        int textColor = palette.getDarkMutedColor(defaultTextColor);
        titlePaint.setColor(palette.getDarkMutedColor(textColor));
        descriptionPaint.setColor(textColor);
        titleTextHeight = getTextHeight(artist.getName(), getWidth(), titlePaint);
    }

    private void setPosterBitmap(Bitmap bitmap) {
        posterBitmap = bitmap;
        bitmapPallete = null;
        //draw poster
        int imageHeight = getResources().getDimensionPixelOffset(R.dimen.poster_height);
        if (bitmap != null) {
            //scale bitmap
            int posterLRPosterPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
            Bitmap scaledBitmap = BitmapUtils.fitToCenterBitmap(posterBitmap,
                    getWidth() - (2 * posterLRPosterPadding),
                    imageHeight);
            // posterBitmap.recycle();
            posterBitmap = scaledBitmap;
            bitmapPallete = Palette.from(posterBitmap).generate();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (artist == null) {
            return;
        }
        //region Draw background
        Palette palette = getPalette();
        canvas.drawRect(0, 0, getWidth(), getHeight(), getRectPaint(palette.getLightVibrantColor(defaultBackgroundColor)));

        if (posterBitmap == null) {
            canvas.drawRect(posterLRPosterPadding,
                    posterTopPadding,
                    getWidth() - posterLRPosterPadding,
                    imageHeight,
                    getRectPaint(WHITE_COLOR));
        } else {
            canvas.drawBitmap(posterBitmap,
                    posterLRPosterPadding,
                    posterTopPadding,
                    bitmapPaint);
        }
        //endregion

        //region draw title

        canvas.save();
        canvas.translate(textLRPadding, posterTopPadding + imageHeight + posterTextMargin);
        if (titleStaticLayout != null) titleStaticLayout.draw(canvas);
        canvas.restore();
        //endregion

        //region draw description

        canvas.save();
        canvas.translate(textLRPadding,
                posterTopPadding + imageHeight + posterTextMargin + titleTextHeight + titleDescMargin);
        if (descriptionStaticLayout != null) descriptionStaticLayout.draw(canvas);
        canvas.restore();
        //endregion
    }

    private void initResources() {
        //region draw poster
        textLRPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
        posterLRPosterPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        posterTopPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        imageHeight = getResources().getDimensionPixelOffset(R.dimen.poster_height);
        posterTextMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        titleDescMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (artist == null) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int posterLRTextPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
        int width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);

        int textWidth = width - (2 * posterLRTextPadding);

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

    private float getTextHeight(String text, int width, TextPaint textPaint) {
        return getStaticLayout(text, width, textPaint).getHeight();
    }

    private StaticLayout getStaticLayout(String text, int width, TextPaint textPaint) {
        if (text == null) {
            text = "";
        }
        return new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
    }

    private Paint getRectPaint(int color) {
        rectPaint.setColor(color);
        return rectPaint;
    }


    private String getArtistDescription() {
        if (artist == null) {
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

    private Palette getPalette() {
        if (bitmapPallete != null) {
            return bitmapPallete;
        } else {
            return getDefaultPalette();
        }
    }

    private Palette bitmapPallete;
    private Palette defaultPallete = Palette.from(Collections.singletonList(new Palette.Swatch(WHITE_COLOR, PALETTE_POPULATION)));

    @NonNull
    private Palette getDefaultPalette() {
        return defaultPallete;
    }

    private final class ImageLoadTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageLoadTarget = null;
            setPosterBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageLoadTarget = null;
            setPosterBitmap(null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            setPosterBitmap(null);
        }
    }
}
