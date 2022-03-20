package com.wifiSmartConfig.ui

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.wifiSmartConfig.R
import com.example.wifiSmartConfig.databinding.ActivitySplashBinding
import com.homeX.ui.base.BaseActivity
import com.wifiSmartConfig.utils.CustomTypefaceSpan


class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val splashScreenTimeOut = 2000L
    override fun initializeView() {
        setSpannable()
        //
        openMainScreen()
    }

    private fun openMainScreen() {
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        binding.ivLogo.startAnimation(slideAnimation)


        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val intent = Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }, splashScreenTimeOut)
    }

    private fun setSpannable() {
        val firstStr = "Smart"
        val lastStr = "Config"
        val spannable: Spannable = SpannableString(firstStr + lastStr)
        val startIndex = 0
        val endIndex = firstStr.length
        val firstTypeFace = ResourcesCompat.getFont(this, R.font.be_vietnam_regular)
        spannable.setSpan(CustomTypefaceSpan(firstTypeFace), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val secondTypeFace = ResourcesCompat.getFont(this, R.font.be_vietnam_extra_bold)
        spannable.setSpan(CustomTypefaceSpan(secondTypeFace), firstStr.length, endIndex + lastStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //your code at here.
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)

            }
        }
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorDarkYellow)),
            firstStr.length, endIndex + lastStr.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        /*spannable.setSpan(
            RelativeSizeSpan(1f), firstStr.length, endIndex + lastStr.length,  Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        */    //spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvAppName.text = spannable
        binding.tvAppName.movementMethod = LinkMovementMethod.getInstance()
        binding.tvAppName.highlightColor = Color.TRANSPARENT
        binding.tvAppName.isEnabled = true
    }
}