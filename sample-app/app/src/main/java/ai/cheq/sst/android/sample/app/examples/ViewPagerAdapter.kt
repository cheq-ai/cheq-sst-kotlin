package ai.cheq.sst.android.sample.app.examples

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragmentSuppliers = mutableListOf<() -> Fragment>()
    private val fragments = mutableListOf<Fragment?>()
    private val titles = mutableListOf<String>()
    private val indexes = mutableMapOf<String, Int>()

    override fun getItemCount(): Int = fragmentSuppliers.size

    override fun createFragment(i: Int) = fragmentSuppliers[i]()

    fun getIndex(title: String) = indexes[title]

    fun getPageTitle(position: Int) = titles[position]

    fun addFragment(title: String, fragmentSupplier: () -> Fragment) {
        titles.add(title)
        fragmentSuppliers.add(fragmentSupplier)
        fragments.add(null)
        indexes[title] = itemCount
    }
}
