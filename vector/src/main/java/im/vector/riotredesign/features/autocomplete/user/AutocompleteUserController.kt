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

package im.vector.riotredesign.features.autocomplete.user

import com.airbnb.epoxy.TypedEpoxyController
import im.vector.matrix.android.api.session.user.model.User
import im.vector.riotredesign.features.autocomplete.AutocompleteClickListener

class AutocompleteUserController : TypedEpoxyController<List<User>>() {

    var listener: AutocompleteClickListener<User>? = null

    override fun buildModels(data: List<User>?) {
        if (data.isNullOrEmpty()) {
            return
        }
        data.forEach { user ->
            autocompleteUserItem {
                id(user.userId)
                userId(user.userId)
                name(user.displayName)
                avatarUrl(user.avatarUrl)
                clickListener { _ ->
                    listener?.onItemClick(user)
                }
            }
        }
    }
}