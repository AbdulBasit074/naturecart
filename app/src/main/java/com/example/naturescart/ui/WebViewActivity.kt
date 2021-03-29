package com.example.naturescart.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityWebViewBinding
import com.example.naturescart.helper.Constants
import com.example.naturescart.helper.LoadingDialog
import com.example.naturescart.helper.setLanguage

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private var loadingView: LoadingDialog? = null

    companion object {
        fun newInstance(context: Context, title: String, url: String): Intent {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(Constants.dataPassKey, url)
            intent.putExtra(Constants.titlePassKey, title)
            return intent
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguage()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        loadingView = LoadingDialog(this)
        loadingView?.show()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.domStorageEnabled = true
        binding.actionBarTitleTv.text = intent.getStringExtra(Constants.titlePassKey)!!
        val url = intent.getStringExtra(Constants.dataPassKey)!!
        var toLoad = url
        if (url.endsWith("pdf") || url.endsWith("doc") || url.endsWith("docx"))
            toLoad = "http://drive.google.com/viewerng/viewer?embedded=true&url=$url"
        binding.webView.loadUrl(toLoad)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingView?.dismiss()
            }
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        else {
            super.onBackPressed()
        }
    }
}