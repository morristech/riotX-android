/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.riotredesign.features.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnyThread
import androidx.annotation.ColorRes
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import im.vector.matrix.android.api.Matrix
import im.vector.matrix.android.api.MatrixPatterns
import im.vector.matrix.android.api.session.content.ContentUrlResolver
import im.vector.matrix.android.api.session.room.model.RoomSummary
import im.vector.riotredesign.R
import im.vector.riotredesign.core.glide.GlideApp
import im.vector.riotredesign.core.glide.GlideRequest
import im.vector.riotredesign.core.glide.GlideRequests

/**
 * This helper centralise ways to retrieve avatar into ImageView or even generic Target<Drawable>
 */

object AvatarRenderer {

    private const val THUMBNAIL_SIZE = 250

    private val AVATAR_COLOR_LIST = listOf(
            R.color.avatar_color_1,
            R.color.avatar_color_2,
            R.color.avatar_color_3
    )

    @UiThread
    fun render(roomSummary: RoomSummary, imageView: ImageView) {
        render(roomSummary.avatarUrl, roomSummary.roomId, roomSummary.displayName, imageView)
    }

    @UiThread
    fun render(avatarUrl: String?, identifier: String, name: String?, imageView: ImageView) {
        render(imageView.context, GlideApp.with(imageView), avatarUrl, identifier, name, DrawableImageViewTarget(imageView))
    }

    @UiThread
    fun render(context: Context,
               glideRequest: GlideRequests,
               avatarUrl: String?,
               identifier: String,
               name: String?,
               target: Target<Drawable>) {
        if (name.isNullOrEmpty()) {
            return
        }
        val placeholder = getPlaceholderDrawable(context, identifier, name)
        buildGlideRequest(glideRequest, avatarUrl)
                .placeholder(placeholder)
                .into(target)
    }

    @AnyThread
    fun getPlaceholderDrawable(context: Context, identifier: String, text: String): Drawable {
        val avatarColor = ContextCompat.getColor(context, getColorFromUserId(identifier))
        return if (text.isEmpty()) {
            TextDrawable.builder().buildRound("", avatarColor)
        } else {
            val isUserId = MatrixPatterns.isUserId(text)
            val firstLetterIndex = if (isUserId) 1 else 0
            val firstLetter = text[firstLetterIndex].toString().toUpperCase()
            TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .endConfig()
                    .buildRound(firstLetter, avatarColor)
        }
    }

    // PRIVATE API *********************************************************************************


//    private fun getAvatarColor(text: String? = null): Int {
//        var colorIndex: Long = 0
//        if (!text.isNullOrEmpty()) {
//            var sum: Long = 0
//            for (i in 0 until text.length) {
//                sum += text[i].toLong()
//            }
//            colorIndex = sum % AVATAR_COLOR_LIST.size
//        }
//        return AVATAR_COLOR_LIST[colorIndex.toInt()]
//    }

    private fun buildGlideRequest(glideRequest: GlideRequests, avatarUrl: String?): GlideRequest<Drawable> {
        val resolvedUrl = Matrix.getInstance().currentSession!!.contentUrlResolver()
                .resolveThumbnail(avatarUrl, THUMBNAIL_SIZE, THUMBNAIL_SIZE, ContentUrlResolver.ThumbnailMethod.SCALE)

        return glideRequest
                .load(resolvedUrl)
                .apply(RequestOptions.circleCropTransform())
    }


    //Based on riot-web implementation
    @ColorRes
    fun getColorFromUserId(sender: String): Int {
        var hash = 0
        var i = 0
        var chr: Char
        if (sender.isEmpty()) {
            return R.color.username_1
        }
        while (i < sender.length) {
            chr = sender[i]
            hash = (hash shl 5) - hash + chr.toInt()
            hash = hash or 0
            i++
        }
        val cI = Math.abs(hash) % 8 + 1
        return when (cI) {
            1    -> R.color.username_1
            2    -> R.color.username_2
            3    -> R.color.username_3
            4    -> R.color.username_4
            5    -> R.color.username_5
            6    -> R.color.username_6
            7    -> R.color.username_7
            else -> R.color.username_8
        }
    }
}