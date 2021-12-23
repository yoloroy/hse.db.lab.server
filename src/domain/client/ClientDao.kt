package com.yoloroy.domain.client

import com.yoloroy.domain.sql.models.ClientRow
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

object ClientDao {

    fun add(name: String, discount: Double, ban: Boolean): ClientRow? = transaction {
        exec("select id, name, discount, ban from client_add('$name', $discount, $ban)") {
            require(it.next())
            it.getClient()
        }
    }

    fun setBan(id: Int, ban: Boolean): Boolean? = transaction {
        exec("select * from client_ban_set($id, $ban)") {
            require(it.next())
            it.getBoolean("result")
                .also { isSuccess -> require(isSuccess) }
        }
    }

    fun setDiscount(id: Int, discount: Double): Boolean? = transaction {
        exec("select * from client_discount_set($id, $discount") {
            require(it.next())
            it.getBoolean("result")
                .also { isSuccess -> require(isSuccess) }
        }
    }
}

private fun ResultSet.getClient() = ClientRow(
    getInt("id"),
    getString("name"),
    getDouble("discount"),
    getBoolean("ban")
)
