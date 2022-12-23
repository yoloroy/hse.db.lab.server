package com.yoloroy.routing.view.client

import com.yoloroy.lib.extensions.now
import com.yoloroy.lib.extensions.plus
import com.yoloroy.lib.extensions.template
import com.yoloroy.routing.view.MDTemplate
import kotlinx.datetime.LocalDateTime
import kotlinx.html.*
import kotlin.time.ExperimentalTime
import kotlin.time.hours

private const val authPhoneEnterDialogId = "authPhoneEnterDialogId"
private const val authPhoneEnterDialogFieldId = "authPhoneEnterDialogFieldId"
private const val authPhoneEnterDialogHeadId = "authPhoneEnterDialogHeadId"
private const val enterPhoneDialogPhoneNumberId = "enterPhoneDialogPhoneNumberId"
private const val authCheckPhoneDialogId = "authCheckPhoneDialogId"
private const val authCheckPhoneDialogFieldId = "authCheckPhoneDialogFieldId"
private const val authCheckPhoneDialogButtonId = "authCheckPhoneDialogButtonId"
private const val checkPhoneDialogSmsTextId = "checkPhoneDialogSmsTextId"

class ClientTemplate : MDTemplate() {

    @ExperimentalTime
    override fun BODY.apply() {
        classes += setOf("bg-light")

        globalScripts()
        clientSelfActions()
        observingTablesForSubscription()
        myBookings()
        authPhoneEnterDialog()
        authCheckPhoneDialog()
        endingScripts()
    }

    private fun BODY.globalScripts() {
        script { // language=js
            +"""
                function getCookieOrNull(name) {
                  let matches = document.cookie.match(new RegExp(
                    '(?:^|; )' + name.replace(/([\.${'$'}?*|{}\(\)\[\]\\\/\+^])/g, '\\${'$'}1') + '=([^;]*)'
                  ));
                  return matches ? decodeURIComponent(matches[1]) : null;
                }

                function add_booking(clientId, tableId, startTime, endTime) {
                    var xhr = new XMLHttpRequest();
                    var url = '/booking/add';
                    xhr.open('POST', url, true);
                    xhr.setRequestHeader('Content-Type', 'application/json');
                    xhr.onreadystatechange = function () {
                        if ((xhr.readyState === 4) * (xhr.status === 200)) {
                            let item = document.createElement('h5');
                            item.innerText = 'Стол#'+tableId+' от:'+startTime+' до:'+endTime;
                            document.getElementById('my_bookings_container').appendChild(item);
                        } else {
                            console.log('vsyo pogano (deleteTable)');
                        }
                    };
                    var data = JSON.stringify({'client_id': clientId, 'table_id': tableId, 'start': startTime, 'end': endTime});
                    xhr.send(data);
                }
            """.trimIndent()
        }
    }

    private fun BODY.clientSelfActions() {
        div("container mt-4 mb-2 p-2") {
            id = "client_self_actions"
            h4("row") { +"Аккаунт" }
            div("row") {
                div("col-md-5 input-group flex-wrap") {
                    div("col-auto form-outline") {
                        input(classes = "form-control") {
                            id = "client_id"
                            readonly = true
                        }
                        label("form-label") {
                            htmlFor = "client_id"
                            +"ID"
                        }
                    }
                    input(classes = "form-control") {
                        id = "client_name"
                        placeholder = "Как нам вас звать?"
                    }
                    button(type = ButtonType.button, classes = "btn btn-primary") {
                        id = "client_reg"
                        onClick = "authEnterPhone();"
                        i("fas fa-user-plus")
                    }
                }
            }
        }
    }

    private fun BODY.authPhoneEnterDialog() {
        dialog("z-index:100") {
            id = authPhoneEnterDialogId
            h4 {
                id = authPhoneEnterDialogHeadId
                +"Вход"
            }
            input(classes = "form-control", type = InputType.tel) {
                id = authPhoneEnterDialogFieldId
                placeholder = "Номер телефона"
            }
            br()
            button {
                id = enterPhoneDialogPhoneNumberId
                onClick = "authCheckPhone();"
                +"Подтвердить"
            }
        }
    }

    private fun BODY.authCheckPhoneDialog() {
        dialog("z-index:100") {
            id = authCheckPhoneDialogId
            h4 { +"Вход" }
            p {
                id = checkPhoneDialogSmsTextId
                +"Смс:"
            }
            input(classes = "form-control", type = InputType.tel) { // TODO ?????
                id = authCheckPhoneDialogFieldId
                placeholder = "Код из Смс"
            }
            br()
            button {
                id = authCheckPhoneDialogButtonId
                onClick = "newUser();"
                +"Подтвердить"
            }
        }
    }

    @ExperimentalTime
    @Suppress("RedundantSemicolon", "CanBeVal", "DEPRECATION")
    private fun BODY.observingTablesForSubscription() {
        div("container mt-4 mb-2") {
            h4("row") { +"Столы на промежуток времени" }
            div("input-group my-2") {
                div("form-outline") {
                    input(classes = "form-control") {
                        id = "start_time"
                        value = LocalDateTime.now().toString()
                    }
                    label("form-label") {
                        htmlFor = "start_time"
                        +"От: yyyy-mm-dd'T'hh:mm:ss"
                    }
                }
                div("form-outline") {
                    input(classes = "form-control") {
                        id = "end_time"
                        value = (LocalDateTime.now() + 1.hours).toString()
                    }
                    label("form-label") {
                        htmlFor = "end_time"
                        +"До: yyyy-mm-dd'T'hh:mm:ss"
                    }
                }
                button(type = ButtonType.button, classes = "btn btn-primary") {
                    id = "observe_tables"
                    attributes["data-mdb-ripple-color"] = "dark"
                    disabled = true
                    onClick = "startTablesObserving();"
                    i("fas fa-user-clock")
                }
            }
        }

        div("container") {
            div {
                id = "tables_view"
                classes += setOf("row", "row-cols-2", "row-cols-md-3", "row-cols-lg-4", "row-cols-xl-5", "row-cols-xxl-6")
            }
        }

        template {
            id = "table_status_card"
            div("card my-2") {
                h5("card-header")
                div("card-body") {
                    p("card-text")
                    button(type = ButtonType.button, classes = "btn btn-outline-primary btn_add_booking") {
                        id = "btn_add_booking"
                        onClick = ";"
                        attributes["data-mdb-ripple-color"] = "dark"
                        disabled = true
                        +"Забронировать"
                    }
                }
            }
        }

        script { //language=js
            +"""
        var socket = null;
        function startTablesObserving() {
            if (socket !== null) {
                socket.close()
            }
            socket = new WebSocket('ws://localhost:8080/tables/status/observe');
            
            var tablesView = document.getElementById('tables_view');
            var tableStatusCardTemplate = document.querySelector('#table_status_card');
            
            var startTimeForm = document.getElementById('start_time');
            var endTimeForm = document.getElementById('end_time');
            
            socket.onopen = function (e) {
                socket.send(startTimeForm.value+','+endTimeForm.value);
                var onTimeUpdate = function(e) {
                    e.preventDefault();
                    socket.send(startTimeForm.value+','+endTimeForm.value);
                }
                startTimeForm.addEventListener('submit', onTimeUpdate);
                endTimeForm.addEventListener('submit', onTimeUpdate);
                
                socket.onmessage = function(statusesJsonString) {
                    var statuses = JSON.parse(statusesJsonString.data);
                
                    tablesView.innerHTML = '';
                    statuses.forEach(function(status) {
                        var item = document.createElement('col');
                        item.appendChild(tableStatusCardTemplate.content.cloneNode(true));
            
                        item.getElementsByClassName('card-header')[0]
                            .innerHTML = 'Стол #' + status.table_id;
                        item.getElementsByClassName('card-text')[0]
                            .innerHTML = status.is_booked ? ('Занят #' + status.booking_id) : 'Свободен';
                        item.getElementsByClassName('btn_add_booking')[0].addEventListener('click', function() {
                            add_booking(document.getElementById('client_id').value,status.table_id,startTimeForm.value,endTimeForm.value);
                        });
                        if (status.is_booked) {
                            item.getElementsByClassName('btn_add_booking')[0].hidden = 'true';
                        } else {
                            if (document.getElementById('client_id').value != '') {
                                item.getElementsByClassName('btn_add_booking')[0].removeAttribute('disabled');
                            }
                        }
                        console.log(item);
            
                        tablesView.appendChild(item);
                    });
                };
            };
        }
        """.trimIndent()
        }
    }

    private fun BODY.myBookings() {
        div("container mt-4 mb-2") {
            h4("row") { +"Мои брони" }
        }

        div("container") {
            id = "my_bookings_container"
        }
    }

    private fun BODY.endingScripts() {
        script { //language=js
            +"""
                var id = getCookieOrNull('id');
                var name = getCookieOrNull('name');
                /** @type {String} */
                var code = null;
                if (id !== null) {
                    document.getElementById('client_id').value = id;
                    document.getElementById('client_name').value = name;
                    document.getElementById('client_name').disabled = true;
                    document.getElementById('client_reg').disabled = true;
                    document.getElementById('client_reg').onclick = '';
                    document.getElementById('start_time').removeAttribute('disabled');
                    document.getElementById('end_time').removeAttribute('disabled');

                    document.getElementById('observe_tables').removeAttribute('disabled');
                    startTablesObserving();
                }

                function authEnterPhone() {
                    const enterPhoneDialog = document.getElementById('$authPhoneEnterDialogId');
                    enterPhoneDialog.showModal();
                }

                function authCheckPhone() {
                    var xhr = new XMLHttpRequest();
                    var url = '/client/check';
                    xhr.open('POST', url, true);
                    xhr.setRequestHeader('Content-Type', 'application/json');
                    xhr.onreadystatechange = function() {
                        const enterPhoneDialog = document.getElementById('$authPhoneEnterDialogId');
                        const checkPhoneDialog = document.getElementById('$authCheckPhoneDialogId');
                        const enterPhoneDialogHead = document.getElementById('$authPhoneEnterDialogHeadId');
                        const checkPhoneDialogSmsText = document.getElementById('$checkPhoneDialogSmsTextId');
                        if ((xhr.readyState === 4) * (xhr.status === 200)) {
                            /** @type {{phone: String, name: String, code: String}} */
                            const smsData = JSON.parse(xhr.responseText);
                            enterPhoneDialog.close();
                            checkPhoneDialogSmsText.textContent = 'Sms, отправленное на номер ' + smsData.phone + ': \'' + smsData.name + ', ваш код для подтверждения входа: ' + smsData.code + '\'';
                            code = smsData.code;
                            checkPhoneDialog.show();
                        } else {
                            enterPhoneDialogHead.value = 'Вход, что-то пошло не так';
                        }
                    };
                    
                    var data = JSON.stringify({
                        'name': document.getElementById('client_name').value,
                        'phone': document.getElementById('$enterPhoneDialogPhoneNumberId').value
                    });
                    xhr.send(data);
                }

                function newUser() {
                    if (document.getElementById('$authCheckPhoneDialogFieldId').value !== code) {
                        const checkPhoneDialogSmsText = document.getElementById('$checkPhoneDialogSmsTextId');
                        if (checkPhoneDialogSmsText.textContent.endsWith('Ошибка!')) checkPhoneDialogSmsText.textContent += '\nОшибка!';
                        return;
                    }
                    var xhr = new XMLHttpRequest();
                    var url = '/client/add';
                    xhr.open('POST', url, true);
                    xhr.setRequestHeader('Content-Type', 'application/json');
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            var userDto = JSON.parse(xhr.responseText);

                            document.cookie = 'id='+userDto.id;
                            document.cookie = 'name='+userDto.name;
                            document.cookie = 'max-age=120000';

                            document.getElementById('client_id').value = userDto.id;
                            document.getElementById('client_name').disabled = true;
                            document.getElementById('client_reg').disabled = true;
                            document.getElementById('client_reg').onclick = '';
                            document.getElementById('start_time').removeAttribute('disabled');
                            document.getElementById('end_time').removeAttribute('disabled');

                            document.getElementById('observe_tables').removeAttribute('disabled');
                            startTablesObserving();
                        }
                        }
                    };
                    
                    var data = JSON.stringify({
                        'name': document.getElementById('client_name').value
                    });
                    xhr.send(data);
                }
            """.trimIndent()
        }
    }
}
