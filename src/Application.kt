package com.yoloroy

import com.yoloroy.domain.booking.BookingRepository
import com.yoloroy.domain.client.ClientRepository
import com.yoloroy.domain.sql.dbEnd
import com.yoloroy.domain.sql.dbStart
import com.yoloroy.domain.sql.repository.SQLBookingRepository
import com.yoloroy.domain.sql.repository.SQLClientRepository
import com.yoloroy.domain.sql.repository.SQLTableRepository
import com.yoloroy.domain.sql.repository.SQLTablesStatusRepository
import com.yoloroy.domain.table.TableRepository
import com.yoloroy.domain.tables_status.TablesStatusRepository
import com.yoloroy.model.BookingService
import com.yoloroy.model.ClientService
import com.yoloroy.model.TableService
import com.yoloroy.model.TablesStatusService
import com.yoloroy.routing.data.*
import com.yoloroy.routing.view.admin.adminView
import com.yoloroy.routing.view.client.clientView
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.exposed.sql.Database
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@ExperimentalCoroutinesApi
@Suppress("unused", "UNUSED_PARAMETER") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(
    testing: Boolean = false,
    clientRepository: ClientRepository = SQLClientRepository(),
    bookingRepository: BookingRepository = SQLBookingRepository(),
    tablesStatusRepository: TablesStatusRepository = SQLTablesStatusRepository(),
    tableRepository: TableRepository = SQLTableRepository()
) {
    lateinit var database: Database
    if (!testing)
        database = dbStart()
    val clientService = ClientService(clientRepository)
    val bookingService = BookingService(bookingRepository)
    val tablesStatusService = TablesStatusService(tablesStatusRepository, bookingService)
    val tableService = TableService(tableRepository)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        // admin
        get("createdb") { database = dbStart(); call.respond("ok") }
        get("enddb") { dbEnd(database); call.respond("ok") }

        // view
        get("clientView") { clientView() }
        get("adminView") { adminView(tableService) }

        // data
        post("client/add") { addClient(clientService) }
        post("client/upd/discount") { updateClientDiscount(clientService) }
        post("client/upd/ban") { updateClientBan(clientService) }

        post("booking/add") { addBooking(bookingService) }
        post("booking/cancel") { cancelBooking(bookingService) }

        post("table/add") { addTable(tableService) }
        post("table/delete") { deleteTable(tableService) }

        webSocket ("tables/status/observe") { observeTablesStatus(tablesStatusService) }

        static("") {
            resources("public")
        }
    }
}
