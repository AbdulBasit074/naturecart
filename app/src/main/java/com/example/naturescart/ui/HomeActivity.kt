package com.example.naturescart.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.adapters.HomeFragmentsVpAdapter
import com.example.naturescart.databinding.ActivityHomeBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.CollectionModel
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.services.product.ProductService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeActivity : AppCompatActivity(), Results {

    private val cartDetailRc = 8320
    private val getFavoritesRc = 2398
    private lateinit var binding: ActivityHomeBinding
    private var previous: MenuItem? = null
    private val deviceAddRequest: Int = 1293
    var loggedUser: User? = null
    private lateinit var fragmentsPagerAdapter: HomeFragmentsVpAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        AuthService(deviceAddRequest, this).addDevice(Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey, "").toString(), true)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        fragmentsPagerAdapter = HomeFragmentsVpAdapter(supportFragmentManager)
        binding.homeFragmentsVp.offscreenPageLimit = 4
        binding.homeFragmentsVp.adapter = fragmentsPagerAdapter
        previous = binding.bottomNavigation.menu.findItem(binding.bottomNavigation.selectedItemId)
        binding.bottomNavigation.itemIconTintList = null
        bottomNavigationFragments()
        val cartID = PreferenceManager.getDefaultSharedPreferences(this).getLong(Constants.cartID, 0)
        CartService(cartDetailRc, this).getCartDetail(cartID)
        if (loggedUser != null)
            ProductService(getFavoritesRc, this).getFavorites(loggedUser!!.accessToken)
        checkAndFetchFcmToken()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdated(event: CartUpdateEvent) {
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.cart)
        badge.backgroundColor = getColor(R.color.red)
        badge.badgeTextColor = getColor(R.color.white)
        badge.number = event.itemCount
        badge.isVisible = event.itemCount > 0
        badge.verticalOffset = resources.getDimension(R.dimen._10sdp).toInt()
        badge.horizontalOffset = resources.getDimension(R.dimen._10sdp).toInt()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLoggedIn(event: LogInEvent) {
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        ProductService(getFavoritesRc, this).getFavorites(loggedUser!!.accessToken)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectivityEvent(event: ConnectivityEvent) {
        if (!event.connected)
            startActivity(Intent(this, NoInternetActivity::class.java))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onClickItemCart(event: ClickCartItemEvent) {
        removeAndHide()
        binding.bottomNavigation.selectedItemId = R.id.cart
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeHomeFragment(event: MoveFragmentEvent) {
        loadFragment(event.fragment)
        binding.homeFragmentsVp.visibility = View.GONE
        binding.homePageFragment.visibility = View.VISIBLE

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun moveToAbout(event: MoveToAboutEvent) {
        binding.bottomNavigation.selectedItemId = R.id.about
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == Constants.categoryDetailsActivityRc
                    || requestCode == Constants.collectionDetailsActivityRc
                    || requestCode == Constants.searchActivityRc)
            && resultCode == RESULT_OK
        ) {
            binding.bottomNavigation.selectedItemId = R.id.cart
        }
    }

    private fun bottomNavigationFragments() {
        val mBottomNavigationListener = BottomNavigationView.OnNavigationItemSelectedListener {
            setPreviousUiUpdate()
            previous = it

            when (it.itemId) {
                R.id.homeNav -> {
                    binding.homeFragmentsVp.currentItem = 0
                    it.setIcon(R.drawable.ic_home_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.favourite -> {
                    removeAndHide()
                    binding.homeFragmentsVp.currentItem = 1
                    it.setIcon(R.drawable.ic_heart_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cart -> {
                    removeAndHide()
                    binding.homeFragmentsVp.currentItem = 2
                    it.setIcon(R.drawable.ic_cart_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.order -> {
                    removeAndHide()
                    binding.homeFragmentsVp.currentItem = 3
                    it.setIcon(R.drawable.ic_order_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about -> {
                    removeAndHide()
                    binding.homeFragmentsVp.currentItem = 4
                    it.setIcon(R.drawable.ic_about_checked)
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener(mBottomNavigationListener)
    }

    private fun removeAndHide() {
        binding.homeFragmentsVp.visibility = View.VISIBLE
        binding.homePageFragment.visibility = View.GONE
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun setPreviousUiUpdate() {
        if (previous == null)
            return
        when (previous!!.itemId) {
            R.id.homeNav -> {
                previous!!.setIcon(R.drawable.ic_home)
            }
            R.id.favourite -> {

                previous!!.setIcon(R.drawable.ic_heart_white)
            }
            R.id.cart -> {

                previous!!.setIcon(R.drawable.ic_cart_white)
            }
            R.id.order -> {
                previous!!.setIcon(R.drawable.ic_order_white)
            }
            R.id.about -> {
                previous!!.setIcon(R.drawable.ic_about)
            }
        }

    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            cartDetailRc -> {
                Persister.with(this).persist(Constants.cartPersistenceKey, data)
                val cartDetail = Gson().fromJson(data, CartDetail::class.java)
                if (!cartDetail.items.isNullOrEmpty())
                    onCartUpdated(CartUpdateEvent(cartDetail.items?.size ?: 0))
            }
            getFavoritesRc -> {
                val favoritesDao = NatureDb.getInstance(this).favouriteDao()
                val favorites: ArrayList<CollectionModel> = Gson().fromJson(data, object : TypeToken<ArrayList<CollectionModel>>() {}.type)
                favorites.forEach {
                    it.products.forEach { product ->
                        favoritesDao.insertProduct(product)
                    }
                }
                EventBus.getDefault().postSticky(FavoritesUpdatedEvent())
            }
        }
    }

    override fun onFailure(requestCode: Int, data: String) {
    }

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount == 1 -> {
                binding.homeFragmentsVp.visibility = View.VISIBLE
                binding.homePageFragment.visibility = View.GONE
                supportFragmentManager.popBackStack()
            }
            supportFragmentManager.backStackEntryCount > 1 -> supportFragmentManager.popBackStack()
            else -> finish()
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.homePageFragment.id, fragment)
        fragmentTransition.addToBackStack(null)
        fragmentTransition.commit()
    }


}