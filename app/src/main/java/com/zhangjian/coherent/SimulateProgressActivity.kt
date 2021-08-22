package com.zhangjian.coherent

import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zhangjian.coherent.databinding.LayoutSimulateProgressBinding

/**
 * Created zhangjian on 2021/8/22(22:09) in project Coherent.
 */
class SimulateProgressActivity : AppCompatActivity() {

    private lateinit var binding: LayoutSimulateProgressBinding

    private lateinit var simulateProcess: ProgressCoherentSimulateProcess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_simulate_progress)

        simulateProcess = createSimulateForProgressBar(binding.progress)
        //针对ProgressBar提供了专门的创建方法
//        simulateProcess = ProgressCoherentSimulateProcess(binding.progress){
//            binding.progress.progress = (it*100).toInt()
//        }

        initWebView()

        load()
    }

    private fun initWebView() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true //将图 片调整到适合webview的大小
        webSettings.setGeolocationEnabled(true)
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.domStorageEnabled = true

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                simulateProcess.setProgress(newProgress / 100f)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                setTitle(title)
            }
        }
    }

    private fun load() {
        binding.webView.loadUrl("https://www.jianshu.com/u/6fc58e84f8c1")
    }

    override fun onDestroy() {
        super.onDestroy()
        simulateProcess.release()
    }
}