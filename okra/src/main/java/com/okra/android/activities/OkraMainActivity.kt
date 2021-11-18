package com.okra.android.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.Gson
import com.okra.android.R
import com.okra.android.`interface`.IOkraWebInterface
import com.okra.android.R.id.ok_webview
import com.okra.android.R.id.progressBar
import com.okra.android.utils.OkraWebInterface

class OkraMainActivity : AppCompatActivity(), IOkraWebInterface {

    companion object {
        const val OKRA_OBJECT = "okraObject"
        const val OKRA_RESULT = "okraResult"
        fun newIntent(context: Context, obj: Any): Intent {
            val intent = Intent(context, OkraMainActivity::class.java)
            intent.putExtra(OKRA_OBJECT, Gson().toJson(obj))
            return intent
        }
    }

    private lateinit var intentForResult :Intent
    private lateinit var webView: WebView
    private lateinit var okraProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(supportActionBar !=null)
            this.supportActionBar?.hide();
        setContentView(R.layout.activity_okra_main)
        val okraObject = intent.getStringExtra(OKRA_OBJECT) ?: throw IllegalStateException("Field $OKRA_OBJECT missing in Intent")

        intentForResult = Intent()
        webView = findViewById(ok_webview)
        setupWebView()
        okraProgressBar = findViewById(progressBar)
        setupWebClient(okraObject)
    }

    private fun setupWebClient(okraObject: String) {
        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                okraProgressBar.visibility = View.GONE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("openOkraWidget('${Gson().toJson(Gson().fromJson(okraObject,Any::class.java))}');", null)
                } else {
                    webView.loadUrl("openOkraWidget('${Gson().toJson(Gson().fromJson(okraObject,Any::class.java))}');")
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.addJavascriptInterface(OkraWebInterface(this), "Android")

        webView.loadUrl("https://mobile.okra.ng/")
    }

    override fun onSuccess(json: String) {
        intentForResult.putExtra(OKRA_RESULT, json)
        setResult(Activity.RESULT_OK,intentForResult)
        finish()
    }

    override fun onError(json: String) {
        intentForResult.putExtra(OKRA_RESULT, json)
        setResult(Activity.RESULT_CANCELED,intentForResult)
        finish()
    }

    override fun onClose(json: String) {
        intentForResult.putExtra(OKRA_RESULT, json)
        setResult(Activity.RESULT_CANCELED,intentForResult)
        finish()
    }

    override fun exitModal(json: String) {
        intentForResult.putExtra(OKRA_RESULT, json)
        setResult(Activity.RESULT_CANCELED,intentForResult)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit")
            .setMessage("Are you sure?")
            .setNegativeButton("No", null)
            .setPositiveButton("Yes"){dialogInterface, which ->
                onClose("closed")
            }
            .create().show()
    }
}