package com.example.naturescart.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.adapters.HomeFragmentsVpAdapter
import com.example.naturescart.databinding.ActivityHomeBinding
import com.example.naturescart.fragments.*
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
                    binding.homeFragmentsVp.currentItem = 1
                    it.setIcon(R.drawable.ic_heart_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.cart -> {
                    binding.homeFragmentsVp.currentItem = 2
                    it.setIcon(R.drawable.ic_cart_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.order -> {
                    binding.homeFragmentsVp.currentItem = 3
                    it.setIcon(R.drawable.ic_order_checked)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about -> {
                    binding.homeFragmentsVp.currentItem = 4
                    it.setIcon(R.drawable.ic_about_checked)
                    return@OnNavigationItemSelectedListener true
                }
            }
            return@OnNavigationItemSelectedListener false
        }
        binding.bottomNavigation.setOnNavigationItemSelectedListener(mBottomNavigationListener)
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
        finish()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}