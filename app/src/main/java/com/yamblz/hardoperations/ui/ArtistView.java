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


/**
 * Created by i-sergeev on 06.07.16
 */
public class ArtistView extends View {
    private static final int WHITE_COLOR = 0xFFFFFF;

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;
    private int defaultTextColor;
    private int defaultBackgroundColor;

    private Artist artist;
    private Bitmap posterBitmap;
    private ImageLoadTarget imageLoadTarget;
    private Picasso picasso;

    int posterLRPosterPadding;
    int posterTopPadding;
    int imageHeight;

    private Paint rectPaint;
    private Paint bitmapPaint;
    int posterTextMargin;
    int textLRPadding;
    int titleDescMargin;

    private StaticLayout titleStaticLayout;
    private StaticLayout descriptionStaticLayout;


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


        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);

        posterTextMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        textLRPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_lr_text_padding);
        titleDescMargin = getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);
        posterLRPosterPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        posterTopPadding = getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        imageHeight = getResources().getDimensionPixelOffset(R.dimen.poster_height);
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        invalidate();
        requestLayout();

        if (imageLoadTarget != null) {
            Picasso.with(getContext()).cancelRequest(imageLoadTarget);
            imageLoadTarget = null;
        }
        imageLoadTarget = new ImageLoadTarget();
        picasso.load(artist.getCover().getBigImageUrl()).into(imageLoadTarget);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (artist == null) {
            return;
        }
        if (posterBitmap == null) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), rectPaint);
        } else {
            int height = getHeight();
            int width = getWidth();
            int left = width - posterLRPosterPadding;
            canvas.drawRect(0, 0, posterLRPosterPadding, height, rectPaint);
            canvas.drawRect(left, 0, width, height, rectPaint);
            canvas.drawRect(posterLRPosterPadding, 0, left, posterTopPadding, rectPaint);
            canvas.drawRect(posterLRPosterPadding, posterTopPadding + imageHeight, left, height, rectPaint);
            canvas.drawBitmap(posterBitmap, posterLRPosterPadding, posterTopPadding, bitmapPaint);
        }
        canvas.save();
        canvas.translate(textLRPadding, posterTopPadding + imageHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.translate(0, titleStaticLayout.getHeight() + titleDescMargin);
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

        int width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);
        titleStaticLayout = getStaticLayout(artist.getName(), width - textLRPadding, titlePaint);
        descriptionStaticLayout = getStaticLayout(getArtistDescription(), width - textLRPadding, descriptionPaint);

        int height = 0;
        height += getResources().getDimensionPixelOffset(R.dimen.poster_height);
        height += titleStaticLayout.getHeight();
        height += descriptionStaticLayout.getHeight();
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_top_padding);
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_bottom_padding);
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_poster_text_margin);
        height += getResources().getDimensionPixelOffset(R.dimen.artist_card_title_desc_margin);

        setMeasuredDimension(width, height);
    }

    private StaticLayout getStaticLayout(StringBuilder text, int width, TextPaint textPaint) {
        if (text == null) {
            text = new StringBuilder();
        }
        return new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
    }

    private StaticLayout getStaticLayout(String text, int width, TextPaint textPaint) {
        if (text == null) {
            text = "";
        }
        return new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
    }


    private StringBuilder getArtistDescription() {
        StringBuilder descriptionText = new StringBuilder();
        if (artist == null) {
            return descriptionText;
        }
        descriptionText.append(artist.getDescription());
        descriptionText.append("\n");
        descriptionText.append("\n");
        descriptionText.append(getResources().getQuantityString(R.plurals.artistAlbums,
                artist.getAlbumsCount(),
                artist.getAlbumsCount()));
        descriptionText.append("\n");
        descriptionText.append(getResources().getQuantityString(R.plurals.artistTracks,
                artist.getTracksCount(),
                artist.getTracksCount()));
        return descriptionText;
    }


    private final class ImageLoadTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageLoadTarget = null;
            Palette palette = Palette.from(bitmap).generate();
            rectPaint.setColor(palette.getLightVibrantColor(defaultBackgroundColor));
            posterBitmap = BitmapUtils.fitToCenterBitmap(bitmap, getWidth() - (2 * posterLRPosterPadding), imageHeight);
            int textColor = palette.getDarkMutedColor(defaultTextColor);
            titlePaint.setColor(palette.getDarkMutedColor(textColor));
            descriptionPaint.setColor(textColor);

            posterBitmap = BitmapUtils.fitToCenterBitmap(posterBitmap,
                    getWidth() - (2 * posterLRPosterPadding),
                    imageHeight);
            invalidate();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageLoadTarget = null;
            rectPaint.setColor(WHITE_COLOR);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            posterBitmap = null;
        }
    }
}