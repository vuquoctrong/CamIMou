package com.mm.android.mobilecommon.views.fragment;

import android.Manifest
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TextView
import com.mm.android.mobilecommon.R
import com.mm.android.mobilecommon.base.BaseDialogFragment
import com.mm.android.mobilecommon.base.DefaultPermissionListener
import com.mm.android.mobilecommon.common.LCConfiguration
import com.mm.android.mobilecommon.common.PermissionHelper
import com.mm.android.mobilecommon.utils.LogUtil
import java.net.URISyntaxException

class WebViewDialogFragment : BaseDialogFragment(), View.OnClickListener {
    protected var mWebView //WebView
            : WebView? = null
    private var mProgressBar //进度条
            : ProgressBar? = null
    protected var mURL //URL
            : String? = null
    private var mBundle: Bundle? = null
    private var mPermissionHelper: PermissionHelper? = null
    private var mNext: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPermissionHelper = PermissionHelper(this)
        setStyle(STYLE_NORMAL, R.style.iot_add_mobile_common_checks_dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(true)
    }

    override fun onStart() {
        super.onStart()
        suitFullScreen()
    }

    private fun suitFullScreen() {
        val dialogWindow = dialog!!.window
        val lp = dialogWindow!!.attributes
        //        lp.gravity = Gravity.BOTTOM;
        lp.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        lp.height = (resources.displayMetrics.heightPixels * 0.95).toInt()
        dialogWindow.attributes = lp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.iotadd_webview_dialog_layout, container, false)
        initView(view)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        exit()
    }

    protected fun exit() {
        if (mWebView == null) {
            return
        }
        //        unRegisterHandler(mWebView);
        mWebView!!.stopLoading()
        mWebView!!.clearCache(true)
        mWebView!!.visibility = View.GONE
        mWebView!!.destroy()
        mWebView!!.webChromeClient = null
        mWebView = null
    }

    private fun initView(view: View) {
        mBundle = arguments
        if (mBundle == null) return
        initWebView(view)
        mProgressBar = view.findViewById(R.id.myProgressBar)
        mNext = view.findViewById(R.id.next)
        mNext?.setText(R.string.common_i_know)
        mNext?.setOnClickListener(this)
    }

    private fun initWebView(view: View) {
        mWebView = view.findViewById(R.id.common_webview)
        mURL = if (mBundle!!.containsKey(LCConfiguration.URL)) mBundle!!.getString(LCConfiguration.URL) else ""
        // mURL = "https://resource-public-test.oss-cn-hangzhou.aliyuncs.com/H5Demo/openWithBrowser/page.html";
        val webSet = mWebView!!.getSettings()
        webSet.builtInZoomControls = false
        webSet.allowFileAccess = true
        webSet.javaScriptEnabled = true
        webSet.savePassword = false
        webSet.setGeolocationEnabled(true)
        webSet.cacheMode = WebSettings.LOAD_NO_CACHE //不使用缓存
        webSet.domStorageEnabled = true
        //以下两行代码用于H5自适应手机屏幕
        webSet.useWideViewPort = true
        webSet.loadWithOverviewMode = true
        webSet.setUserAgentString(webSet.userAgentString + " " + getString(R.string.application_name))
        mWebView!!.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    mProgressBar!!.visibility = View.GONE
                    //progress == 100会多次执行，但不得不先放在这里，因为有些网页不执行onPageFinished()等方法
//                    showOrHideCloseBtn();
                } else {
                    if (View.GONE == mProgressBar!!.visibility || View.INVISIBLE == mProgressBar!!.visibility) {
                        mProgressBar!!.visibility = View.VISIBLE
                    }
                    mProgressBar!!.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }

            //            @Override
            //            public void onReceivedTitle(WebView view, String title) {
            //                super.onReceivedTitle(view, title);
            //                if (mBundle.getBoolean("isTitleFollowHTML", false)) {
            //                    if (!TextUtils.isEmpty(title) && !view.getUrl().equals(title) && !title.startsWith("http")) {
            //                        mTitle.setTitleTextCenter(title);
            //                    }
            //                }
            //            }
            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                mPermissionHelper!!.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), object : DefaultPermissionListener() {
                    override fun onGranted() {
                        callback.invoke(origin, true, true)
                    }

                    override fun onDenied(): Boolean {
                        callback.invoke(origin, false, false)
                        return true
                    }
                })
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }
        })
        mWebView!!.setWebViewClient(object : WebViewClient() {
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                LogUtil.debugLog("29217", "onReceivedSslError(CommonWebViewFragment.java:157)------->>Url=" + error.url + "error = " + error.primaryError)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

//                String title = view.getTitle();
//                if (mBundle.getBoolean("isTitleFollowHTML", false))
//                {
//                    mTitle.setTitleTextCenter((title == null || title.startsWith("http")) ? "" : title);
//                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //处理intent协议
                if (url.startsWith("intent://")) {
                    val intent: Intent
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.component = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            intent.selector = null
                        }
                        if (Build.VERSION.SDK_INT >= 30) {
                            if (activity!!.packageManager.hasSystemFeature(Intent.URI_INTENT_SCHEME.toString())) {
                                startActivity(intent)
                            }
                        } else {
                            val resolves = activity!!.packageManager.queryIntentActivities(intent, 0)
                            if (resolves.size > 0) {
                                startActivity(intent)
                            }
                        }
                        return true
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        })

//        registerBaseHandler(mWebView);
//        registerHandler(mWebView);
        if (!TextUtils.isEmpty(mURL)) {
            LogUtil.debugLog("WebViewDialogFragment", "url:$mURL")
            mWebView!!.loadUrl(mURL!!)
        }

    }

    override fun onClick(v: View) {
        if (v.id == R.id.next) {
            dismiss()
        }
    }

    companion object {
        fun newInstance(url: String?): WebViewDialogFragment {
            val webViewDialogFragment = WebViewDialogFragment()
            val bundle = Bundle()
            bundle.putString(LCConfiguration.URL, url)
            webViewDialogFragment.arguments = bundle
            return webViewDialogFragment
        }
    }
}