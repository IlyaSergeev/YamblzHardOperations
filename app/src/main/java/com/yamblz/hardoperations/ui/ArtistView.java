package com.yamblz.hardoperations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
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
    private Bitmap posterBitmap;

    private ImageLoadTarget imageLoadTarget;
    private Picasso picasso;

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

        if (imageLoadTarget != null)
        {
            Picasso.with(getContext()).cancelRequest(imageLoadTarget);
            imageLoadTarget = null;
        }
        imageLoadTarget = new ImageLoadTarget();
        picasso.load(artist.getCover().getBigImageUrl()).into(imageLoadTarget);
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
        if (posterBitmap == null)
        {
            canvas.drawRect(posterLRPosterPadding,
                            posterTopPadding,
                            getWidth() - posterLRPosterPadding,
                            imageHeight,
                            getRectPaint());
        }
        else
        {
            Bitmap scaledBitmap = scaleBitmap(posterBitmap,
                                              getWidth() - (2 * posterLRPosterPadding),
                                              imageHeight);
            canvas.drawBitmap(scaledBitmap,
                              posterLRPosterPadding,
                              posterTopPadding,
                              getBitmapPaint());
            scaledBitmap.recycle();
        }

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

    private Paint getBitmapPaint()
    {
        Paint bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);
        return bitmapPaint;
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

    private static Bitmap scaleBitmap(@NonNull Bitmap image, int width, int height)
    {
        Bitmap background = Bitmap.createBitmap(width, height, image.getConfig());
        float originalWidth = image.getWidth();
        float originalHeight = image.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = width / originalWidth;
        float xTranslation = 0.0f, yTranslation = (height - originalHeight * scale) / 2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(image, transformation, paint);
        return background;
    }

    private final class ImageLoadTarget implements Target
    {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
        {
            imageLoadTarget = null;
            updateBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable)
        {
            imageLoadTarget = null;
            updateBitmap(null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable)
        {
            updateBitmap(null);
        }

        private void updateBitmap(Bitmap bitmap)
        {
            posterBitmap = bitmap;
            invalidate();
        }
    }
}
