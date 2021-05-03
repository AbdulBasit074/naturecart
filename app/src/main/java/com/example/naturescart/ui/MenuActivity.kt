package com.example.naturescart.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.naturescart.BuildConfig
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityMenuBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.example.naturescart.services.product.ProductService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import org.greenrobot.eventbus.EventBus
import java.util.*

class MenuActivity : AppCompatActivity(), Results {

    private val registerUserRequest: Int = 122
    private val loginUserRequest: Int = 145
    private val productFavouriteRc: Int = 2389
    private val forgotPasswordRq: Int = 2459
    private val otpPasswordRq: Int = 1159
    private var loadingView: LoadingDialog? = null
    private val otpArray = arrayOfNulls<String>(4)
    private lateinit var binding: ActivityMenuBinding
    private val countries = ArrayList<String>()
    private var areaSearchList: ArrayList<AreaSearchAble> = ArrayList()
    private var loggedUser: User? = null
    private var genderSelectForRegister: String? = null
    private var countrySelectForRegister: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        loadingView = LoadingDialog(this)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        getCountryFromLocale()
        setViews()
        setListeners()
        if (loggedUser == null)
            binding.profileBtn.visibility = View.GONE
    }

    private fun setListeners() {
        binding.contactUsBtn.setOnClickListener {
            EventBus.getDefault().postSticky(MoveToAboutEvent())
            finish()

        }
        binding.rateUsBtn.setOnClickListener {
            moveToPlayStore()
        }
        binding.langBtn.setOnClickListener {
            startActivity(Intent(this, LanguageSelectionActivity::class.java))
        }
        binding.aboutBtn.setOnClickListener {
            EventBus.getDefault().postSticky(MoveToAboutEvent())
            finish()
        }
        binding.signInBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.signInBottomSheet.hideSHowPassword.setOnClickListener {

            if (binding.signInBottomSheet.passwordEt.transformationMethod == PasswordTransformationMethod.getInstance()) {
                //For Show Password
                binding.signInBottomSheet.hideSHowPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_show))
                binding.signInBottomSheet.passwordEt.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.signInBottomSheet.passwordEt.setSelection(binding.signInBottomSheet.passwordEt.text.length)

            } else {
                binding.signInBottomSheet.hideSHowPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_hide))
                binding.signInBottomSheet.passwordEt.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.signInBottomSheet.passwordEt.setSelection(binding.signInBottomSheet.passwordEt.text.length)
            }

        }
        binding.registerBottomSheet.hideSHowPassword.setOnClickListener {

            if (binding.registerBottomSheet.passwordEtRegister.transformationMethod == PasswordTransformationMethod.getInstance()) {
                //For Show Password
                binding.registerBottomSheet.hideSHowPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_show))
                binding.registerBottomSheet.passwordEtRegister.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.registerBottomSheet.passwordEtRegister.setSelection(binding.registerBottomSheet.passwordEtRegister.text.length)

            } else {
                binding.registerBottomSheet.hideSHowPassword.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_password_hide))
                binding.registerBottomSheet.passwordEtRegister.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.registerBottomSheet.passwordEtRegister.setSelection(binding.registerBottomSheet.passwordEtRegister.text.length)
            }
        }


        binding.signInBottomSheet.registerNewBtn.setOnClickListener {
            val bottomSheetSignIn = BottomSheetBehavior.from(binding.signInBottomSheet.parent)
            bottomSheetSignIn.state = BottomSheetBehavior.STATE_COLLAPSED

            val bottomSheetRegister = BottomSheetBehavior.from(binding.registerBottomSheet.parent)
            bottomSheetRegister.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetRegister.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    }
                }
            })
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.signInBottomSheet.forgotBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.forgotPasswordEmailBS.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        binding.forgotPasswordEmailBS.submitBtn.setOnClickListener {
            if (binding.forgotPasswordEmailBS.emailInput.text.isNotEmpty()) {
                loadingView?.show()
                AuthService(forgotPasswordRq, this).forgotPassword(binding.forgotPasswordEmailBS.emailInput.text.toString())
            } else
                showToast(Constants.getTranslate(this, "email_required"))
        }
        binding.forgotPasswordEmailBS.loginBtn.setOnClickListener {

            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(binding.registerBottomSheet.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.from(binding.forgotPasswordEmailBS.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.registerBottomSheet.loginBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
                BottomSheetBehavior.STATE_EXPANDED

            BottomSheetBehavior.from(binding.registerBottomSheet.parent).state =
                BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.profileBtn.setOnClickListener {
            moveTo(UserDetailActivity::class.java)
        }
        binding.registerBottomSheet.male.setOnClickListener {
            resetGenderView()
            binding.registerBottomSheet.male.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.registerBottomSheet.male.background = ContextCompat.getDrawable(this, R.drawable.border_black)
            genderSelectForRegister = binding.registerBottomSheet.male.text.toString()
        }
        binding.registerBottomSheet.female.setOnClickListener {
            resetGenderView()
            binding.registerBottomSheet.female.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.registerBottomSheet.female.background = ContextCompat.getDrawable(this, R.drawable.border_black)
            genderSelectForRegister = binding.registerBottomSheet.female.text.toString()


        }
        binding.registerBottomSheet.ratherGender.setOnClickListener {
            resetGenderView()
            binding.registerBottomSheet.ratherGender.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.registerBottomSheet.ratherGender.background = ContextCompat.getDrawable(this, R.drawable.border_black)
            genderSelectForRegister = binding.registerBottomSheet.ratherGender.text.toString()
        }
        binding.registerBottomSheet.registerBtn.setOnClickListener {
            if (isRegisterInputOk()) {
                loadingView?.show()
                AuthService(registerUserRequest, this).userRegister(
                    binding.registerBottomSheet.firstName.text.toString(),
                    binding.registerBottomSheet.lastName.text.toString(),
                    binding.registerBottomSheet.emailEtRegister.text.toString(),
                    binding.registerBottomSheet.passwordEtRegister.text.toString(),
                    getString(R.string.country_phone_code) + binding.registerBottomSheet.phoneNo.text.toString(), genderSelectForRegister, countrySelectForRegister
                )
            }
        }

        binding.registerBottomSheet.countrySelectionSpinner.setOnClickListener {

            var custom = SimpleSearchDialogCompat(this, Constants.getTranslate(this, "select_country"), Constants.getTranslate(this, "search"), null, areaSearchList) { dialog, item, position ->
                binding.registerBottomSheet.countrySelectionSpinner.text = item.title
                countrySelectForRegister = item.title
                dialog.dismiss()
            }
            custom.show()

        }


        binding.signInBottomSheet.loginBtn.setOnClickListener {
            if (isLoginInputOk()) {
                loadingView?.show()
                AuthService(loginUserRequest, this).userLogin(
                    binding.signInBottomSheet.emailEt.text.toString(),
                    binding.signInBottomSheet.passwordEt.text.toString(),
                    Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey, "").toString()
                )
            }
        }
        binding.otpVerifyBS.verifyBtn.setOnClickListener {
            loadingView?.show()
            AuthService(otpPasswordRq, this).verifyOtp(binding.forgotPasswordEmailBS.emailInput.text.toString(), otpArray.convertToString().toInt())
        }
        binding.otpVerifyBS.digit1Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.otpVerifyBS.digit2Et.requestFocus()
                    otpArray[0] = binding.otpVerifyBS.digit1Et.text.toString()
                }
            }
        })
        binding.otpVerifyBS.digit2Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.otpVerifyBS.digit3Et.requestFocus()
                    otpArray[1] = binding.otpVerifyBS.digit2Et.text.toString()
                } else {
                    binding.otpVerifyBS.digit1Et.requestFocus()
                }
            }
        })
        binding.otpVerifyBS.digit3Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.otpVerifyBS.digit4Et.requestFocus()
                    otpArray[2] = binding.otpVerifyBS.digit3Et.text.toString()
                } else {
                    binding.otpVerifyBS.digit2Et.requestFocus()
                }
            }
        })
        binding.otpVerifyBS.digit4Et.addTextChangedListener(object : MyTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    otpArray[3] = binding.otpVerifyBS.digit4Et.text.toString()
                    loadingView?.show()
                    AuthService(otpPasswordRq, this@MenuActivity).verifyOtp(binding.forgotPasswordEmailBS.emailInput.text.toString(), otpArray.convertToString().toInt())
                    hideKeyboard()
                } else {
                    binding.otpVerifyBS.digit3Et.requestFocus()
                }
            }
        })
        val textColor = ContextCompat.getColor(this, R.color.saleem_green2)
        val privacyPolicyText = SpannableString(Constants.getTranslate(this, "terms_and_policy"))
        privacyPolicyText.setSpan(object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(WebViewActivity.newInstance(this@MenuActivity, Constants.getTranslate(this@MenuActivity, "terms"), Constants.termsUrl))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = textColor
            }
        }, 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        privacyPolicyText.setSpan(object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(WebViewActivity.newInstance(this@MenuActivity, Constants.getTranslate(this@MenuActivity, "privacy"), Constants.privacyPolicyUrl))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = textColor
            }
        }, 22, privacyPolicyText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.registerBottomSheet.termsAndConditionTv.text = privacyPolicyText
        binding.registerBottomSheet.termsAndConditionTv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun resetGenderView() {
        binding.registerBottomSheet.male.background = ContextCompat.getDrawable(this, R.drawable.border_grey)
        binding.registerBottomSheet.female.background = ContextCompat.getDrawable(this, R.drawable.border_grey)
        binding.registerBottomSheet.ratherGender.background = ContextCompat.getDrawable(this, R.drawable.border_grey)
        binding.registerBottomSheet.male.setTextColor(ContextCompat.getColor(this, R.color.divider))
        binding.registerBottomSheet.female.setTextColor(ContextCompat.getColor(this, R.color.divider))
        binding.registerBottomSheet.ratherGender.setTextColor(ContextCompat.getColor(this, R.color.divider))
    }

    private fun isRegisterInputOk(): Boolean {
        when {

            binding.registerBottomSheet.firstName.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "first_name_req"))
                return false
            }
            binding.registerBottomSheet.lastName.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "last_name_req"))
                return false
            }
            binding.registerBottomSheet.emailEtRegister.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "email_required"))
                return false
            }
            binding.registerBottomSheet.passwordEtRegister.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "passwrod_must_req"))
                return false
            }

            binding.registerBottomSheet.phoneNo.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "phone_req"))
                return false
            }
            else -> return true
        }
    }


    private fun isLoginInputOk(): Boolean {

        return when {
            binding.signInBottomSheet.emailEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "email_required"))
                false
            }
            binding.signInBottomSheet.passwordEt.text.isEmpty() -> {
                showToast(Constants.getTranslate(this, "passwrod_must_req"))
                false
            }
            else -> true

        }
    }

    private fun setViews() {
        BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
            BottomSheetBehavior.STATE_COLLAPSED
        countries.forEach {
            areaSearchList.add(AreaSearchAble(it))
        }

    }


    override fun onSuccess(requestCode: Int, data: String) {

        Handler(Looper.getMainLooper()).postDelayed({

            loadingView?.dismiss()

            when (requestCode) {
                registerUserRequest -> {
                    AuthService(loginUserRequest, this).userLogin(
                        binding.registerBottomSheet.emailEtRegister.text.toString(),
                        binding.registerBottomSheet.passwordEtRegister.text.toString(),
                        Persister.with(this).getPersisted(Constants.fcmTokenPersistenceKey, "").toString()
                    )
                }
                loginUserRequest -> {
                    saveUserDetail(data)
                }
                forgotPasswordRq -> {
                    showToast(data)
                    BottomSheetBehavior.from(binding.otpVerifyBS.parent).state = BottomSheetBehavior.STATE_EXPANDED
                    binding.otpVerifyBS.digit1Et.requestFocus()
                    showKeyboard()

                }
                otpPasswordRq -> {
                    BottomSheetBehavior.from(binding.otpVerifyBS.parent).state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    BottomSheetBehavior.from(binding.forgotPasswordEmailBS.parent).state =
                        BottomSheetBehavior.STATE_COLLAPSED
                    binding.otpVerifyBS.digit1Et.text.clear()
                    binding.otpVerifyBS.digit2Et.text.clear()
                    binding.otpVerifyBS.digit3Et.text.clear()
                    binding.otpVerifyBS.digit4Et.text.clear()
                    hideKeyboard()
                    DialogCustomForgotPasswordChange(this, binding.forgotPasswordEmailBS.emailInput.text.toString()) { data ->
                        onPasswordChange(
                            data
                        )
                    }.show()
                }
            }
        }, 1000)

    }

    private fun onPasswordChange(data: String) {
        BottomSheetBehavior.from(binding.signInBottomSheet.parent).state =
            BottomSheetBehavior.STATE_EXPANDED
        DialogCustom(this, R.drawable.ic_thumb, data).show()
    }

    private fun saveUserDetail(data: String) {
        loggedUser = Gson().fromJson(data, User::class.java)
        NatureDb.getInstance(this).userDao().logOut()
        NatureDb.getInstance(this).userDao().login(loggedUser!!)
        val favorites = NatureDb.getInstance(this).favouriteDao().getAllProduct()
        favorites.forEach {
            ProductService(productFavouriteRc, this).addToFavourite(loggedUser?.accessToken ?: "", it.id ?: 0)
        }
        EventBus.getDefault().postSticky(LogInEvent())
        if (callingActivity == null) {
            moveTo(UserDetailActivity::class.java)
            finish()
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
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
    }

    override fun onFailure(requestCode: Int, data: String) {
        loadingView?.dismiss()
        showToast(data)
    }

    private fun moveToPlayStore() {
        val uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }
}
