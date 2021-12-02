package com.santis.minharua.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

object ViewAnimation {

    interface AnimListener {
        fun onFinish()
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }

    fun expand(v: View) {
        val a = expandAction(v)
        v.startAnimation(a)
    }

    private fun expandAction(v: View): Animation {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight
        v.layoutParams.height = 0
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
        return a
    }

    fun fadeIn(v: View) {
        fadeIn(v, null)
    }

    private fun fadeIn(v: View, animListener: AnimListener?) {
        v.visibility = View.GONE
        v.alpha = 0.0f
        // Prepare the View for the animation
        v.animate()
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.visibility = View.VISIBLE
                    animListener?.onFinish()
                    super.onAnimationEnd(animation)
                }
            })
            .alpha(1.0f)
    }

    fun fadeOut(v: View) {
        fadeOut(v, null)
    }

    private fun fadeOut(v: View, animListener: AnimListener?) {
        v.alpha = 1.0f
        // Prepare the View for the animation
        v.animate()
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animListener?.onFinish()
                    super.onAnimationEnd(animation)
                }
            })
            .alpha(0.0f)
    }
}
