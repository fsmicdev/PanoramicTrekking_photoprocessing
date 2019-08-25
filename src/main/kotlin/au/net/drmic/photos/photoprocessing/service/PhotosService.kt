package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.model.PhotoSize
import au.net.drmic.photos.photoprocessing.repository.PhotosRepository
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.ApiModelProperty
import org.imgscalr.Scalr
import org.slf4j.Logger
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
import java.time.LocalDate

@Service
class PhotosService {

    @Autowired
    lateinit var photosRepository: PhotosRepository

    @Autowired
    lateinit var logger: Logger

    fun saveFileUploadPhoto(imageOriginal: MultipartFile,
                            ownerUserId: Long,
                            datePhotoWasTaken: LocalDate,
                            description: String): Photos {
        val photos = Photos()

        val photoName = imageOriginal.originalFilename

        val tmpOriginalUploadedImageFile = File(System.getProperty("java.io.tmpdir") + photoName)

        val photoByteArray = imageOriginal.bytes

        imageOriginal.transferTo(tmpOriginalUploadedImageFile)

        photos.ownerUserId = ownerUserId
        photos.datePhotoWasTaken = java.sql.Date.valueOf(datePhotoWasTaken)
        photos.imageOriginal = SerialBlob(photoByteArray)
        photos.imageCroppedStandard = scale(tmpOriginalUploadedImageFile, imageOriginal, PhotoSize.WXH_512X512)
        photos.imageCroppedThumbnail = scale(tmpOriginalUploadedImageFile, imageOriginal, PhotoSize.WXH_128X128)
        photos.dateTimeUpdated = Timestamp(Date().time)
        photos.description = description

        tmpOriginalUploadedImageFile.delete()

        return photosRepository.save(photos)
    }

    fun scale(tmpOriginalUploadedImageFile: File, photoMultipartFile: MultipartFile, photoSize: PhotoSize): Blob {
        val photoNameWithSize = photoSize.toString() + photoMultipartFile.originalFilename
        val photoType = photoNameWithSize.substring(photoNameWithSize.indexOf(".") + 1)

        val originalBufferedImage : BufferedImage = ImageIO.read(tmpOriginalUploadedImageFile)

        val genCroppedFile = scale(originalBufferedImage, photoSize, photoNameWithSize, photoType)

        val generatedBlob = SerialBlob(convertFileContentToBlob(genCroppedFile.path))
        logger.info(">>> Created blob " + generatedBlob + " for photoName[" + photoNameWithSize + "] of photoType[" + photoType + "]")

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

        val transformedPhotoFile = File(System.getProperty("java.io.tmpdir") + imageName)

        ImageIO.write(scaledImage, imageType, transformedPhotoFile)

        return transformedPhotoFile
    }
}
