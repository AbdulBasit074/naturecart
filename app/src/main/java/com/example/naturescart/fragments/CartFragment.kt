package com.example.naturescart.fragments

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
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.moveFromFragment
import com.example.naturescart.helper.showToast
import com.example.naturescart.model.CartDetail
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.cart.CartService
import com.example.naturescart.ui.CartOrderDetailActivity
import com.example.naturescart.ui.MenuActivity
import com.google.gson.Gson

class CartFragment : Fragment(), Results {

    private val cartDetailRequest: Int = 212
    private lateinit var cartBinding: FragmentCartBinding
    private var cartDetail: CartDetail = CartDetail()
    private var loggedUser: User? = null

    private var cartID: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        cartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        return cartBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedUser = NatureDb.newInstance(requireActivity()).userDao().getLoggedUser()
        cartID =
            PreferenceManager.getDefaultSharedPreferences(activity).getLong(Constants.cartID, 0)
        if (cartID!!.toInt() == 0)
            showToast("Cart is Empty")
        else
            CartService(cartDetailRequest, this).getCartDetail(cartID)
        setListeners()

    }

    override fun onResume() {
        super.onResume()
        loggedUser = NatureDb.newInstance(requireActivity()).userDao().getLoggedUser()
    }

    private fun setListeners() {
        cartBinding.nextBtn.setOnClickListener {

            if (loggedUser != null)
                moveFromFragment(CartOrderDetailActivity.newInstance(requireActivity(), cartDetail))
            else
                moveFromFragment(requireActivity(), MenuActivity::class.java)
        }
    }

    private fun setAdapter() {

        cartBinding.cartRv.adapter =
            CartItemRvAdapter(
                cartDetail.items as ArrayList<CartDetail.Item>,
                requireActivity()
            ) { refreshData() }
    }

    private fun refreshData() {
        cartID =
            PreferenceManager.getDefaultSharedPreferences(activity).getLong(Constants.cartID, 0)
        CartService(cartDetailRequest, this).getCartDetail(cartID)
    }


    override fun onSuccess(requestCode: Int, data: String) {

        when (requestCode) {
            cartDetailRequest -> {
                cartDetail = Gson().fromJson(data, CartDetail::class.java)
                upDateUi()
                setAdapter()
            }
        }


    }

    private fun upDateUi() {
        cartBinding.totalItem.text =
            getString(R.string.total_cart, cartDetail.summary?.totalItems.toString())
        cartBinding.totalAmount.text =
            getString(R.string.aed_price, cartDetail.summary?.subTotal.toString())
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

}