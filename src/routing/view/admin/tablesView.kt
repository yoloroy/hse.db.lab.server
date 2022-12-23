package com.yoloroy.routing.view.admin

import com.yoloroy.domain.table.TableStatsModel
import com.yoloroy.lib.extensions.popup
import kotlinx.datetime.Month
import kotlinx.html.*

private object UiIds {
    const val tablesTableId = "tables_table"

    const val popupViewId = "popup_view"
    const val popupChartId = "popup_chart"
}

private object TemplateRowIds {
    const val templateId = "table_row_template"

    const val id = "table_row_id"
    const val minimumCheck = "table_row_minimum_check"
    const val clientCapacity = "table_row_client_capacity"
    const val hoursStats = "table_row_hours_stats"
    const val monthsStats = "table_row_months_stats"
    const val imageView = "table_row_iveage_view"

    const val formImageUploadId = "form_image_upload"
    const val formImageViewId = "form_image_view"
    const val formMinimumCheckId = "form_minimum_check"
    const val formClientCapacityId = "form_client_capacity"
}

fun FlowContent.tablesView(tables: List<TableStatsModel>) = div("mt-4 mb-2") {
    tableActions()
    tablesTable(tables)
    statsPopup()
    tablesScript()
}

private fun FlowContent.statsPopup() = popup(
    background = {
        id = UiIds.popupViewId
        onClick = "hidePopup();"
        hidden = true
    },
    content = {
        canvas { id = UiIds.popupChartId }
    }
)

private fun FlowContent.tablesTable(tables: List<TableStatsModel>) {
    table("table") {
        thead {
            tr {
                th(ThScope.col) { +"#" }
                th(ThScope.col) { +"Table Image" }
                th(ThScope.col) { +"Minimum Check" }
                th(ThScope.col) { +"Client Capacity" }
                th(ThScope.col) { +"Hours Stats" }
                th(ThScope.col) { +"Months Stats" }
            }
        }
        tbody {
            id = UiIds.tablesTableId

            tables.sortedBy { it.id }.forEach {
                tableRow(it)
            }

            tr {
                th(ThScope.row) {
                    a {
                        onClick = "addTable(" +
                                "document.getElementById('${TemplateRowIds.formImageViewId}').src," +
                                "document.getElementById('${TemplateRowIds.formMinimumCheckId}').value," +
                                "document.getElementById('${TemplateRowIds.formClientCapacityId}').value" +
                                ");"
                        role = "button"
                        style = "color: #0d6efd;"
                        i("fas fa-plus-circle") {
                            attributes["data-mdb-ripple-color"] = "dark"
                        }
                    }
                }
                td {
                    form {
                        id = TemplateRowIds.formImageUploadId
                        fileInput(name = "photo") {
                            accept = "image/*"
                        }
                        submitInput()
                    }
                    img {
                        id = TemplateRowIds.formImageViewId
                        width = "48"
                        height = "48"
                        hidden = true
                    }
                    script {
                        // language=JavaScript
                        +"""
                        var formImageUpload = document.getElementById('${TemplateRowIds.formImageUploadId}');
                        var formImageView = document.getElementById('${TemplateRowIds.formImageViewId}');
                        formImageUpload.onsubmit = function() {
                            var formData = new FormData(formImageUpload);
                            formData.append('file', formImageUpload.children[0].value);
                            var xhr = new XMLHttpRequest();
                            xhr.open('POST', '/file/image', true);
                            xhr.send(formData);
                            xhr.onreadystatechange = function () {
                                if (((xhr.readyState === 4) * (xhr.status === 200)) !== 1) return;
                                const imagePath = xhr.responseText;
                                formImageUpload.hidden = true;
                                formImageView.hidden = false;
                                formImageView.src = imagePath;
                            }
                            // To avoid actual submission of the form
                            return false;
                        }
                        """.trimIndent()
                    }
                }
                td {
                    input(type = InputType.number, classes = "form-control rounded") {
                        id = TemplateRowIds.formMinimumCheckId
                    }
                }
                td {
                    input(type = InputType.number, classes = "form-control rounded") {
                        id = TemplateRowIds.formClientCapacityId
                    }
                }
            }
        }
    }
}

private fun TBODY.tableRow(table: TableStatsModel) {
    tr {
        th(ThScope.row) {
            id = TemplateRowIds.id
            +"${table.id}"
        }
        td {
            id = TemplateRowIds.imageView
            img {
                table.imageUrl?.let { src = it }
                width = "48"
                height = "48"
            }
        }
        td {
            id = TemplateRowIds.minimumCheck
            +"${table.minimumCheck}"
        }
        td {
            id = TemplateRowIds.clientCapacity
            +"${table.humanCapacity}"
        }
        td {
            id = TemplateRowIds.hoursStats
            a {
                role = "button"
                style = "color: rgb(57, 192, 237);"
                onClick = "hoursPopup(${table.hoursUsage.values.joinToString(",","[","]")});"
                i("fas fa-clock") {
                    attributes["data-mdb-ripple-color"] = "dark"
                }
            }
        }
        td {
            id = TemplateRowIds.monthsStats
            a {
                role = "button"
                style = "color: rgb(57, 192, 237);"
                onClick = "monthsPopup(${table.monthlyUsage.values.joinToString(",","[","]")});"
                i("fas fa-calendar-day") {
                    attributes["data-mdb-ripple-color"] = "dark"
                }
            }
        }
        td {
            a {
                role = "button"
                style = "color: rgb(255, 178, 26);"
                onClick = "deleteTable(${table.id});"
                i("fas fa-minus-circle") {
                    attributes["data-mdb-ripple-color"] = "dark"
                }
            }
        }
    }
}

private fun FlowContent.tablesScript() = script {
    // language=js
    @Suppress("DEPRECATION")
    +"""
        var chart = null;
    """.trimIndent()
}

private fun FlowContent.tableActions() = script {
    val hoursLabels = (1..24).joinToString("', '", "'", "'")
    val monthsLabels = (1..12).map { Month.of(it) }.joinToString("', '", "'", "'")
    //language=js
    @Suppress("DEPRECATION")
    +"""
        function hoursPopup(values) {
            let labels = [$hoursLabels];
            let data = {
                labels: labels,
                datasets: [{
                    label: 'Почасовая статистика использования стола',
                    backgroundColor: ['#0e2a38','#0f2e3e','#134','#468','#f64','#ff913c','#fb3','#ffc146','#ffc757','#eeb668','#ffc757','#eeb668','#ffc757','#eeb668','#ffc757','#eeb668','#ffc146','#fba145','#f78044','#803c4d','#134','#0f2e3e','#0e2a38','#0d2633'
                    ],
                    borderColor: '#eee',
                    data: values,
                }]
            };
            if(chart){chart.destroy();}
            chart = new Chart(
                document.getElementById('${UiIds.popupChartId}'),
                {
                    type: 'polarArea',
                    data: data,
                }
            );
            showPopup();
        }

        function monthsPopup(values) {
            let labels = [$monthsLabels];
            let data = {
                labels: labels,
                datasets: [{
                    label: 'Почасовая статистика использования стола',
                    backgroundColor: [
                        '#0cf', '#27ccc8', // winter
                        '#73cc5a', '#9c2', '#b3ca28', // spring
                        '#ccc734', '#e6c43d', '#ffc146', // summer
                        '#fd4', '#ff8000', '#af906f', // autumn
                        '#58aeb7' // winter
                    ],
                    borderColor: 'rgb(255, 99, 132)',
                    data: values,
                }]
            };
            if(chart){chart.destroy();}
            chart = new Chart(
                document.getElementById('${UiIds.popupChartId}'),
                {
                    type: 'bar',
                    data: data,
                    options: {
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    },
                }
            );
            showPopup();
        }

        function showPopup() {
            document.getElementById('${UiIds.popupViewId}').hidden = false
        }

        function hidePopup() {
            document.getElementById('${UiIds.popupViewId}').hidden = true
        }

        function deleteTable(tableId) {
            var xhr = new XMLHttpRequest();
            var url = '/table/delete';
            xhr.open('POST', url, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.onreadystatechange = function () {
                if ((xhr.readyState === 4) * (xhr.status === 200)) {
                    location.reload();
                } else {
                    console.log('vsyo pogano (deleteTable)');
                }
            };
            var data = JSON.stringify({'id': tableId});
            xhr.send(data);
        }

        function addTable(imageUrl, minimumCheck, clientCapacity) {
            var xhr = new XMLHttpRequest();
            var url = '/table/add';
            xhr.open('POST', url, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.onreadystatechange = function () {
                if ((xhr.readyState === 4) * (xhr.status === 200)) {
                    location.reload();
                } else {
                    console.log('vsyo pogano (deleteTable)');
                }
            };
            var data = JSON.stringify({'image_url': imageUrl, 'minimum_check': minimumCheck, 'client_capacity': clientCapacity});
            xhr.send(data);
        }
    """.trimIndent()
    // language=js
}
