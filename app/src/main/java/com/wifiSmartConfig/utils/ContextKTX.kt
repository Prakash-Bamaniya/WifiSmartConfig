package com.wifiSmartConfig.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.espressif.iot.esptouch2.provision.TouchNetUtil
import com.example.wifiSmartConfig.R
import com.google.android.material.snackbar.Snackbar


fun Fragment.showSnackBar(msg: String) {
    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT)
}

fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
}

fun updateTime(hours: Int, mins: Int): String {
    var hours = hours
    var timeSet = ""
    if (hours > 12) {
        hours -= 12
        timeSet = "PM"
    } else if (hours == 0) {
        hours += 12
        timeSet = "AM"
    } else if (hours == 12) timeSet = "PM" else timeSet = "AM"
    var minutes = ""
    minutes = if (mins < 10) "0$mins" else mins.toString()

    // Append in a StringBuilder
    val aTime = StringBuilder().append(hours).append(':')
        .append(minutes).append(" ").append(timeSet).toString()
    return aTime
}

fun Context.checkLocation(): StateResult {
    val result = StateResult()
    result.locationRequirement = true
    val manager: LocationManager = getSystemService(LocationManager::class.java)
    val enable = LocationManagerCompat.isLocationEnabled(manager)
    if (!enable) {
        result.message = getString(R.string.message_location)
        return result
    }
    result.locationRequirement = false
    return result
}

fun Context.checkWifi(wifiManager: WifiManager?): StateResult {
    val result = StateResult()
    result.wifiConnected = false
    val wifiInfo = wifiManager!!.connectionInfo
    val connected: Boolean = TouchNetUtil.isWifiConnected(wifiManager)
    if (!connected) {
        result.message = getString(R.string.message_wifi_connection)
        return result
    }
    val ssid: String = TouchNetUtil.getSsidString(wifiInfo)
    val ipValue = wifiInfo.ipAddress
    if (ipValue != 0) {
        result.address = TouchNetUtil.getAddress(wifiInfo.ipAddress)
    } else {
        result.address = TouchNetUtil.getIPv4Address()
        if (result.address == null) {
            result.address = TouchNetUtil.getIPv6Address()
        }
    }
    result.wifiConnected = true
    result.message = ""
    result.is5G = TouchNetUtil.is5G(wifiInfo.frequency)
    if (result.is5G) {
        result.message = getString(R.string.message_wifi_frequency)
    }
    result.ssid = ssid
    result.ssidBytes = TouchNetUtil.getRawSsidBytesOrElse(wifiInfo, ssid.toByteArray())
    result.bssid = wifiInfo.bssid

    return result
}

fun Activity.checkLocationPermission(): StateResult {
    val result = StateResult()
    result.permissionGranted = false
    val locationGranted = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    if (!locationGranted) {
        val splits: List<String> = getString(R.string.message_permission).split("\n")
        require(splits.size == 2) { "Invalid String @RES esptouch_message_permission" }
        val ssb = SpannableStringBuilder(splits[0])
        ssb.append('\n')
        val clickMsg = SpannableString(splits[1])
        val clickSpan = ForegroundColorSpan(-0xffdd01)
        clickMsg.setSpan(clickSpan, 0, clickMsg.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        ssb.append(clickMsg)
        result.message = ssb
        return result
    }
    result.permissionGranted = true
    return result
}