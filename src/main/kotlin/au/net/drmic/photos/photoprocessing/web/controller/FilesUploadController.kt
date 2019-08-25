package au.net.drmic.photos.photoprocessing.web.controller

import au.net.drmic.photos.photoprocessing.service.PhotosService
import com.fasterxml.jackson.annotation.JsonFormat
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
class FilesUploadController {

    @Autowired
    lateinit var photosService: PhotosService

    @Autowired
    lateinit var logger: Logger

    @RequestMapping("/apis/photos/photo", method = [ RequestMethod.POST ])
    fun uploadMultipartFile(@RequestParam("imageOriginal") imageOriginal: MultipartFile,
                            @RequestParam("ownerUserId") ownerUserId: Long,
                            // @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
                            // @ApiModelProperty(value = "datePhotoWasTaken",
                            //                   dataType = "java.lang.String", example = "2018-06-30")
                            @DateTimeFormat(pattern = "dd.MM.yyyy")
                            @RequestParam("datePhotoWasTaken") datePhotoWasTaken: LocalDate,
                            @RequestParam(value = "description", required = false) description: String): String {
        photosService.saveFileUploadPhoto(imageOriginal, ownerUserId, datePhotoWasTaken, description)

        var fileName = imageOriginal.getOriginalFilename()

        logger.info("File uploaded successfully! -> filename = " + fileName)

        return "Uploaded: [" + fileName + "]"
    }
}
