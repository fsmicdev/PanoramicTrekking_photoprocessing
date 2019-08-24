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
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Blob

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
        photos.imageCroppedStandard = scale(imageOriginal, PhotoSize.WXH_512X512)
        photos.imageCroppedThumbnail = scale(imageOriginal, PhotoSize.WXH_128X128)
        photos.dateTimeUpdated = Timestamp(Date().time)
        photos.description = description

        return photosRepository.save(photos)
    }

    fun scale(photoMultipartFile: MultipartFile, photoSize: PhotoSize): Blob {
        val photoName = photoSize.toString() + photoMultipartFile.originalFilename
        val photoType = photoName.substring(photoName.indexOf(".") + 1)

        val tmpFile = File(photoName)

        photoMultipartFile.transferTo(tmpFile)
        val originalBufferedImage : BufferedImage = ImageIO.read(tmpFile)

        val genCroppedFile = scale(originalBufferedImage, photoSize, photoName, photoType)

        val generatedBlob = SerialBlob(convertFileContentToBlob(genCroppedFile.path))
        logger.info("Created blob " + generatedBlob + " for photoName[" + photoName + "] of photoType[" + photoType + "]")

        tmpFile.delete()

        return generatedBlob
    }

    @Throws(IOException::class)
    fun convertFileContentToBlob(filePathStr: String): ByteArray {
        val filePath = Paths.get(filePathStr)

        return Files.readAllBytes(filePath)
    }
    /**
     * Adapted from
     * https://github.com/maltesander/java-image-scaling-thumbnail/blob/master/src/main/java/com/tutorialacademy/img/imgscalr/ImageScaler.java
     */
    @Throws(IOException::class)
    fun scale(image: BufferedImage, photoSize: PhotoSize, imageName: String, imageType: String): File {
        val res = photoSize.toImageResolution()

        var scaledImage: BufferedImage? = null

        if (res.numPixels > 0) { // via number of pixels
            scaledImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, res.numPixels, Scalr.OP_ANTIALIAS)
        } else if (res.width > 0 && res.height > 0) { // via width and height
            scaledImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, res.width, res.height, Scalr.OP_ANTIALIAS)
        } else if (res.width < 0 && res.height < 0 && res.numPixels < 0) { // via all 3 (width, height, and number of pixels)
            scaledImage = image
        }

        val transformedPhotoFile = File(photoSize.toString() + "_" + imageName)

        ImageIO.write(scaledImage, imageType, transformedPhotoFile)

        return transformedPhotoFile
    }
}
