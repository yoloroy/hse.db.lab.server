package com.yoloroy.routing.data

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import java.io.File
import java.util.*

suspend fun PipelineContext<Unit, ApplicationCall>.uploadImage() {
    val destinationFolder = this::class.java.getResource("/public/saved_imgs/")!!.path
    val multipart = call.receiveMultipart()
    var fileName: String? = null
    try {
        multipart.forEachPart { partData ->
            when(partData) {
                is PartData.FileItem ->{
                    fileName = partData.save(destinationFolder)
                }
                else -> Unit
            }
        }

        val imageUrl = "/saved_imgs/$fileName"
        call.respond(HttpStatusCode.OK, imageUrl)
    } catch (ex: Exception) {
        File("$destinationFolder/$fileName").delete()
        call.respond(HttpStatusCode.InternalServerError,"Error")
    }
}

fun PartData.FileItem.save(path: String): String {
    val fileBytes = streamProvider().readBytes()
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    val folder = File(path)
    folder.mkdir()
    println("Path = $path $fileName")
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}
