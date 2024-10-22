package ai.cheq.sst.android.sample.app.examples

import ai.cheq.sst.android.app.R
import ai.cheq.sst.android.app.databinding.ActivityExampleBinding
import ai.cheq.sst.android.sample.app.Constants
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ExampleActivity : AppCompatActivity(), Titled {
    private lateinit var binding: ActivityExampleBinding
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)
        adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.examplePager.adapter = adapter

        val bundle = intent.extras ?: Bundle()
        bundle.getParcelableArrayList(Constants.EXAMPLES, ExampleDetails::class.java)
            ?.let { examples ->
                initToolbar()
                initNavDrawer(examples)
            }

        onBackPressedDispatcher.addCallback {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START); true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setTitle(@StringRes titleResource: Int) {
        val actionbar = supportActionBar
        actionbar?.title = getString(titleResource)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_nav_drawer_menu_24dp)
    }

    private fun initNavDrawer(examples: List<ExampleDetails>?) {
        val windowManager =
            this.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = windowManager.currentWindowMetrics.bounds.width()
        val dim320dp = resources.getDimensionPixelSize(R.dimen.dim320dp)
        if (width > dim320dp) {
            navigationView.layoutParams.width = dim320dp
        } else {
            navigationView.layoutParams.width = width
        }

        examples?.forEachIndexed { index, element ->
            val menuItem = navigationView.menu.add(
                R.id.nav_drawer_examples_group, index, 0, element.nameResource
            )
            if (element.iconResource != null) {
                menuItem.setIcon(element.iconResource)
            }
            adapter.addFragment(getString(element.nameResource)) {
                element.clazz.getDeclaredConstructor().newInstance()
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()
            when {
                examples != null && menuItem.itemId >= 0 && menuItem.itemId < examples.size -> binding.examplePager.currentItem =
                    menuItem.itemId

                else -> return@setNavigationItemSelectedListener false
            }
            return@setNavigationItemSelectedListener true
        }
    }
}