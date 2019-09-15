package au.net.drmic.photos.photoprocessing.repository.entity;

import au.net.drmic.photos.photoprocessing.model.PhotoType
import au.net.drmic.photos.photoprocessing.repository.entity.support.JpaPersistCapable
import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Blob
import java.sql.Date
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*

@Entity
class Photos : JpaPersistCapable() {

    @Column(nullable = false)
    var ownerUserId: Long? = null

    lateinit var datePhotoWasTaken: Date

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit var photoType: PhotoType

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

    lateinit var description: String

    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.EAGER, mappedBy = "photo")
    private val photoTags = mutableListOf<PhotosTag>()

    fun getPhotoTags(): List<PhotosTag> {
       return photoTags
    }

}
