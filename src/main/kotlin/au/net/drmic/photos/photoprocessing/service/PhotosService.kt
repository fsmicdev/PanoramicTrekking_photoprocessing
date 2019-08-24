package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.model.PhotoSize
import au.net.drmic.photos.photoprocessing.repository.PhotosRepository
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.utility.KotLogger
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.*
import javax.sql.rowset.serial.SerialBlob
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException



@Service
class PhotosService {

    val logger = KotLogger.logger<PhotosService>()

    @Autowired
    lateinit var photosRepository: PhotosRepository

    fun saveFileUploadPhoto(imageOriginal: MultipartFile, ownerUserId: Long, datePhotoWasTaken: Date,
                            description: String): Photos {
        val photos = Photos()

        photos.ownerUserId = ownerUserId
        photos.datePhotoWasTaken = java.sql.Date(datePhotoWasTaken.time)
        photos.imageOriginal = SerialBlob(imageOriginal.bytes)
        // @TODO: Crop transform to standard size and set photos.imageCroppedStandard
        // @TODO: Crop transform to thumbnail size and set photos.imageCroppedThumbnail
        photos.dateTimeUpdated = Timestamp(Date().time)
        photos.description = description

        return photosRepository.save(photos)
    }

    /**
     * Adapted from
     * https://github.com/maltesander/java-image-scaling-thumbnail/blob/master/src/main/java/com/tutorialacademy/img/imgscalr/ImageScaler.java
     */
    @Throws(IOException::class)
    fun scale(image: BufferedImage, size: PhotoSize, imageName: String, imageType: String): File {
        val res = size.toImageResolution()

        var scaledImage: BufferedImage? = null

        if (res.numPixels > 0) { // via number of pixels
            scaledImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, res.numPixels, Scalr.OP_ANTIALIAS)
        } else if (res.width > 0 && res.height > 0) { // via width and height
            scaledImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, res.width, res.height, Scalr.OP_ANTIALIAS)
        } else if (res.width < 0 && res.height < 0 && res.numPixels < 0) { // via all 3 (width, height, and number of pixels)
            scaledImage = image
        }

        val transformedPhotoFile = File(size.toString() + "_" + imageName)

        ImageIO.write(scaledImage, imageType, transformedPhotoFile)

        return transformedPhotoFile
    }
}
