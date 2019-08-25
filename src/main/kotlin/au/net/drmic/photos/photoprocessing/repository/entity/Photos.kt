package au.net.drmic.photos.photoprocessing.repository.entity;

import au.net.drmic.photos.photoprocessing.repository.entity.support.JpaPersistCapable
import java.sql.Blob
import java.sql.Date
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Photos : JpaPersistCapable() {

    @Column(nullable = false)
    var ownerUserId: Long? = null

    lateinit var datePhotoWasTaken: Date

    @Column(nullable = false)
    lateinit var imageOriginal: Blob

    @Column(nullable = false)
    lateinit var imageCroppedStandard: Blob

    @Column(nullable = false)
    lateinit var imageCroppedThumbnail: Blob

    @Column(nullable = false)
    lateinit var dateTimeUpdated: Timestamp

    lateinit var description: String

}
