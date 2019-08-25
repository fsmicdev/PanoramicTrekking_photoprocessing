package au.net.drmic.photos.photoprocessing.repository.entity;

import au.net.drmic.photos.photoprocessing.repository.entity.support.JpaPersistCapable
import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Blob
import java.sql.Date
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@Entity
class Photos : JpaPersistCapable() {

    @Column(nullable = false)
    var ownerUserId: Long? = null

    lateinit var datePhotoWasTaken: Date

    @JsonIgnore
    @Column(nullable = false)
    @Lob
    lateinit var imageOriginal: Blob

    // serialize as data uri instead
    @JsonProperty("imageOriginal")
    fun getIMageOriginalBase64(): String {
        // @TODO: just assuming it is a jpeg. you would need to cater for different media types
        return "data:image/jpeg;base64," + String(Base64.getEncoder().encode(
                imageOriginal.getBytes(0, imageOriginal.length().toInt())))
    }

    @JsonIgnore
    @Column(nullable = false)
    @Lob
    lateinit var imageCroppedStandard: Blob

    // serialize as data uri instead
    @JsonProperty("imageCroppedStandard")
    fun getImageCroppedStandardBase64(): String {
        // @TODO: just assuming it is a jpeg. you would need to cater for different media types
        return "data:image/jpeg;base64," + String(Base64.getEncoder().encode(
                imageCroppedStandard.getBytes(0, imageOriginal.length().toInt())))
    }

    @JsonIgnore
    @Column(nullable = false)
    @Lob
    lateinit var imageCroppedThumbnail: Blob

    // serialize as data uri instead
    @JsonProperty("imageCroppedThumbnail")
    fun getImageCroppedThumbnailBase64(): String {
        // @TODO: just assuming it is a jpeg. you would need to cater for different media types
        return "data:image/jpeg;base64," + String(Base64.getEncoder().encode(
                imageCroppedThumbnail.getBytes(0, imageOriginal.length().toInt())))
    }

    @Column(nullable = false)
    lateinit var dateTimeUpdated: Timestamp

    lateinit var description: String

}
