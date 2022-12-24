package com.yoloroy.routing.view.admin

import com.yoloroy.domain.table.TableStatsModel
import com.yoloroy.lib.util.ResultOf
import com.yoloroy.routing.view.MDTemplate
import kotlinx.html.*

class AdminTemplate(val tables: ResultOf<List<TableStatsModel>>) : MDTemplate() {
    override fun HEAD.apply() {
        script(src = "https://cdn.jsdelivr.net/npm/chart.js") {}
    }

    override fun BODY.apply() {
        script {//language=js
            +"""
                function httpGet(theUrl) {
                    var xmlHttp = new XMLHttpRequest();
                    xmlHttp.open('GET', theUrl, false); // false for synchronous request
                    xmlHttp.send(null);
                    return xmlHttp.responseText;
                }
            """.trimIndent()
        }
        div("container") {
            when (tables) {
                is ResultOf.Success -> {
                    tablesView(tables.value)
                }
                is ResultOf.Error -> {
                    p("mt-4 mb-2") {
                        +"throwable: ${tables.throwable} message: ${tables.message}"
                    }
                }
            }
        }
    }
}
