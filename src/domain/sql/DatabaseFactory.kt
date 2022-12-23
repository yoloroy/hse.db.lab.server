package com.yoloroy.domain.sql

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException
import java.lang.reflect.InvocationTargetException

object DatabaseFactory {

    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbUrl = appConfig.property("db.jdbcUrl").getString()
    private val dbUser = appConfig.property("db.dbUser").getString()
    private val dbPassword = appConfig.property("db.dbPassword").getString()

    fun connectDev() = Database.connect(hikariDev())

    fun connectRel() = Database.connect(hikariRel())

    private fun hikariDev(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = dbUrl.replace("rel", "template")
            username = dbUser
            password = dbPassword
            isAutoCommit = true
            validate()
        }
        return HikariDataSource(config)
    }

    private fun hikariRel(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPassword
            isAutoCommit = true
            validate()
        }
        return HikariDataSource(config)
    }
}

fun dbStart(): Database {
    val temp = DatabaseFactory.connectDev()
    if (transaction { exec("select exists(SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = 'restaurant_rel')") { it.next(); it.getBoolean("exists") } == true }.also { println("rest_rel exists: $it") }) {
        TransactionManager.closeAndUnregister(temp)
        return DatabaseFactory.connectRel()
    }
    transaction {
        connection.autoCommit = true
        exec("""
            SELECT pid, pg_terminate_backend(pid) 
            FROM pg_stat_activity 
            WHERE datname = 'restaurant_template' AND pid <> pg_backend_pid();""")
        SchemaUtils.createDatabase("restaurant_rel with template restaurant_template")
        connection.autoCommit = false
        connection.autoCommit = true
    }
    TransactionManager.closeAndUnregister(temp)

    return DatabaseFactory.connectRel()
}

fun dbEnd(database: Database) {
    try {
        DatabaseFactory.connectRel()
    } catch (e: Exception) { // TODO
        e.printStackTrace()
        return
    }

    TransactionManager.closeAndUnregister(database)
    val temp = DatabaseFactory.connectDev()
    transaction {
        connection.autoCommit = true
        exec("""
            SELECT pid, pg_terminate_backend(pid) 
            FROM pg_stat_activity 
            WHERE datname = 'restaurant_rel' AND pid <> pg_backend_pid();""")
        SchemaUtils.dropDatabase("restaurant_rel")
        connection.autoCommit = false
        connection.autoCommit = true
    }
    TransactionManager.closeAndUnregister(temp)
}
