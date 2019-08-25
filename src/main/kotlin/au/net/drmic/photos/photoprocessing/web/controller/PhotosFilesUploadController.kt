package au.net.drmic.photos.photoprocessing.web.controller

import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.service.PhotosService
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
class PhotosFilesUploadController {

    @Autowired
    lateinit var photosService: PhotosService

    @Autowired
    lateinit var logger: Logger

    @RequestMapping("/apis/photos/photo/owner/{ownerUserId}", method = [ RequestMethod.GET ])
    fun retrievePhotosByOwner(@PathVariable("ownerUserId", required = true) ownerUserId: Long): List<Photos> {
        return photosService.retrievePhotosByOwner(ownerUserId)
    }

    @RequestMapping("/apis/photos/photo/{photoId}", method = [ RequestMethod.GET ])
    fun retrievePhotoById(@PathVariable("photoId", required = true) photoId: Long): ResponseEntity<Photos> {
        val photo = photosService.retrievePhotoById(photoId)

        if (photo.isPresent) {
            return ResponseEntity.ok(photo.get())
        } else {
            return ResponseEntity(null, null, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping("/apis/photos/photo", method = [ RequestMethod.POST ])
    fun uploadMultipartFile(@RequestParam("imageOriginal") imageOriginal: MultipartFile,
                            @RequestParam("ownerUserId") ownerUserId: Long,
                            // @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
                            // @ApiModelProperty(value = "datePhotoWasTaken",
                            //                   dataType = "java.lang.String", example = "2018-06-30")
                            @DateTimeFormat(pattern = "dd.MM.yyyy")
                            @RequestParam("datePhotoWasTaken") datePhotoWasTaken: LocalDate,
                            @RequestParam(value = "description", required = false) description: String): ResponseEntity<Long> {
        val photo = photosService.saveFileUploadPhoto(imageOriginal, ownerUserId, datePhotoWasTaken, description)

        var fileName = imageOriginal.getOriginalFilename()

        logger.info("File uploaded successfully! -> filename = " + fileName)

        return ResponseEntity.ok(photo.id)
    }
}
