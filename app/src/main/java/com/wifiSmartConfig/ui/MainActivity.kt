package com.wifiSmartConfig.ui

import com.example.wifiSmartConfig.databinding.ActivityMainBinding
import com.homeX.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun initializeView() {
        replaceFragment(FirstFragment(),true)
    }


}