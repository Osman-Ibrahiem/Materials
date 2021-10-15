package com.osman.nagwa_task.ui.splash

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

fun View.animateLogo(
    lifecycle: Lifecycle,
    animateCount: Int = 3,
    onAnimatorEnd: () -> Unit
) {
    val lifecycleObserver = object : LifecycleObserver {

        private val handler = Handler(Looper.getMainLooper())
        private lateinit var animatorScale: ObjectAnimator
        private lateinit var animatorAlpha: ObjectAnimator
        private lateinit var animatorReverse: ObjectAnimator

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            var count = 1
            animatorScale =
                animatorOfProperty(300, "scaleX" to 1.2f, "scaleY" to 1.2f, "alpha" to 1f)
            animatorReverse =
                animatorOfProperty(300, "scaleX" to 1.0f, "scaleY" to 1.0f, "alpha" to 1f)
            animatorAlpha =
                animatorOfProperty(600, "scaleX" to 100f, "scaleY" to 100f, "alpha" to 0f)

            animatorScale.addListener(onEnd = {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    animatorReverse.start()
                }
            })

            animatorReverse.addListener(onEnd = {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    handler.postDelayed({
                        count++
                        if (count == animateCount) {
                            animatorAlpha.start()
                        } else {
                            animatorScale.start()
                        }
                    }, 500)
                }
            })

            animatorAlpha.addListener(onEnd = {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    onAnimatorEnd()
                }
            })

            if (scaleX != 1.0f || scaleY != 1.0f) {
                count = 0
                animatorReverse.start()
            } else {
                handler.postDelayed({ animatorScale.start() }, 300)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            handler.removeCallbacksAndMessages(null)
            animatorScale.cancel()
            animatorAlpha.cancel()
            animatorReverse.cancel()
        }
    }

    lifecycle.addObserver(lifecycleObserver)

}

private fun View.animatorOfProperty(
    duration: Long,
    vararg properties: Pair<String, Float>
) = ObjectAnimator.ofPropertyValuesHolder(
    this,
    *properties.map { PropertyValuesHolder.ofFloat(it.first, it.second) }.toTypedArray()
).apply { this.duration = duration }