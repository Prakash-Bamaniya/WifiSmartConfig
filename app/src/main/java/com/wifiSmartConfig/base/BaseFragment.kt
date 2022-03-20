package com.wifiSmartConfig.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.homeX.ui.base.BaseActivity

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<binding : ViewBinding>(private val inflate: Inflate<binding>) : Fragment(), View.OnClickListener {
    abstract fun initializeView()
    abstract fun setListener()

    private var _binding: binding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        setListener()
    }

    fun addFragment(currentFragment: Fragment, nextFragment: Fragment, isAnimation: Boolean) {

        activity?.let {
            (it as BaseActivity<*>).addFragment(currentFragment, nextFragment, isAnimation)
        }
    }


    fun replaceFragment(nextFragment: Fragment, isAnimation: Boolean) {
        activity?.let {
            (it as BaseActivity<*>).replaceFragment(nextFragment, isAnimation)
        }
    }

    fun onBackInclusive() {
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    fun onBack() {
        parentFragmentManager.popBackStack()
    }


}