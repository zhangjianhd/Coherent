package com.zhangjian.coherent

import android.animation.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import java.lang.ref.WeakReference
import kotlin.math.ceil

/**
 * Created by zhangjian on 2021/2/1.
 */
class ProgressCoherentSimulateProcess(target: View, private var progressUpdate: (Float) -> Unit) {
    private val viewReference = WeakReference<View>(target)
    private var mAnimator: Animator? = null
    private var alphaAnimator: ObjectAnimator? = null
    private fun cancelAnim() {
        if (mAnimator != null && mAnimator!!.isStarted) {
            mAnimator!!.cancel()
        }
        if (alphaAnimator != null && alphaAnimator!!.isStarted) {
            alphaAnimator!!.cancel()
        }
    }

    private fun startAnim(toEnd: Boolean) {
        cancelAnim()
        mCurrentProgress = if (mCurrentProgress <= 0f) 0.001f else mCurrentProgress
        if (toEnd) {
            //到1f，
            var segmentStartAnimator: Animator? = null
            if (mCurrentProgress < CONSTANT_SPEED_MAX_PROGRESS) {
                segmentStartAnimator = segmentStartAnimator(MAX_DECELERATE_SPEED_DURATION)
                segmentStartAnimator.interpolator = DecelerateInterpolator()
            }
            alphaAnimator = ObjectAnimator.ofFloat(viewReference.get()!!, "alpha", 1f, 0f)
            alphaAnimator!!.duration = DO_END_ALPHA_DURATION.toLong()
            alphaAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    viewReference.get()?.let {
                        it.visibility = View.GONE
                    }
                }
            })
            val segmentEndAnimator =
                ValueAnimator.ofFloat(CONSTANT_SPEED_MAX_PROGRESS, MAX_PROGRESS)
            segmentEndAnimator.duration = DO_END_PROGRESS_DURATION.toLong()
            segmentEndAnimator.addUpdateListener(mAnimatorUpdateListener)
            var mAnimatorSet = AnimatorSet()
            mAnimatorSet.playTogether(alphaAnimator, segmentEndAnimator)
            if (segmentStartAnimator != null) {
                val mAnimatorSet1 = AnimatorSet()
                mAnimatorSet1.play(mAnimatorSet).after(segmentStartAnimator)
                mAnimatorSet = mAnimatorSet1
            }
            mAnimatorSet.addListener(mAnimatorListenerAdapter)
            mAnimator = mAnimatorSet
            mAnimator?.let {
                it.cancel()
            }
            mAnimator!!.start()
            isEnd = true
        } else {
            mAnimator?.let {
                it.cancel()
            }
            mAnimator = segmentStartAnimator(MAX_UNIFORM_SPEED_DURATION)
            mAnimator!!.interpolator = LinearInterpolator()
            mAnimator!!.start()
        }
        inProgress = true
    }

    private fun segmentStartAnimator(duration: Int): Animator {
        val constantSpeedAnimator = ValueAnimator.ofFloat(mCurrentProgress, CONSTANT_SPEED_MAX_PROGRESS)
        val residue = (CONSTANT_SPEED_MAX_PROGRESS - mCurrentProgress) / MAX_PROGRESS
        constantSpeedAnimator.duration = (residue * duration).toLong()
        constantSpeedAnimator.addUpdateListener(mAnimatorUpdateListener)
        return constantSpeedAnimator
    }

    private var mCurrentProgress = -1f
    private var inProgress = false
    private var isEnd = false

    /**
     * 接收的进度是0-1的float
     *
     * @param progress
     */
    fun setProgress(progress: Float) {
        if (progress <= 0) {
            reset()
        }
        val target = viewReference.get()
        target?.let {
            if (progress >= 0) {
                if (progress < 1f && !inProgress) {
                    it.visibility = View.VISIBLE
                    startAnim(false)
                }
                if (progress == 1f && !isEnd && mCurrentProgress != progress) {
                    it.visibility = View.VISIBLE
                    startAnim(true)
                }
            } else {
                it.visibility = View.GONE
            }
        }
    }

    private fun updateCurrentProgress(progress: Float) {
        mCurrentProgress = progress
        progressUpdate.invoke(progress)
    }

    private val mAnimatorUpdateListener = AnimatorUpdateListener { animation ->
        updateCurrentProgress(animation.animatedValue as Float)
        if (mCurrentProgress < CONSTANT_SPEED_MAX_PROGRESS) {
            target.alpha = 1f
        }
    }
    private val mAnimatorListenerAdapter: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            reset()
        }
    }

    private fun reset() {
        inProgress = false
        isEnd = false
        mCurrentProgress = -1f
    }

    fun release() {
        mAnimator?.let {
            it.cancel()
        }
    }

    companion object {
        /**
         * 默认匀速动画最大的时长
         */
        private const val MAX_UNIFORM_SPEED_DURATION = 8 * 1000

        /**
         * 默认加速后减速动画最大时长
         */
        private const val MAX_DECELERATE_SPEED_DURATION = 450

        /**
         * 0.95f-1f时，透明度1f-0f时长
         */
        private const val DO_END_ALPHA_DURATION = 630

        /**
         * 0.95f - 1f动画时长
         */
        private const val DO_END_PROGRESS_DURATION = 500
        private const val CONSTANT_SPEED_MAX_PROGRESS = 0.95f
        private const val MAX_PROGRESS = 1f
    }

    init {
        target.visibility = View.GONE
        this.progressUpdate = progressUpdate
    }
}

fun createSimulateForProgressBar(progressBar: ProgressBar): ProgressCoherentSimulateProcess {
    return ProgressCoherentSimulateProcess(progressBar) {
        ceil(it * 100f).toInt().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(this, true)
            } else {
                progressBar.progress = this
            }
        }
    }
}