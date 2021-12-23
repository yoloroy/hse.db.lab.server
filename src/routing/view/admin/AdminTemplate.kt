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
                    xmlHttp.open( 'GET', theUrl, false ); // false for synchronous request
                    xmlHttp.send( null );
                    return xmlHttp.responseText;
                }
            """.trimIndent()
        }
        div("container") {
            div("input-group  mt-4 mb-2") {
                button(type = ButtonType.button, classes = "btn btn-danger") {
                    id = "dbConnectionSwitchButton"
                    attributes["switchmode"] = "enddb"
                    onClick = // language=js
                        """
                        let button = getElementById('${id}');
                        if (button.switchmode === 'createdb') {
                            httpGet('/createdb');
                            button.switchmode = 'enddb';
                            button.innerText = 'End DB';
                            button.classList.remove('btn-success');
                            button.classList.add('btn-danger');
                        } else {
                            httpGet('/enddb');
                            button.switchmode = 'createdb';
                            button.innerText = 'Create DB';
                            button.classList.remove('btn-danger');
                            button.classList.add('btn-success');
                        }
                
                """.trimIndent()
                    +"End DB"
                }
            }
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
