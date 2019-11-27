package au.net.drmic.photos.photoprocessing.repository.entity;

import au.net.drmic.commonsupport.persistence.jpa.JpaPersistCapable
import au.net.drmic.photos.photoprocessing.model.PhotoType
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Blob
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import kotlin.streams.toList

@JsonIgnoreProperties(
        value = arrayOf( "imageOriginal", "imageCroppedThumbnail", "imageCroppedStandard", "photoTags" ),
        allowSetters = true,
        allowGetters = false)
@Entity
class Photos : JpaPersistCapable() {

    @Column(nullable = false)
    var ownerUserId: Long? = null

    lateinit var datePhotoWasTaken: LocalDate

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit var photoType: PhotoType

    @Column(nullable = false)
    @Lob
    private lateinit var imageOriginal: Blob

    @JsonIgnore // ----- N.B. disable serializing this field, by default -----
    fun getImageOriginal(): Blob? {
        return imageOriginal
    }

    fun setImageOriginal(imageOriginal : Blob) {
        this.imageOriginal = imageOriginal
    }

    @Column(nullable = false)
    @Lob
    private lateinit var imageCroppedStandard: Blob

    // @JsonIgnore // ----- N.B. disable serializing this field, by default -----
    fun getImageCroppedStandard(): Blob? {
        return imageCroppedStandard
    }

    fun setImageCroppedStandard(imageCroppedStandard : Blob) {
        this.imageCroppedStandard = imageCroppedStandard
    }

    @Column(nullable = false)
    @Lob
    private lateinit var imageCroppedThumbnail: Blob

    @JsonIgnore // ----- N.B. disable serializing this field, by default -----
    fun getImageCroppedThumbnail(): Blob? {
        return imageCroppedThumbnail
    }

    fun setImageCroppedThumbnail(imageCroppedThumbnail : Blob) {
        this.imageCroppedThumbnail = imageCroppedThumbnail
    }

    private lateinit var imageOriginalBase64: String
    private lateinit var imageCroppedStandardBase64: String
    private lateinit var imageCroppedThumbnailBase64: String

    // ----- N.B. serialize as data uri instead -----
    @JsonProperty("imageOriginalBase64")
    fun getImageOriginalBase64(): String {
        // @TODO: just assuming it is a jpeg. you would need to cater for different media types
        return "data:image/jpeg;base64," + String(Base64.getEncoder().encode(
                imageOriginal.getBytes(0, imageOriginal.length().toInt())))
    }

    // ----- serialize as data uri instead -----
    @JsonProperty("imageCroppedStandardBase64")
    fun getImageCroppedStandardBase64(): String {
        // @TODO: just assuming it is a jpeg. you would need to cater for different media types
        return "data:image/jpeg;base64," + String(Base64.getEncoder().encode(
                imageCroppedStandard.getBytes(0, imageOriginal.length().toInt())))
    }

    // ----- serialize as data uri instead -----
    @JsonProperty("imageCroppedThumbnailBase64")
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

    @Transient
    private lateinit var photoTagsStrings: List<String>

    @JsonProperty("photoTagsStrings")
    fun getPhotoTagsStrings(): List<String> {
        return photoTags.stream().map(PhotosTag::tag).map(Tag::tagWord).toList()
    }

}
