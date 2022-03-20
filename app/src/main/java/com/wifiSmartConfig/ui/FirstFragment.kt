package com.wifiSmartConfig.ui

import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.espressif.iot.esptouch.EsptouchTask
import com.espressif.iot.esptouch.IEsptouchListener
import com.espressif.iot.esptouch.IEsptouchResult
import com.espressif.iot.esptouch.IEsptouchTask
import com.espressif.iot.esptouch.util.ByteUtil
import com.wifiSmartConfig.MyApp
import com.example.wifiSmartConfig.R
import com.example.wifiSmartConfig.databinding.FragmentFirstBinding

import com.wifiSmartConfig.base.BaseFragment
import com.wifiSmartConfig.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment<FragmentFirstBinding>(FragmentFirstBinding::inflate) {
    private var mWifiManager: WifiManager? = null

    private var mSsid: String? = null
    private var mSsidBytes: ByteArray? = null
    private var mBssid: String? = null
    override fun initializeView() {
        mWifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        askPermission()
        observeData()
    }

    private fun askPermission() {
        permission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        GpsUtils(requireContext()).turnGPSOn { isGPSEnable -> }

    }

    private fun observeData() {
        MyApp.instance.observeBroadcast(this) { broadcast ->

            onWifiChanged()
        }
    }

    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

    }

    private fun executeEsptouch() {
        val ssid = if (mSsidBytes == null) ByteUtil.getBytesByString(mSsid) else mSsidBytes!!
        val pwdStr: String = binding.apPasswordEdit.text.toString()
        val password = if (pwdStr == null) null else ByteUtil.getBytesByString(pwdStr)
        val bssid = com.espressif.iot.esptouch.util.TouchNetUtil.parseBssid2bytes(mBssid)
        val devCountStr: String = binding.deviceCountEdit.text.toString()
        val deviceCount = devCountStr?.toString()?.toByteArray() ?: ByteArray(0)
        val broadcast = byteArrayOf((if (binding.packageModeGroup.checkedRadioButtonId === R.id.packageBroadcast) 1 else 0).toByte())
        SendWifiInfo(ssid, bssid = bssid, password = password, deviceCount, broadcast).execute()
    }

    override fun setListener() {
        binding.confirmBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.confirmBtn -> {
                executeEsptouch()
            }
        }
    }


    private fun checkAllPermissions(): StateResult {
        var result = requireActivity().checkLocationPermission()
        if (!result.permissionGranted) {
            return result
        }
        result = requireContext().checkLocation()
        result.permissionGranted = true
        if (result.locationRequirement) {
            return result
        }
        result = requireContext().checkWifi(mWifiManager)
        result.permissionGranted = true
        result.locationRequirement = false
        return result
    }

    private fun onWifiChanged() {
        val stateResult: StateResult = checkAllPermissions()

        mSsid = stateResult.ssid
        mSsidBytes = stateResult.ssidBytes
        mBssid = stateResult.bssid
        var message = stateResult.message ?: ""
        var confirmEnable = false
        if (stateResult.wifiConnected) {
            confirmEnable = true
            if (stateResult.is5G) {
                message = getString(R.string.wifi_5g_message)
            }
        }
        binding.apSsidText.text = mSsid ?: ""
        binding.apBssidText.text = mBssid ?: ""
        binding.messageView.text = message ?: ""
        binding.confirmBtn.isEnabled = confirmEnable
    }


    inner class SendWifiInfo(val ssid: ByteArray?, val bssid: ByteArray?, val password: ByteArray?, val deviceCount: ByteArray?, val broadcast: ByteArray?) : IEsptouchListener {

        private var mResultDialog: AlertDialog? = null
        private var mEsptouchTask: IEsptouchTask? = null
        private val mLock = Object()

        fun execute() = lifecycleScope.launch {
            onPreExecute()
            val result = doInBackground() // runs in background thread without blocking the Main Thread
            onPostExecute(result)
        }

        private suspend fun doInBackground(): List<IEsptouchResult>? = withContext(Dispatchers.IO) { // to run code in Background Thread
            // do async work
            // delay(1000) // simulate async work

            //synchronized(mLock) {
            val apSsid: ByteArray = ssid!!
            val apBssid: ByteArray = bssid!!
            val apPassword: ByteArray = password!!
            val deviceCountData: ByteArray = deviceCount!!
            val broadcastData: ByteArray = broadcast!!
            val taskResultCount = if (deviceCountData.isEmpty()) -1 else String(deviceCountData).toInt()
            val context = activity!!.applicationContext
            mEsptouchTask = EsptouchTask(apSsid, apBssid, apPassword, context)
            mEsptouchTask?.setPackageBroadcast(broadcastData[0].equals(1))
            mEsptouchTask?.setEsptouchListener(this@SendWifiInfo)
            // }
            return@withContext mEsptouchTask?.executeForResults(taskResultCount)
        }

        private fun onPreExecute() {
            //show progress
            binding.progressView.isVisible = true
        }

        // Runs on the Main(UI) Thread
        private fun onPostExecute(result: List<IEsptouchResult>?) {
            Log.d("SmartConfigData", "onPostExecute: $result")
            if (result == null) {
                mResultDialog = AlertDialog.Builder(activity!!)
                    .setMessage(R.string.configure_result_failed_port)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                mResultDialog?.setCanceledOnTouchOutside(false)
                return
            }
            binding.progressView.isVisible = false
            // check whether the task is cancelled and no results received

            // check whether the task is cancelled and no results received
            val firstResult = result[0]
            if (firstResult.isCancelled) {
                return
            }
            // the task received some results including cancelled while
            // executing before receiving enough results

            // the task received some results including cancelled while
            // executing before receiving enough results
            if (!firstResult.isSuc) {
                mResultDialog = AlertDialog.Builder(activity!!)
                    .setMessage(R.string.configure_result_failed)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                mResultDialog?.setCanceledOnTouchOutside(false)
                return
            }

            val resultMsgList = ArrayList<CharSequence>(result.size)
            for (touchResult in result) {
                val message = activity!!.getString(
                    R.string.configure_result_success_item,
                    touchResult.bssid, touchResult.inetAddress.hostAddress
                )
                resultMsgList.add(message)
            }
            val items = arrayOfNulls<CharSequence>(resultMsgList.size)
            mResultDialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.configure_result_success)
                .setItems(resultMsgList.toArray(items), null)
                .setPositiveButton(android.R.string.ok, null)
                .show()
            mResultDialog?.setCanceledOnTouchOutside(false)

        }

        override fun onEsptouchResultAdded(result: IEsptouchResult?) {
            Log.d("SmartConfigData", "onPostExecute: $result")
            if (activity != null) {

                Log.i("SmartConfigData", "onEsptouchResultAdded: $result")
                val text = result?.bssid + " is connected to the wifi"
                binding.testResult.append(
                    String.format(
                        Locale.ENGLISH,
                        "%s,%s\n",
                        result?.inetAddress?.hostAddress,
                        result?.bssid
                    )
                )
            }
        }
    }

}