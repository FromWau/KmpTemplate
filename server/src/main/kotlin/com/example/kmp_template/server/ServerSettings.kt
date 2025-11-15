package com.example.kmp_template.server

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import java.io.File

class ServerSettings() {
    private val config: Config get() = ConfigFactory.load()

    val dataDir: File
        get() {
            val dataPath = config.propertyOrNull("data.dir")
                ?: System.getenv("DATA_DIR")
                ?: error("No data directory specified. Set data.dir in the config or set DATA_DIR environment variable.")

            val dataDir = File(dataPath)

            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }

            return dataDir
        }


    private fun Config.propertyOrNull(path: String): String? = try {
        this.getString(path)
    } catch (_: ConfigException.Missing) {
        null
    }
}
