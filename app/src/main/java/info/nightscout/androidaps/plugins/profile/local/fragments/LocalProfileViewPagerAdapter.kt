package info.nightscout.androidaps.plugins.profile.local.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class LocalProfileViewPagerAdapter(val tabArray: Array<Pair<String, Fragment>>, fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = tabArray.size

    override fun createFragment(position: Int) = tabArray[position].second
}
