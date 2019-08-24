package au.net.drmic.photos.photoprocessing.utility

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KotLogger {

    companion object {
        inline fun <reified T> logger(): Logger {
            return LoggerFactory.getLogger(T::class.java)
        }
    }

}