package au.net.drmic.photos.photoprocessing.utility

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

class DateTimeUtilities {

    companion object {
        fun timestampNow(): Timestamp {
            return Timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        }
    }
}