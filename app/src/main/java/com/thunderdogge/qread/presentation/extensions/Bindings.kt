package com.thunderdogge.qread.presentation.extensions

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.bottomsheet.BottomSheetBehavior

@BindingAdapter("isGone")
fun setIsGone(view: View, value: Boolean) {
    view.visibility = if (value) View.GONE else View.VISIBLE
}

@BindingAdapter("isVisible")
fun setIsVisible(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}

@BindingAdapter("isExpanded")
fun setIsExpanded(view: View, value: Boolean) {
    val behaviour = BottomSheetBehavior.from(view)
    behaviour.state = if (value) {
        BottomSheetBehavior.STATE_EXPANDED
    } else {
        BottomSheetBehavior.STATE_HIDDEN
    }
}

@InverseBindingAdapter(attribute = "isExpanded", event = "isExpandedAttrChanged")
fun getIsExpanded(view: View): Boolean {
    val state = BottomSheetBehavior.from(view).state
    return state == BottomSheetBehavior.STATE_EXPANDED
}

@BindingAdapter("isExpandedAttrChanged")
fun setIsExpandedListener(view: View, listener: InverseBindingListener) {
    val behaviour = BottomSheetBehavior.from(view)
    behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN,
                BottomSheetBehavior.STATE_EXPANDED,
                BottomSheetBehavior.STATE_COLLAPSED -> listener.onChange()
                BottomSheetBehavior.STATE_DRAGGING,
                BottomSheetBehavior.STATE_HALF_EXPANDED,
                BottomSheetBehavior.STATE_SETTLING -> Unit
            }
        }
    })
}