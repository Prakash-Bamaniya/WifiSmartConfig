package com.wifiSmartConfig.utils

import java.net.InetAddress

data class StateResult(
    var message: CharSequence? = null,
    var permissionGranted: Boolean = false,
    var locationRequirement: Boolean = false,
    var wifiConnected: Boolean = false,
    var is5G: Boolean = false,
    var address: InetAddress? = null,
    var ssid: String? = null,
    var ssidBytes: ByteArray? = null,
    var bssid: String? = null
)