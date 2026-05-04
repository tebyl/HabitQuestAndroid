package com.habitquest.ui.component

import androidx.annotation.DrawableRes
import com.habitquest.R
import com.habitquest.ui.screen.profile.AVATAR_OPTIONS

@DrawableRes
fun userAvatarResOrDefault(userAvatar: String?): Int {
    return AVATAR_OPTIONS.firstOrNull { it.emoji == userAvatar }?.imageRes
        ?: R.drawable.avatar_default
}
