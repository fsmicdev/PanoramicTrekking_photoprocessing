package au.net.drmic.photos.photoprocessing.web.controller

import au.net.drmic.commonsupport.exception.BusinessValidationException
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

@CrossOrigin(origins = [ "http://localhost:4200" ])
@RestController
@RequestMapping("apis/photos")
class PhotosProcessingController {

    @Autowired
    lateinit var photosService: PhotosService

    @Autowired
    lateinit var logger: Logger

    @ApiOperation("Retrieve all photos (including associated meta-data) of a particular user, looked-up " +
                        "via the user's id.")
    @RequestMapping("/owner/{ownerUserId}", method = [ RequestMethod.GET ])
    fun retrievePhotosByOwner(@ApiParam("The user id of the photo's owner.", required = true)
                              @PathVariable("ownerUserId", required = true) ownerUserId: Long): ResponseEntity<List<Photos>> {
        return ResponseEntity.ok(photosService.retrievePhotosByOwner(ownerUserId))
    }

    @ApiOperation("Retrieve an existing photo, including associated meta-data, via its id.")
    @RequestMapping("/{photoId}", method = [ RequestMethod.GET ])
    fun retrievePhotoById(@ApiParam("The id of the photo.", required = true)
                          @PathVariable("photoId", required = true) photoId: Long): ResponseEntity<Photos> {
        val photo = photosService.retrievePhotoById(photoId)

        if (photo.isPresent) {
            return ResponseEntity.ok(photo.get())
        } else {
            return ResponseEntity<Photos>(null, null, HttpStatus.NOT_FOUND)
        }
    }

    @ApiOperation("Update an existing photo, including associated meta-data.")
    @RequestMapping("/", method = [ RequestMethod.PUT ])
    fun updatePhoto(@RequestBody(required = true) photo: Photos): ResponseEntity<Photos> {
        return ResponseEntity.ok(photosService.updatePhoto(photo))
    }

    @ApiOperation("Upload a raw photo file and associated meta-data (including tag words). The photo will be " +
                        "transformed into a few consistent sizes on the server, including thumbnail and " +
                        "'standard' sizes.")
    @RequestMapping("/", method = [ RequestMethod.POST ])
    fun uploadMultipartFile(
            @ApiParam("The photo file to be uploaded.", required = true)
            @RequestParam("imageOriginal", required = true) imageOriginal: MultipartFile,
            @ApiParam("The user id of the photo's owner.", required = true)
            @RequestParam("ownerUserId", required = true) ownerUserId: Long,
            @ApiParam(value="Date photo was taken [ dd.MM.yyyy ].", example = "30.08.2019", required = true)
            @DateTimeFormat(pattern = "dd.MM.yyyy")
            @RequestParam("datePhotoWasTaken", required = true) datePhotoWasTaken: LocalDate,
            @ApiParam("Narrative on the photo.", required = true)
            @RequestParam(value = "description", required = true) description: String,
            @ApiParam("List of tags/keywords, which can be later searched on.", required = true)
            @RequestParam(value = "tags", required = true) tags: List<String>): ResponseEntity<Long> {
        // ----->>> N.B. PhotoType can be inferred from imageOriginal MultipartFile suffix <<<-----
        val photosFilename = imageOriginal.originalFilename!!
        logger.info(">>>>> photosFilename: ", photosFilename)
        val lastIndexOfDotSeparator = photosFilename.lastIndexOf(".")

        var photoType: PhotoType? = null

        if (lastIndexOfDotSeparator != -1) {
            val photosFileSuffixUpper = photosFilename.substring(lastIndexOfDotSeparator + 1).toUpperCase().trim()

            photoType = PhotoType.valueOf(photosFileSuffixUpper)
        }

        logger.info(">>> >>> >>> Inferred photoType: " + photoType)

        if (photoType!= null) {
            val photo = photosService.saveFileUploadPhoto(
                    photoType, imageOriginal, ownerUserId, datePhotoWasTaken, description, tags)

            var fileName = imageOriginal.getOriginalFilename()

            logger.info("File uploaded successfully! -> filename = {}", fileName)

            return ResponseEntity.ok(photo.id)
        } else {
            throw BusinessValidationException("Cannot infer the photo's type from its filename - MUST BE one of " +
                                              "*.JPG, *.JPEG, *.PNG, *.BMP, *.GIF")
        }
    }
}
