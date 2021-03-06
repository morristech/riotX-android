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
package im.vector.riotredesign.features.home.room.detail.timeline.action

import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import im.vector.matrix.android.api.session.Session
import im.vector.riotredesign.core.platform.VectorViewModel
import org.koin.android.ext.android.get

/**
 * Quick reactions state, it's a toggle with 3rd state
 */
enum class TriggleState {
    NONE,
    FIRST,
    SECOND
}

data class QuickReactionState(
        val agreeTrigleState: TriggleState,
        val likeTriggleState: TriggleState,
        /** Pair of 'clickedOn' and current toggles state*/
        val selectionResult: Pair<String, List<String>>? = null,
        val eventId: String) : MvRxState

/**
 * Quick reaction view model
 * TODO: configure initial state from event
 */
class QuickReactionViewModel(initialState: QuickReactionState) : VectorViewModel<QuickReactionState>(initialState) {


    fun toggleAgree(isFirst: Boolean) = withState {
        if (isFirst) {
            setState {
                val newTriggle = if (it.agreeTrigleState == TriggleState.FIRST) TriggleState.NONE else TriggleState.FIRST
                copy(
                        agreeTrigleState = newTriggle,
                        selectionResult = Pair(agreePositive, getReactions(this, newTriggle, null))
                )
            }
        } else {
            setState {
                val newTriggle = if (it.agreeTrigleState == TriggleState.SECOND) TriggleState.NONE else TriggleState.SECOND
                copy(
                        agreeTrigleState = agreeTrigleState,
                        selectionResult = Pair(agreeNegative, getReactions(this, newTriggle, null))
                )
            }
        }
    }

    fun toggleLike(isFirst: Boolean) = withState {
        if (isFirst) {
            setState {
                val newTriggle = if (it.likeTriggleState == TriggleState.FIRST) TriggleState.NONE else TriggleState.FIRST
                copy(
                        likeTriggleState = newTriggle,
                        selectionResult = Pair(likePositive, getReactions(this, null, newTriggle))
                )
            }
        } else {
            setState {
                val newTriggle = if (it.likeTriggleState == TriggleState.SECOND) TriggleState.NONE else TriggleState.SECOND
                copy(
                        likeTriggleState = newTriggle,
                        selectionResult = Pair(likeNegative, getReactions(this, null, newTriggle))
                )
            }
        }
    }

    private fun getReactions(state: QuickReactionState, newState1: TriggleState?, newState2: TriggleState?): List<String> {
        return ArrayList<String>(4).apply {
            when (newState2 ?: state.likeTriggleState) {
                TriggleState.FIRST -> add(likePositive)
                TriggleState.SECOND -> add(likeNegative)
                else -> {
                }
            }
            when (newState1 ?: state.agreeTrigleState) {
                TriggleState.FIRST -> add(agreePositive)
                TriggleState.SECOND -> add(agreeNegative)
                else -> {
                }
            }
        }
    }


    companion object : MvRxViewModelFactory<QuickReactionViewModel, QuickReactionState> {

        val agreePositive = "👍"
        val agreeNegative = "👎"
        val likePositive = "🙂"
        val likeNegative = "😔"

        fun getOpposite(reaction: String): String? {
            return when (reaction) {
                agreePositive -> agreeNegative
                agreeNegative -> agreePositive
                likePositive -> likeNegative
                likeNegative -> likePositive
                else -> null
            }
        }

        override fun initialState(viewModelContext: ViewModelContext): QuickReactionState? {
            // Args are accessible from the context.
            // val foo = vieWModelContext.args<MyArgs>.foo
            val currentSession = viewModelContext.activity.get<Session>()
            val parcel = viewModelContext.args as MessageActionsBottomSheet.ParcelableArgs
            val event = currentSession.getRoom(parcel.roomId)?.getTimeLineEvent(parcel.eventId)
                    ?: return null
            var agreeTriggle: TriggleState = TriggleState.NONE
            var likeTriggle: TriggleState = TriggleState.NONE
            event.annotations?.reactionsSummary?.forEach {
                //it.addedByMe
                if (it.addedByMe) {
                    if (agreePositive == it.key) {
                        agreeTriggle = TriggleState.FIRST
                    } else if (agreeNegative == it.key) {
                        agreeTriggle = TriggleState.SECOND
                    }

                    if (likePositive == it.key) {
                        likeTriggle = TriggleState.FIRST
                    } else if (likeNegative == it.key) {
                        likeTriggle = TriggleState.SECOND
                    }
                }
            }
            return QuickReactionState(agreeTriggle, likeTriggle, null, event.root.eventId ?: "")
        }
    }
}