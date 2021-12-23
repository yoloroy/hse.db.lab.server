package com.yoloroy.domain.sql.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.insert

object Clients : IntIdTable("clients") {
    val name = varchar("name", length = 30)
    val discount = double("discount")
    val ban = bool("ban")

    override val primaryKey = PrimaryKey(id)
}

object Tables : IntIdTable("tables") {
    val minimumCheck = double("minimum_check")
    val humanCapacity = integer("human_capacity")

    override val primaryKey = PrimaryKey(id)
}

object Bookings : IntIdTable("bookings") {
    val client = reference("client_id", Clients)
    val table = reference("table_id", Tables)
    val start = datetime("start")
    val end = datetime("end")

    override val primaryKey = PrimaryKey(id, client, table)
}

object TablesStatus : IntIdTable("tables_status") {
    val table = reference("table_id", Tables)
    val booking = reference("booking_id", Bookings)
    val time = datetime("time")
    val value = integer("value")

    override val primaryKey = PrimaryKey(id, table, booking)
}
