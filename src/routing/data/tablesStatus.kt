package com.yoloroy.routing.data

import com.yoloroy.domain.tables_status.TableStatusModel
import com.yoloroy.lib.extensions.receivePairOfDates
import com.yoloroy.lib.util.ResultOf
import com.yoloroy.model.TablesStatusService
import com.yoloroy.routing.data.models.TableStatusDto
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalCoroutinesApi
suspend fun DefaultWebSocketServerSession.observeTablesStatus(tablesStatusService: TablesStatusService) {
    val (start, end) = incoming.receive().receivePairOfDates()

    val statusesFlow = tablesStatusService.subscribeOnInterval(start..end)
    val job = launch {
        statusesFlow
            .filterIsInstance<ResultOf.Success<List<TableStatusModel>>>()
            .map { it.value }
            .map { list -> list.map { TableStatusDto(it) } }
            .cancellable()
            .collect {
                outgoing.send(Frame.Text(Json.encodeToString(it)))
            }
    }

    for (frame in incoming) when(frame) {
        is Frame.Close -> job.cancel()
        else -> Unit
    }
}
