package com.yoloroy.routing.view

import io.ktor.html.*
import kotlinx.html.*

open class MDTemplate : Template<HTML> {
    final override fun HTML.apply() {
        head {
            meta(charset = "UTF-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1, shrink-to-fit=no")
            meta(content = "ie=edge") { attributes["http-equiv"] = "x-ua-compatible" }
            link(rel = "icon", href = "img/mdb-favicon.ico", type = "image/x-icon")
            link(rel = "stylesheet", href = "https://use.fontawesome.com/releases/v5.15.2/css/all.css")
            link(rel = "stylesheet", href = "https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700;900&amp;display=swap")
            link(rel = "stylesheet", href = "css/mdb.min.css")
            link(rel = "stylesheet", href = "css/popup.css")

            apply()
        }
        body {
            apply()

            script("text/javascript", src = "js/mdb.min.js") {}
        }
    }

    protected open fun HEAD.apply() {}

    protected open fun BODY.apply() {}
}
