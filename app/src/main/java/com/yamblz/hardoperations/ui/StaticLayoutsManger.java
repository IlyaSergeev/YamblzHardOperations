package com.yamblz.hardoperations.ui;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Aleksandra on 09/08/16.
 */
public class StaticLayoutsManger {
    private static StaticLayout titleStaticLayout;
    private static StaticLayout descriptionStaticLayout;

    private static StaticLayout getStaticLayout(String text, int width, TextPaint textPaint) {
        if (text == null) {
            text = "";
        }
        return new StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
    }

    public static void initTitleStaticLayout(String text, int width, TextPaint textPaint) {
        titleStaticLayout = getStaticLayout(text, width, textPaint);
    }

    public static void initDescriptionStaticLayout(String text, int width, TextPaint textPaint) {
        descriptionStaticLayout = getStaticLayout(text, width, textPaint);
    }

    public static StaticLayout getTitleStaticLayout() {
        return titleStaticLayout;
    }

    public static StaticLayout getDescriptionStaticLayout() {
        return descriptionStaticLayout;
    }
}
