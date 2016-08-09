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
    private Palette palette;
    private Resources resources;
    private Paint whitePaint;
    private Paint bitmapPaint;

    int posterTextMargin;
    int textLRPadding;
    int topPadding;
    int imageHeight;
    int titleDescMargin;
    float titleTextHeight;
    int bottomPadding;
    int textMargin;

    StaticLayout titleStaticLayout;

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

    private void init(@NonNull Context context) {
        picasso = Picasso.with(context);

        whitePaint = getRectPaint(WHITE_COLOR);

        resources = getResources();
        //noinspection deprecation
        defaultTextColor = resources.getColor(R.color.default_text_color);
        //noinspection deprecation
        defaultBackgroundColor = resources.getColor(R.color.default_background_color);

        float titleFontSize = resources.getDimensionPixelSize(R.dimen.artist_card_title_font_size);
        titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextSize(titleFontSize);
        titlePaint.setColor(defaultTextColor);


        bitmapPaint = getBitmapPaint();
        float descriptionFontSize = resources.getDimensionPixelSize(R.dimen.artist_card_font_size);
        descriptionPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        descriptionPaint.setTextSize(descriptionFontSize);
        descriptionPaint.setColor(defaultTextColor);

        posterTextMargin = resources.getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        textLRPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);

        topPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        imageHeight = resources.getDimensionPixelOffset(R.dimen.poster_height);
        titleDescMargin = resources.getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

        bottomPadding = resources.getDimensionPixelOffset(R.dimen.artist_card_bottom_padding);
        textMargin = resources.getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
    }

    public void setArtist(Artist artist) {
        this.artist = artist;

        String artistName = artist.getName();

        titleTextHeight = getTextHeight(artistName, getWidth(), titlePaint);
        if (imageLoadTarget != null) {
            Picasso.with(getContext()).cancelRequest(imageLoadTarget);
            imageLoadTarget = null;
        }
        imageLoadTarget = new ImageLoadTarget();
        picasso.load(artist.getCover().getBigImageUrl()).into(imageLoadTarget);


        invalidate();
        requestLayout();
    }

    private void setPosterBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            posterBitmap = BitmapUtils.fitToCenterBitmap(bitmap,
                    getWidth() - (2 * topPadding),
                    imageHeight);

        } else {
            posterBitmap = null;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (artist == null) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        String artistName = artist.getName();
        //Draw background
        palette = getPalette();
        int textColor = palette.getDarkMutedColor(defaultTextColor);
        titlePaint.setColor(palette.getDarkMutedColor(textColor));
        descriptionPaint.setColor(textColor);
        canvas.drawRect(0, 0, width, height, getRectPaint(palette.getLightVibrantColor(
                defaultBackgroundColor)));
        //draw poster


        if (posterBitmap == null) {
            canvas.drawRect(topPadding,
                    topPadding,
                    width - topPadding,
                    imageHeight,
                    whitePaint);
        } else {
            canvas.drawBitmap(posterBitmap,
                    topPadding,
                    topPadding,
                    bitmapPaint);
        }

        //draw title

        titleStaticLayout = getStaticLayout(artist.getName(),
                width - textLRPadding,
                titlePaint);

        canvas.save();
        canvas.translate(textLRPadding, topPadding + imageHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        //draw description

        StaticLayout descriptionStaticLayout = getStaticLayout(getArtistDescription(),
                width - textLRPadding,
                descriptionPaint);


        canvas.save();
        canvas.translate(textLRPadding,
                topPadding + imageHeight + posterTextMargin + titleTextHeight + titleDescMargin);
        descriptionStaticLayout.draw(canvas);
        canvas.restore();
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

        height += topPadding;

        height += bottomPadding;
        height += textMargin;
        height += titleDescMargin;

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
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(color);
        return rectPaint;
    }

    private Paint getBitmapPaint() {
        Paint bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);
        return bitmapPaint;
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
        if (posterBitmap != null && !posterBitmap.isRecycled()) {
            return Palette.from(posterBitmap).generate();
        } else {
            return getDefaultPalette();
        }
    }

    @NonNull
    private static Palette getDefaultPalette() {
        Palette.Swatch swatch = new Palette.Swatch(WHITE_COLOR, PALETTE_POPULATION);
        return Palette.from(Collections.singletonList(swatch));
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
