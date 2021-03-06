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

package im.vector.matrix.android.api.failure

import java.io.IOException

/**
 * This class allows to expose different kinds of error to be then handled by the application.
 * As it is a sealed class, you typically use it like that :
 * when(failure) {
 *   is NetworkConnection -> Unit
 *   is ServerError       -> Unit
 *   is Unknown           -> Unit
 * }
 */
sealed class Failure(cause: Throwable? = null) : Throwable(cause = cause) {
    data class Unknown(val throwable: Throwable? = null) : Failure(throwable)
    data class NetworkConnection(val ioException: IOException? = null) : Failure(ioException)
    data class ServerError(val error: MatrixError) : Failure(RuntimeException(error.toString()))

    abstract class FeatureFailure : Failure()

}