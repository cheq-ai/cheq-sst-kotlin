package ai.cheq.sst.android.sample.app.examples

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


abstract class TabbedFragment(
    @LayoutRes contentLayoutId: Int,
    @IdRes private val pagerId: Int,
    @IdRes private val tabLayoutId: Int,
    vararg tabs: Tab
) : Fragment(contentLayoutId) {
    private lateinit var pager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var tabLayout: TabLayout
    private val tabs = tabs.toList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager = view.findViewById(pagerId)
        adapter = ViewPagerAdapter(getChildFragmentManager(), lifecycle)

        tabLayout = view.findViewById(tabLayoutId)

        tabs.forEach { tab ->
            adapter.addFragment(getString(tab.titleResource), tab.fragment)
        }

        pager.adapter = adapter

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        tabs.lastOrNull { it.primary }?.let {
            changeToTab(getString(it.titleResource))
        }
    }

    fun changeToTab(title: String) {
        pager.currentItem = adapter.getIndex(title)!!
    }

    data class Tab(
        @StringRes val titleResource: Int,
        val primary: Boolean = false,
        val fragment: () -> Fragment
    )
}