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

package im.vector.matrix.android.internal.session.room.membership

import im.vector.matrix.android.api.session.events.model.EventType
import im.vector.matrix.android.api.session.events.model.toModel
import im.vector.matrix.android.api.session.room.model.RoomMember
import im.vector.matrix.android.internal.database.mapper.ContentMapper
import im.vector.matrix.android.internal.database.model.ChunkEntity
import im.vector.matrix.android.internal.database.model.EventEntity
import im.vector.matrix.android.internal.database.model.EventEntityFields
import im.vector.matrix.android.internal.database.model.RoomEntity
import im.vector.matrix.android.internal.database.query.findIncludingEvent
import im.vector.matrix.android.internal.database.query.next
import im.vector.matrix.android.internal.database.query.prev
import im.vector.matrix.android.internal.database.query.where
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmQuery

internal class SenderRoomMemberExtractor(private val roomId: String) {

    fun extractFrom(event: EventEntity, realm: Realm = event.realm): RoomMember? {
        val sender = event.sender ?: return null
        // If the event is unlinked we want to fetch unlinked state events
        val unlinked = event.isUnlinked
        val roomEntity = RoomEntity.where(realm, roomId = roomId).findFirst() ?: return null
        val chunkEntity = ChunkEntity.findIncludingEvent(realm, event.eventId)
        val content = when {
            chunkEntity == null   -> null
            event.stateIndex <= 0 -> baseQuery(chunkEntity.events, sender, unlinked).next(from = event.stateIndex)?.prevContent
            else                  -> baseQuery(chunkEntity.events, sender, unlinked).prev(since = event.stateIndex)?.content
        }

        val fallbackContent = content
                       ?: baseQuery(roomEntity.untimelinedStateEvents, sender, unlinked).prev(since = event.stateIndex)?.content

        return ContentMapper.map(fallbackContent).toModel()
    }

    private fun baseQuery(list: RealmList<EventEntity>,
                          sender: String,
                          isUnlinked: Boolean): RealmQuery<EventEntity> {
        return list
                .where()
                .equalTo(EventEntityFields.STATE_KEY, sender)
                .equalTo(EventEntityFields.TYPE, EventType.STATE_ROOM_MEMBER)
                .equalTo(EventEntityFields.IS_UNLINKED, isUnlinked)
    }

}