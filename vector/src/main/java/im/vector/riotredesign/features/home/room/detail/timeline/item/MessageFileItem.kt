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

package im.vector.riotredesign.features.home.room.detail.timeline.item

import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.riotredesign.R

@EpoxyModelClass(layout = R.layout.item_timeline_event_base)
abstract class MessageFileItem : AbsMessageItem<MessageFileItem.Holder>() {

    @EpoxyAttribute
    var filename: CharSequence = ""
    @EpoxyAttribute
    @DrawableRes
    var iconRes: Int = 0
    @EpoxyAttribute
    override lateinit var informationData: MessageInformationData
    @EpoxyAttribute
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.fileLayout.renderSendState()
        holder.filenameView.text = filename
        holder.fileImageView.setImageResource(iconRes)
        holder.filenameView.setOnClickListener(clickListener)
        holder.filenameView.paintFlags = (holder.filenameView.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
    }

    override fun getStubType(): Int = STUB_ID

    class Holder : AbsMessageItem.Holder() {
        override fun getStubId(): Int = STUB_ID

        val fileLayout by bind<ViewGroup>(R.id.messageFileLayout)
        val fileImageView by bind<ImageView>(R.id.messageFileImageView)
        val filenameView by bind<TextView>(R.id.messageFilenameView)

    }

    companion object {
        private val STUB_ID = R.id.messageContentFileStub
    }

}