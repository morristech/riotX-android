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

package im.vector.matrix.android.api.session.room.model

import com.squareup.moshi.Json

/**
 * Represents the membership of a user on a room
 */
enum class Membership(val value: String) {

    NONE("none"),

    @Json(name = "invite")
    INVITE("invite"),

    @Json(name = "join")
    JOIN("join"),

    @Json(name = "knock")
    KNOCK("knock"),

    @Json(name = "leave")
    LEAVE("leave"),

    @Json(name = "ban")
    BAN("ban");

    fun isLeft(): Boolean {
        return this == KNOCK || this == LEAVE || this == BAN
    }

}
