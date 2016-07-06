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
import com.squareup.picasso.Transformation;
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

    private final GetPaletteTransformation paletteTransformation = new GetPaletteTransformation();
    private final Palette defaultPalette = Palette.from(Collections.singletonList(new Palette.Swatch(
            WHITE_COLOR,
            PALETTE_POPULATION)));

    private TextPaint titlePaint;
    private TextPaint descriptionPaint;
    private Paint posterPaint;

    private int defaultTextColor;
    private int defaultBackgroundColor;

    private Picasso picasso;
    private ImageLoadTarget imageLoadTarget;
    private Bitmap posterBitmap;
    @NonNull
    private Palette palette = defaultPalette;

    private Artist artist;
    private String descriptionText = "";

    private int cardTopPadding;
    private int cardBottomPadding;
    private int posterTextMargin;
    private int titleDescriptionMargin;
    private int posterLRPosterPadding;
    private int posterHeight;
    private int textLRPadding;

    private float titleTextHeight = 0f;

    private StaticLayout titleStaticLayout;
    private StaticLayout descriptionStaticLayout;

    private Paint backgroundPaint;
    private final Paint posterPlaceholderPaint = getRectPaint1(WHITE_COLOR);

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

        backgroundPaint = getRectPaint1(defaultBackgroundColor);
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
        palette = defaultPalette;
        picasso.load(artist.getCover().getBigImageUrl())
               .transform(paletteTransformation)
               .into(imageLoadTarget);
    }

    private void setPosterBitmap(Bitmap bitmap)
    {
        if (posterBitmap != null)
        {
            posterBitmap.recycle();
        }
        if (bitmap != null)
        {
            posterBitmap = BitmapUtils.fitToCenterBitmap(bitmap,
                                                         getWidth() - (2 * posterLRPosterPadding),
                                                         posterHeight);
        }
        else
        {
            posterBitmap = null;
        }
        invalidate();
    }

    private void setPalette(@NonNull Palette palette)
    {
        this.palette = palette;
        backgroundPaint = getRectPaint1(palette.getLightVibrantColor(defaultBackgroundColor));
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
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

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
                            posterPlaceholderPaint);
        }
        else
        {
            canvas.drawBitmap(posterBitmap,
                              posterLRPosterPadding,
                              cardTopPadding,
                              posterPaint);
        }

        //draw title
        canvas.save();
        canvas.translate(textLRPadding, cardTopPadding + posterHeight + posterTextMargin);
        titleStaticLayout.draw(canvas);
        canvas.restore();

        //draw description
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

        titleStaticLayout = getStaticLayout(artist.getName(), textWidth, titlePaint);
        titleTextHeight = titleStaticLayout.getHeight();

        descriptionStaticLayout = getStaticLayout(descriptionText,
                                                  textWidth,
                                                  descriptionPaint);
        float descriptionTextHeight = descriptionStaticLayout.getHeight();

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

    private Paint getRectPaint1(int color)
    {
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(color);
        return rectPaint;
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

    private final class GetPaletteTransformation implements Transformation
    {
        @Override
        public Bitmap transform(Bitmap source)
        {
            setPalette(Palette.from(source).generate());
            return source;
        }

        @Override
        public String key()
        {
            return "ImageTransformation";
        }
    }
}
