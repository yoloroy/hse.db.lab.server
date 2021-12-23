package com.yoloroy.lib.extensions

import kotlinx.html.*

class Template(consumer: TagConsumer<*>) : HTMLTag("template", consumer, emptyMap(), inlineTag = false, emptyTag = false), HtmlBlockTag

inline fun FlowContent.template(crossinline block: Template.() -> Unit) {
    Template(consumer).visit(block)
}

inline fun FlowContent.popup(crossinline background: DIV.() -> Unit = {}, crossinline content: DIV.() -> Unit = {}) =
    div("b-popup") {
        background()
        div("b-popup-content", content)
    }
