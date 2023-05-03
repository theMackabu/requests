package dev.themackabu.requests

import java.io.File
import dev.themackabu.requests.log
import cafe.adriel.satchel.Satchel
import dev.themackabu.requests.plugin
import cafe.adriel.satchel.SatchelStorage
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import cafe.adriel.satchel.encrypter.bypass.BypassSatchelEncrypter
import cafe.adriel.satchel.serializer.raw.RawSatchelSerializer

fun database(path: String): SatchelStorage {
    val file = File(plugin.dataFolder.absolutePath + File.separator + path)
    
    log.info("Initialized database $path")
    return Satchel.with(
        storer = FileSatchelStorer(file),
        encrypter = BypassSatchelEncrypter,
        serializer = RawSatchelSerializer
    )
}