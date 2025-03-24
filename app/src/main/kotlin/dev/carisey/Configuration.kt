package dev.carisey

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceOrFileSource

data class App(
    val foo: String,
)

data class Configuration(
    val app: App,
) {
    companion object {
        @OptIn(ExperimentalHoplite::class)
        fun load(): Configuration =
            ConfigLoaderBuilder
                .default()
                .withExplicitSealedTypes("_type")
                .addResourceOrFileSource("/application.conf")
                .build()
                .loadConfigOrThrow<Configuration>()
    }
}
