package com.homeX.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewbinding.ViewBinding
import com.example.wifiSmartConfig.R

abstract class BaseActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity() {

    lateinit var binding: B

    //abstract fun defineLayout(): View
    abstract fun initializeView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        //setContentView(defineLayout())
        initializeView()

    }


    fun addFragment(currentFragment: Fragment, nextFragment: Fragment, isAnimation: Boolean) {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            hide(currentFragment)
            setReorderingAllowed(true)
            if (isAnimation)
                setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left)
            addToBackStack(currentFragment::class.simpleName)
            add(R.id.mainContainer, nextFragment, nextFragment::class.simpleName)

        }
    }

    fun replaceFragment(nextFragment: Fragment, isAnimation: Boolean) {
        val fragmentManager = supportFragmentManager
        fragmentManager.commit {
            setReorderingAllowed(true)
            if (isAnimation)
                setCustomAnimations(R.anim.slide_in_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
            replace(R.id.mainContainer, nextFragment, nextFragment::class.simpleName)
        }
    }

}