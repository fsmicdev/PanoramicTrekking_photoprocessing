package au.net.drmic.photos.photoprocessing.web.controller

import au.net.drmic.photos.photoprocessing.model.PhotoType
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.service.PhotosService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping("apis/photos/photo")
class PhotosProcessingController {

    @Autowired
    lateinit var photosService: PhotosService

    @Autowired
    lateinit var logger: Logger

    @RequestMapping("/owner/{ownerUserId}", method = [ RequestMethod.GET ])
    fun retrievePhotosByOwner(@ApiParam("The user id of the photo's owner.")
                              @PathVariable("ownerUserId", required = true) ownerUserId: Long): List<Photos> {
        return photosService.retrievePhotosByOwner(ownerUserId)
    }

    @RequestMapping("/{photoId}", method = [ RequestMethod.GET ])
    fun retrievePhotoById(@ApiParam("The id of the photo.")
                          @PathVariable("photoId", required = true) photoId: Long): ResponseEntity<Photos> {
        val photo = photosService.retrievePhotoById(photoId)

        if (photo.isPresent) {
            return ResponseEntity.ok(photo.get())
        } else {
            return ResponseEntity(null, null, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping("/", method = [ RequestMethod.PUT ])
    fun updatePhoto(@RequestBody photo: Photos): ResponseEntity<Photos> {
        return ResponseEntity.ok(photosService.updatePhoto(photo))
    }

    @ApiOperation("Upload a raw photo file and associated meta-data. The photo will be transformed into a few " +
                        "consistent sizes on the server, including thumbnail and 'standard' sizes.")
    @RequestMapping("/", method = [ RequestMethod.POST ])
    fun uploadMultipartFile(
            @ApiParam("The format type of the photo.")
            @RequestParam("photoType") photoType: PhotoType,
            @ApiParam("The photo file to be uploaded.")
            @RequestParam("imageOriginal") imageOriginal: MultipartFile,
            @ApiParam("The user id of the photo's owner.")
            @RequestParam("ownerUserId") ownerUserId: Long,
            @ApiParam(value="Date photo was taken [ dd.MM.yyyy ].", example = "30.08.2019")
            @DateTimeFormat(pattern = "dd.MM.yyyy")
            @RequestParam("datePhotoWasTaken") datePhotoWasTaken: LocalDate,
            @ApiParam("Narrative on the photo.")
            @RequestParam(value = "description", required = false) description: String,
            @ApiParam("List of tags/keywords, which can be later searched on.")
            @RequestParam(value = "tags", required = false) tags: List<String>): ResponseEntity<Long> {
        val photo = photosService.saveFileUploadPhoto(
                photoType, imageOriginal, ownerUserId, datePhotoWasTaken, description)

        var fileName = imageOriginal.getOriginalFilename()

        logger.info("File uploaded successfully! -> filename = " + fileName)

        return ResponseEntity.ok(photo.id)
    }
}
