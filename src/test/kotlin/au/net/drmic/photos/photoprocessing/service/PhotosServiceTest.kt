package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.model.PhotoType
import au.net.drmic.photos.photoprocessing.repository.PhotosRepository
import au.net.drmic.photos.photoprocessing.repository.PhotosTagRepository
import au.net.drmic.photos.photoprocessing.repository.TagRepository
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.repository.entity.PhotosTag
import au.net.drmic.photos.photoprocessing.repository.entity.Tag
import io.kotlintest.*
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.date.shouldBeBefore
import io.kotlintest.specs.FunSpec
import io.kotlintest.spring.SpringListener
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItem
import org.apache.commons.io.IOUtils
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.commons.CommonsMultipartFile
import java.io.*
import java.io.File.createTempFile
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.sql.DataSource
import javax.sql.rowset.serial.SerialBlob


const val TAG_ONE = "SOME_TAG"
const val TAG_TWO = "ANOTHER_TAG"
const val NON_EXISTING_TAG = "NON_EXISTING_TAG"

@RunWith(SpringRunner::class)
@SpringBootTest
// @SqlGroup(
//         Sql("/classpath:loadTestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
//        Sql("/test-user-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD))
class PhotosServiceTest : FunSpec() {

    override fun listeners(): List<TestListener> {
        return listOf(SpringListener)
    }

    lateinit var photoOne : Photos

    @Autowired
    private lateinit var photosService: PhotosService

    @Autowired
    private lateinit var photosRepository: PhotosRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var photosTagRepository: PhotosTagRepository

    @Autowired
    private lateinit var dataSource: DataSource

    override fun beforeTest(testCase: TestCase) {
        // ScriptUtils.executeSqlScript(dataSource.connection, ClassPathResource("cleanupTestData.sql"));

        // ScriptUtils.executeSqlScript(dataSource.connection, ClassPathResource("loadTestData.sql"));

        var dateTimeNow = LocalDateTime.now()

        photoOne = Photos()
        photoOne.photoType = PhotoType.PNG
        photoOne.datePhotoWasTaken = LocalDate.now()
        photoOne.ownerUserId = 1L
        photoOne.description = "A spiffy trekking photo"
        photoOne.setImageOriginal(SerialBlob(ByteArray(500)))
        photoOne.setImageCroppedThumbnail(SerialBlob(ByteArray(500)))
        photoOne.setImageCroppedStandard(SerialBlob(ByteArray(7000)))
        photoOne.dateTimeCreated = dateTimeNow
        photoOne.dateTimeUpdated = dateTimeNow

        photoOne = photosRepository.save(photoOne)

        var photoTwo = Photos()
        photoTwo.photoType = PhotoType.PNG
        photoTwo.datePhotoWasTaken = LocalDate.now()
        photoTwo.ownerUserId = 1L
        photoTwo.description = "Another spiffy trekking photo"
        photoTwo.setImageOriginal(SerialBlob(ByteArray(487)))
        photoTwo.setImageCroppedThumbnail(SerialBlob(ByteArray(5205)))
        photoTwo.setImageCroppedStandard(SerialBlob(ByteArray(8321)))
        photoTwo.dateTimeCreated = dateTimeNow
        photoTwo.dateTimeUpdated = dateTimeNow

        photoTwo = photosRepository.save(photoTwo)

        var tagOneForUserOne = Tag()
        tagOneForUserOne.tagWord = TAG_ONE
        tagOneForUserOne.ownerUserId = 1
        tagOneForUserOne.dateTimeCreated = dateTimeNow
        tagOneForUserOne.dateTimeUpdated = dateTimeNow

        tagOneForUserOne = tagRepository.save(tagOneForUserOne)

        var tagTwoForUserOne = Tag()
        tagTwoForUserOne.tagWord = TAG_TWO
        tagTwoForUserOne.ownerUserId = 1
        tagTwoForUserOne.dateTimeCreated = dateTimeNow
        tagTwoForUserOne.dateTimeUpdated = dateTimeNow

        tagTwoForUserOne = tagRepository.save(tagTwoForUserOne)
        
        var photoOneTagOneForUserOne = PhotosTag()
        photoOneTagOneForUserOne.photo = photoOne
        photoOneTagOneForUserOne.tag = tagOneForUserOne
        photoOneTagOneForUserOne.dateTimeCreated = dateTimeNow
        photoOneTagOneForUserOne.dateTimeUpdated = dateTimeNow

        photoOneTagOneForUserOne = photosTagRepository.save(photoOneTagOneForUserOne)

        var photoOneTagTwoForUserOne = PhotosTag()
        photoOneTagTwoForUserOne.photo = photoOne
        photoOneTagTwoForUserOne.tag = tagTwoForUserOne
        photoOneTagTwoForUserOne.dateTimeCreated = dateTimeNow
        photoOneTagTwoForUserOne.dateTimeUpdated = dateTimeNow

        photoOneTagTwoForUserOne = photosTagRepository.save(photoOneTagTwoForUserOne)

        var photoTwoTagOneForUserOne = PhotosTag()
        photoTwoTagOneForUserOne.photo = photoTwo
        photoTwoTagOneForUserOne.tag = tagOneForUserOne
        photoTwoTagOneForUserOne.dateTimeCreated = dateTimeNow
        photoTwoTagOneForUserOne.dateTimeUpdated = dateTimeNow

        photoTwoTagOneForUserOne = photosTagRepository.save(photoTwoTagOneForUserOne)

        photosTagRepository.findAll()
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        photosTagRepository.deleteAll()
        photosRepository.deleteAll()

        // ScriptUtils.executeSqlScript(dataSource.connection, ClassPathResource("cleanupTestData.sql"));
    }

    init {
        test("retrievePhotoById(..): Non-existing photo [id]; should return Optional.empty") {
            photosService.retrievePhotoById(999L) shouldBe Optional.empty()
        }

        test("retrievePhotoById(..): Existing photo [id]; should return photo") {
            val photo = photosService.retrievePhotoById(photoOne.id).get()

            photo.photoType shouldBe PhotoType.PNG
            photo.datePhotoWasTaken shouldBe LocalDate.now()
            photo.ownerUserId shouldBe 1L
            photo.description shouldBe "A spiffy trekking photo"
            photo.dateTimeCreated shouldBeBefore LocalDateTime.now()
            photo.dateTimeUpdated shouldBeBefore LocalDateTime.now()
        }

        test("retrievePhotosByOwner(..): No photos returned for non-existing owner.") {
            photosService.retrievePhotosByOwner(514L) shouldBe emptyList()
        }

        test("retrievePhotosByOwner(..): Correct number of photos should be returned for existing owner.") {
            photosService.retrievePhotosByOwner(1L).size shouldBe 2
        }

        test("searchOwnerPhotosByTagFilter(..): No photos returned for non-existing owner.") {
            photosService.searchOwnerPhotosByTagFilter(514L,
                    mutableListOf(TAG_ONE)) shouldBe emptySet()
        }

        test("searchOwnerPhotosByTagFilter(..): No photos returned for existing owner, but non-matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(NON_EXISTING_TAG)) shouldBe emptySet()
        }

        test("searchOwnerPhotosByTagFilter(..): Photos (more than one) returned for existing owner and " +
                    "a matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_ONE)).size shouldBe 2
        }

        test("searchOwnerPhotosByTagFilter(..): Photo (only one) returned for existing owner and another " +
                    "[single] matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_TWO)).size shouldBe 1
        }

        test("searchOwnerPhotosByTagFilter(..): Photo (only one) returned for existing owner and " +
                    "lowercase [converted to uppercase] matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_TWO.toLowerCase())).size shouldBe 1
        }

        test("searchOwnerPhotosByTagFilter(..): Photo returned for existing owner and two matching tags.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_ONE, TAG_TWO)).size shouldBe 1
        }

        test("searchOwnerPhotosByTagFilter(..): No photos returned for existing owner, two matching " +
                    "tags, and one non-matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_ONE, TAG_TWO, NON_EXISTING_TAG)) shouldBe emptySet()
        }

        test("convertFileContentToByteArray(..): Non-existing file - throws java.nio.file.NoSuchFileException") {
            shouldThrow<java.nio.file.NoSuchFileException> {
                photosService.convertFileContentToByteArray("Z:/Unfound/notbelievable.png")
            }
        }

        test("convertFileContentToByteArray(..): Existing file - converts to ByteArray") {
            val tmpFile : File = createTempFile("temp", "txt")
            val tmpFilePath = tmpFile.getAbsolutePath()
            tmpFile.deleteOnExit()

            val data = "Temp test data written only..."

            try {
                BufferedWriter(FileWriter(tmpFile)).use { writer ->
                    writer.write(data)
                }
            } catch (e: IOException) {
                fail("Should not be exception")
            }

            val fileBytes = photosService.convertFileContentToByteArray(tmpFilePath)
            fileBytes.size shouldNotBe 0
            fileBytes.size shouldBe data.length
        }

        /*
        test("saveFileUploadPhoto(..): with 2 tags - creates file with 2 tags") {
            val tags = ArrayList<String>()
            tags.add("Uno")
            tags.add("Duo")

            val tmpFile : File = createTempFile("temp", ".txt")
            val tmpFilePath = tmpFile.getAbsolutePath()
            //tmpFile.deleteOnExit()

            val bufWrit = BufferedWriter(FileWriter(tmpFile))

            try {
                bufWrit.write("Some Text")
                bufWrit.flush()
                bufWrit.close()

                val file = File(tmpFilePath)
                val fileItem: FileItem = DiskFileItem("mainFile", Files.probeContentType(file.toPath()),
                        false, file.name, file.length().toInt(), file.parentFile)

                try {
                    val input: InputStream = FileInputStream(file)
                    val os = fileItem.outputStream
                    IOUtils.copy(input, os)
                } catch (ex: IOException) { // do something.
                }

                val multipartFile: MultipartFile = CommonsMultipartFile(fileItem)

                val photo = photosService.saveFileUploadPhoto(
                        PhotoType.PNG,
                        multipartFile,
                        1L,
                        LocalDate.now(),
                        "Some desc",
                        tags)

                photo shouldNotBe null
                photo.description shouldBe "Some desc"
                photo.getPhotoTags().size shouldBe 2
                photo.datePhotoWasTaken shouldBe LocalDate.now()
                photo.dateTimeUpdated shouldBeBefore LocalDateTime.now()
                photo.dateTimeCreated shouldBeBefore LocalDateTime.now()
            } catch (e: IOException) {
                fail("Should not be exception")
            } finally {

            }
        }
        */
    }

}
