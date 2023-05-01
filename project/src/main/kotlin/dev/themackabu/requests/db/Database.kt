package dev.themackabu.requests.db

import java.io.File
import dev.themackabu.requests.Main
import cafe.adriel.satchel.Satchel
import cafe.adriel.satchel.SatchelStorage
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import cafe.adriel.satchel.encrypter.bypass.BypassSatchelEncrypter
import cafe.adriel.satchel.serializer.raw.RawSatchelSerializer

class Database(path: String) {
    private lateinit var db: SatchelStorage
    private var path: String
    private var file: File

    init {
        this.file = File(Main.getPlugin().dataFolder.absolutePath + File.separator + path)
        this.path = path
    }

    fun init(): SatchelStorage {
        this.db = Satchel.with(
            storer = FileSatchelStorer(this.file),
            encrypter = BypassSatchelEncrypter,
            serializer = RawSatchelSerializer
        )

        return this.db
    }
}