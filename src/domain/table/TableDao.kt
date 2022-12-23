package com.yoloroy.domain.table

import com.yoloroy.domain.sql.models.TableHoursUsageRow
import com.yoloroy.domain.sql.models.TableMonthsUsageRow
import com.yoloroy.domain.sql.models.TableRow
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

object TableDao {
    fun add(imageUrl: String?, minimumCheck: Double, capacity: Int) = transaction {
        exec("select * from table_add($minimumCheck, $capacity, '$imageUrl')") {
            it.next()
            it.getTable()
        }
    }

    fun update(id: Int, minimumCheck: Double?, capacity: Int?) = transaction {
        exec("select * from table_update($id, $minimumCheck, $capacity)") {
            it.next()
            it.getTable()
        }
    }

    fun getAll() = transaction {
        exec("select * from table_get_all()") {
            sequence {
                while (it.next()) {
                    yield(it.getTable())
                }
            }.toList()
        }
    }

    fun delete(id: Int) = transaction {
        exec("select * from table_delete($id)") {
            it.next()
            it.getTable()
        }
    }

    fun getStats() = transaction {
        val tables = getAll() ?: return@transaction null
        val monthsStats = getMonthsUsage() ?: return@transaction null
        val hoursStats = getHoursUsage() ?: return@transaction null

        return@transaction Triple(tables, monthsStats, hoursStats)
    }

    private fun getHoursUsage() = transaction {
        exec("select * from tables_get_average_usage_per_hour()") {
            sequence {
                while (it.next()) {
                    yield(it.getTableHoursUsage())
                }
            }.toList()
        }
    }

    private fun getMonthsUsage() = transaction {
        exec("select * from tables_get_monthly_usage_stats()") {
            sequence {
                while (it.next()) {
                    yield(it.getTableMonthsUsage())
                }
            }.toList()
        }
    }
}

private fun ResultSet.getTableHoursUsage() = TableHoursUsageRow(
    getInt("table_id"),
    getInt("hour"),
    getDouble("average_usage")
)

private fun ResultSet.getTableMonthsUsage() = TableMonthsUsageRow(
    getInt("table_id"),
    getInt("mnth"),
    getInt("usage_count")
)

private fun ResultSet.getTable() = TableRow(
    getInt("id"),
    getDouble("minimum_check"),
    getInt("human_capacity"),
    getString("image_url")
)
