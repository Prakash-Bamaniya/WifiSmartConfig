package com.wifiSmartConfig.utils

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.text.style.TypefaceSpan
class CustomTypefaceSpan(private val typeface: Typeface?) : MetricAffectingSpan() {
    override fun updateDrawState(paint: TextPaint) {
        paint.typeface=typeface
    }

    override fun updateMeasureState(paint: TextPaint) {
        paint.typeface=typeface
    }
}