package au.net.drmic.photos.photoprocessing.web.controller

import au.net.drmic.photos.photoprocessing.service.PhotosService
import au.net.drmic.photos.photoprocessing.utility.KotLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController("photos")
class FilesUploadController {

    val logger = KotLogger.logger<FilesUploadController>()

    @Autowired
    lateinit var photosService: PhotosService

    @PostMapping
    fun uploadMultipartFile(@RequestParam("imageOriginal") imageOriginal: MultipartFile,
                            @RequestParam("ownerUserId") ownerUserId: Long,
                            @RequestParam("datePhotoWasTaken") datePhotoWasTaken: Date,
                            @RequestParam(value = "description", required = false) description: String): String {
        photosService.saveFileUploadPhoto(imageOriginal, ownerUserId, datePhotoWasTaken, description)

        var fileName = imageOriginal.getOriginalFilename()

        logger.info("File uploaded successfully! -> filename = " + fileName)

        return "Uploaded: [" + fileName + "]"
    }
}
