package com.example.naturescart.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.naturescart.R
import com.example.naturescart.adapters.CartItemRvAdapter
import com.example.naturescart.databinding.FragmentCartBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.ui.CartOrderDetailActivity
import com.example.naturescart.ui.MenuActivity
import com.example.naturescart.ui.NotificationActivity
import com.example.naturescart.ui.UserDetailActivity
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartFragment : Fragment(), Results {

    private val cartDataPersistenceKey = "cartDataPersistenceKey"
    private val cartDetailRequest: Int = 212
    private val loginRequest: Int = 2112
    private lateinit var cartBinding: FragmentCartBinding
    private var cartDetail: CartDetail = CartDetail()
    private var loggedUser: User? = null
    private var cartID: Long? = null
    private var loadingView: LoadingDialog? = null
    private var isFirstCall = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        cartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        return cartBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingView = LoadingDialog(requireContext())
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
        cartID = PreferenceManager.getDefaultSharedPreferences(activity).getLong(Constants.cartID, 0)
        if (cartID!!.toInt() == 0) {
            cartBinding.emptyCartContainer.visibility = View.VISIBLE
            cartBinding.checkoutContainer.visibility = View.GONE
        } else {
            val cartData = Persister.with(requireContext()).getPersisted(cartDataPersistenceKey, null)
            if (cartData == null)
                loadingView?.show()
            else
                onSuccess(cartDetailRequest, cartData)
            CartService(cartDetailRequest, this).getCartDetail(cartID)
        }
        setListeners()
    }


    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
    }

    private fun setListeners() {
        cartBinding.toolBar.appLogo.setOnClickListener {
            EventBus.getDefault().postSticky(LogoClickEvent())
        }
        cartBinding.toolBar.notificationBtn.setOnClickListener {
            moveFromFragment(requireActivity(), NotificationActivity::class.java)
        }
        cartBinding.toolBar.profileBtn.setOnClickListener {
            if (loggedUser == null)
                moveForResultFragment(requireActivity(), MenuActivity::class.java, loginRequest)
            else
                moveFromFragment(requireActivity(), UserDetailActivity::class.java)
        }
        cartBinding.nextBtn.setOnClickListener {
            if (loggedUser != null) {
                moveFromFragment(requireActivity(), CartOrderDetailActivity::class.java)
            } else
                moveForResultFragment(requireActivity(), MenuActivity::class.java, loginRequest)
        }
    }

    private fun setAdapter() {
        cartBinding.cartRv.adapter = CartItemRvAdapter(cartDetail.items as ArrayList<CartDetail.Item>, requireActivity()) { refreshData() }
    }

    private fun checkForEmptiness() {
        if (cartDetail.items.isNullOrEmpty()) {
            cartBinding.emptyCartContainer.visibility = View.VISIBLE
            cartBinding.checkoutContainer.visibility = View.GONE
        } else {
            cartBinding.emptyCartContainer.visibility = View.GONE
            cartBinding.checkoutContainer.visibility = View.VISIBLE
        }
    }

    private fun refreshData() {
        cartID = PreferenceManager.getDefaultSharedPreferences(activity).getLong(Constants.cartID, 0)
        if(cartID!! > 0)
        CartService(cartDetailRequest, this).getCartDetail(cartID)
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            cartDetailRequest -> {
                Persister.with(requireContext()).persist(Constants.cartPersistenceKey, data)
                cartDetail = Gson().fromJson(data, CartDetail::class.java)
                upDateUi()
                setAdapter()
                checkForEmptiness()
                if (isFirstCall) {
                    EventBus.getDefault().postSticky(CartUpdateEvent(cartDetail.items?.size ?: 0))
                    isFirstCall = false
                }
                loadingView?.dismiss()
                Persister.with(requireContext()).persist(cartDataPersistenceKey, data)
            }
        }
    }

    private fun upDateUi() {


        cartBinding.totalItem.text = getString(R.string.total_cart, cartDetail.summary?.totalItems.toString())
        if (cartDetail.summary?.subTotal!! < 50) {
            cartBinding.freeDeliveryNotesPrice.text = String.format("%.2f", (50 - cartDetail.summary?.subTotal!!))
            cartBinding.freeDeliveryNotesPrice.visibility = View.VISIBLE
            cartBinding.freeDeliveryNotesAdd.visibility = View.VISIBLE
            cartBinding.freeDeliveryNotesdelivery.visibility = View.VISIBLE
        } else {
            cartBinding.freeDeliveryNotesPrice.visibility = View.GONE
            cartBinding.freeDeliveryNotesAdd.visibility = View.GONE
            cartBinding.freeDeliveryNotesdelivery.visibility = View.GONE
        }
        cartBinding.totalAmount.text = getString(R.string.aed_price, String.format("%.2f", cartDetail.summary?.subTotal))
    }

    override fun onFailure(requestCode: Int, data: String) {
        loadingView?.dismiss()
        showToast(data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                loginRequest -> {
                    loggedUser = NatureDb.getInstance(requireActivity()).userDao().getLoggedUser()
                }

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartUpdated(event: CartUpdateEvent) {
        refreshData()
    }

}