package com.habitquest.ui.component

import androidx.annotation.DrawableRes
import com.habitquest.R

@DrawableRes
fun userAvatarResOrDefault(userAvatar: String?): Int {
    return if (userAvatar.isNullOrBlank()) {
        R.drawable.avatar_default
    } else {
        R.drawable.avatar_default
    }
}
