package com.example.naturescart.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityEditProfileBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import java.util.*

class EditProfileActivity : AppCompatActivity(), Results {

    private lateinit var binding: ActivityEditProfileBinding
    private var loggedUser: User? = null
    private val countries = ArrayList<String>()
    private val updateProfile: Int = 2661
    private var selectCountry: String = ""
    private var selectGender: String = ""
    private var areaSearchList: ArrayList<AreaSearchAble> = ArrayList()
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var dialog: DialogCustom


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        binding.user = loggedUser
        setListeners()
        getCountryFromLocale()
        setOtherUserParams()

    }

    private fun setOtherUserParams() {
        binding.phoneNo.setText(loggedUser?.phone?.removePrefix("+971"))
        selectCountry = loggedUser!!.nationality
        selectGender = loggedUser!!.gender
        if (selectCountry.isNotEmpty()) {
            binding.countrySelectionSpinner.text = selectCountry
        }
        if (selectGender.isNotEmpty()) {
            if (selectGender == "Male" || (selectGender == "ذكر"))
                binding.male.performClick()
            else if (selectGender == "Female" || (selectGender == "أنثى"))
                binding.female.performClick()
            else
                binding.ratherGender.performClick()
        }
    }

    private fun setListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.male.setOnClickListener {
            resetGenderView()
            binding.male.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.male.background = ContextCompat.getDrawable(this, R.drawable.border_black)
            selectGender = binding.male.text.toString()
        }
        binding.female.setOnClickListener {
            resetGenderView()
            binding.female.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.female.background = ContextCompat.getDrawable(this, R.drawable.border_black)
            selectGender = binding.female.text.toString()
        }
        binding.ratherGender.setOnClickListener {
            resetGenderView()
            binding.ratherGender.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.ratherGender.background = ContextCompat.getDrawable(this, R.drawable.border_black)
            selectGender = binding.ratherGender.text.toString()
        }
        binding.changePasswordBtn.setOnClickListener {
            DialogCustomPasswordChange(this, loggedUser!!.accessToken) { data -> onPasswordChange(data) }.show()
        }
        binding.countrySelectionSpinner.setOnClickListener {

            var custom = SimpleSearchDialogCompat(this, Constants.getTranslate(this, "select_country"), Constants.getTranslate(this, "search"), null, areaSearchList) { dialog, item, position ->
                binding.countrySelectionSpinner.text = item.title
                selectCountry = item.title
                dialog.dismiss()
            }
            custom.show()

        }


        binding.saveBtn.setOnClickListener {
            if (isInputOk()) {
                AuthService(updateProfile, this).editProfile(
                    loggedUser!!.accessToken,
                    binding.firstName.text.toString(),
                    binding.lastName.text.toString(),
                    binding.emailEt.text.toString(),
                    binding.phoneNo.text.toString(),
                    selectGender,
                    selectCountry
                )
            }

        }
    }

    private fun resetGenderView() {
        binding.male.background = ContextCompat.getDrawable(this, R.drawable.border_grey)
        binding.female.background = ContextCompat.getDrawable(this, R.drawable.border_grey)
        binding.ratherGender.background = ContextCompat.getDrawable(this, R.drawable.border_grey)
        binding.male.setTextColor(ContextCompat.getColor(this, R.color.divider))
        binding.female.setTextColor(ContextCompat.getColor(this, R.color.divider))
        binding.ratherGender.setTextColor(ContextCompat.getColor(this, R.color.divider))
    }

    private fun onPasswordChange(data: String) {
        changePassword()
        dialog = DialogCustom(this, R.drawable.ic_thumb, data)
        dialog.show()
        handler.postDelayed(runnable, 1500)
    }
    private fun changePassword() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            dialog.dismiss()
        }
    }

    private fun isInputOk(): Boolean {

        return when {
            binding.firstName.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "first_name_req"))
                false
            }
            binding.lastName.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "last_name_req"))
                false
            }

            binding.lastName.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "last_name_req"))
                false
            }

            binding.emailEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "email_required"))
                false
            }
            binding.phoneNo.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "phone_req"))
                false
            }
            else -> true
        }
    }

    override fun onSuccess(requestCode: Int, data: String) {
        when (requestCode) {
            updateProfile -> {
                DialogCustom(this, R.drawable.ic_thumb, data).show()
                updateProfileLocally()
            }
        }
    }

    private fun updateProfileLocally() {
        loggedUser?.firstName = binding.firstName.text.toString()
        loggedUser?.lastName = binding.lastName.text.toString()
        loggedUser?.email = binding.emailEt.text.toString()
        loggedUser?.phone = binding.phoneNo.text.toString()
        loggedUser?.gender = selectGender
        loggedUser?.nationality = selectCountry
        NatureDb.getInstance(this).userDao().logOut()
        NatureDb.getInstance(this).userDao().login(loggedUser!!)
    }

    override fun onFailure(requestCode: Int, data: String) {
        showToast(data)
    }

    private fun getCountryFromLocale() {
        /**get Countries from local*/
        val locales = Locale.getAvailableLocales()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        countries.forEach {
            areaSearchList.add(AreaSearchAble(it))
        }

    }

}