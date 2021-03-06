package com.example.naturescart.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityPaymentWebViewBinding
import com.example.naturescart.helper.*
import org.json.JSONObject
import java.text.DateFormat
import java.util.*


class PaymentWebView : AppCompatActivity() {

    companion object {
        fun newInstance(context: Context, cartID: Long, userID: Int, addressID: Int, contactLess: Boolean): Intent {
            return Intent(context, PaymentWebView::class.java).putExtra(Constants.cartID, cartID)
                .putExtra(Constants.userID, userID)
                .putExtra(Constants.addressID, addressID)
                .putExtra(Constants.contactLess, contactLess)
        }
    }

    private var cartID: Long = 0
    private var userID: Int = 0
    private var addressID: Int = 0
    private var contactLess: Boolean = false

    private lateinit var binding: ActivityPaymentWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_web_view)
        cartID = intent.getLongExtra(Constants.cartID, 0)
        userID = intent.getIntExtra(Constants.userID, 0)
        addressID = intent.getIntExtra(Constants.addressID, 0)
        contactLess = intent.getBooleanExtra(Constants.contactLess, false)
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        setWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        val loadingView = LoadingDialog(this)
        val currentDateTimeString: String = DateFormat.getDateInstance().format(Date())
        loadingView.show()
        binding.webView.loadUrl(Constants.paymentMethodUrl + "$cartID" + "/$userID" + "/$addressID" + "/$contactLess" + "/${TranslationsHelper.getInstance(this).getLocale()}/")
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingView.dismiss()
                view!!.evaluateJavascript(
                    "document.getElementsByTagName('pre')[0].innerHTML",
                    JavaScriptResult()
                )
            }
        }
    }

    inner class JavaScriptResult : ValueCallback<String> {
        override fun onReceiveValue(value: String?) {
            if (value != "null" && value != null) {
                if (value.contains("status")) {
                    if (value.contains("200")) {
                        Persister.with(this@PaymentWebView).persist(Constants.cartPersistenceKey, null)
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        try {
                            val unescaped: String = value.substring(1, value.length - 1) // remove wrapping quotes
                                .replace("\\\\", "\\") // unescape \\ -> \
                                .replace("\\\"", "\"") // unescape \" ->
                            DialogErrorCustom(this@PaymentWebView, R.drawable.ic_error, JSONObject(unescaped).getString("message")) { onOkClick() }.show()
                        } catch (e: Exception) {
                            DialogErrorCustom(this@PaymentWebView, R.drawable.ic_error, "Please Try Again") { onOkClick() }.show()
                        }
                    }
                }
            }
        }
    }
    private fun onOkClick() {
        finish()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        else
            super.onBackPressed()

    }
}