package com.zhangjian.coherent

import android.os.Build
import android.view.View
import android.widget.ProgressBar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

/**
 * Created by zhangjian on 2021/2/1.
 */
const val PERIOD = 50L
const val TIMES = 1000L / PERIOD

class ProgressCoherentRealValueProcess(
    target: View,
    private var progressUpdate: (Float) -> Unit
) {
    private val viewReference = WeakReference<View>(target)

    init {
        target.alpha = 0f
    }

    private var disposable: Disposable? = null
    private var interval = 0f
    private fun notifyProgress() {
        mCurrentProgress = if (mCurrentProgress <= 0f) 0.00000001f else mCurrentProgress
        interval = mReadyToProgress - mCurrentProgress //间隔
        if (disposable == null || disposable!!.isDisposed) {
            disposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .filter { mCurrentProgress < mReadyToProgress }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val view = viewReference.get()
                    if (view == null) {
                        release()
                    } else {
                        mCurrentProgress = 1f.coerceAtMost(mCurrentProgress + interval / TIMES)
                        if (mReadyToProgress == 1f) {
                            view.alpha = view.alpha - 1f / (TIMES + 2)//透明度变化稍微小一丁点，保持进度到1之前还有一点
                        } else {
                            view.alpha = 1f
                        }
                        if (mCurrentProgress == 1f) {
                            view.alpha = 0f
                            release()
                        }
                    }

                }
        }
    }

    private var mCurrentProgress = -1f
        set(value) {
            field = value
            progressUpdate.invoke(value)
        }
    private var mReadyToProgress = -1f
    private fun inLoading(): Boolean {
        return mCurrentProgress > 0 && mCurrentProgress < 1
    }

    /**
     * 接收的进度是0-1的float
     *
     * @param progress
     */
    fun setProgress(progress: Float) {
        if (viewReference.get()?.isAttachedToWindow == true) {
            if (!inLoading() && progress == 1f) {
                //当前进度不在动画中且直接设置1的，直接不显示
                reset()
            } else if (progress in 0.0..1.0) {
                if (progress < mReadyToProgress) {
                    //复用或者被设置了新的进度等，设置的进度小于当前进度时,停止当前进度
                    reset()
                    release()
                }
                mReadyToProgress = progress
                notifyProgress()
            } else {
                reset()
            }
        } else {
            //View没有AttachedToWindow的时候直接把进度丢回去，无需动画
            release()
            mCurrentProgress = progress
            viewReference.get()?.alpha = if (progress < 1) 1f else 0f
        }
    }

    private fun reset() {
        mCurrentProgress = -1f
        viewReference.get()?.alpha = 0f
    }

    private fun release() {
        if (disposable != null) {
            disposable!!.dispose()
            disposable = null
        }
    }
}

fun createRealValueForProgressBar(progressBar: ProgressBar): ProgressCoherentRealValueProcess {
    return ProgressCoherentRealValueProcess(progressBar) {
        ceil(it * 100).toInt().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(this, true)
            } else {
                progressBar.progress = this
            }
        }
    }
}