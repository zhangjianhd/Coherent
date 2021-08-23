package com.zhangjian.coherent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zhangjian.coherent.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        dataBinding.viewModel = viewModel

        progressProcess = createProgressProcessForProgressBar(dataBinding.progressBarCoherent)

        coverProcess = ProgressCoherentRealValueProcess(dataBinding.tcCoherentProgressCover) {
            dataBinding.tcCoherentProgressCover.setProgress(it)
        }

        dataBinding.btnStart.setOnClickListener {
            start()
        }
    }

    private var disposable: Disposable? = null

    private lateinit var progressProcess: ProgressCoherentRealValueProcess
    private lateinit var coverProcess: ProgressCoherentRealValueProcess

    private fun start() {
        disposable?.let {
            if (!it.isDisposed) {
                return
            }
        }
        var index = 0
        disposable = Observable.interval(0,800, TimeUnit.MILLISECONDS)
            .take(progress.size.toLong())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progress[index].let {
                    viewModel.progressRel.set(it * 100f)
                    dataBinding.progressBarNormal.progress = (it * 100).toInt()
                    dataBinding.tcNormalProgressCover.setProgress(it)
                    progressProcess.setProgress(it)
                    coverProcess.setProgress(it)
                }
                index++
            }
    }

    private val progress = mutableListOf<Float>()
    private val viewModel = MainViewModel()

    init {
        progress.add(0.05f)
        progress.add(0.15f)
//        progress.add(0.23f)
//        progress.add(0.38f)
        progress.add(0.50f)
        progress.add(0.58f)
        progress.add(0.69f)
        progress.add(0.72f)
        progress.add(0.78f)
        progress.add(0.88f)
        progress.add(0.95f)
        progress.add(1.00f)
    }
}