package com.jxxt.sues.ui

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jxxt.sues.ui.classtable.ClassTableFragment
import com.jxxt.sues.ui.settings.SettingsFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = mutableListOf<Fragment>()

    init {
        fragments += ClassTableFragment()
        fragments += SettingsFragment()
    }

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
    }
}