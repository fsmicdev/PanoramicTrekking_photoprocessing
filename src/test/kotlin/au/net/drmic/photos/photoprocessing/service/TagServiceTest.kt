package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.model.PhotoType
import au.net.drmic.photos.photoprocessing.repository.PhotosRepository
import au.net.drmic.photos.photoprocessing.repository.PhotosTagRepository
import au.net.drmic.photos.photoprocessing.repository.TagRepository
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.repository.entity.PhotosTag
import au.net.drmic.photos.photoprocessing.repository.entity.Tag
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.spring.SpringListener
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import javax.sql.rowset.serial.SerialBlob

@RunWith(SpringRunner::class)
@SpringBootTest
class TagServiceTest : FunSpec() {

    override fun listeners(): List<TestListener> {
        return listOf(SpringListener)
    }

    @Autowired
    private lateinit var tagService: TagService

    @Autowired
    private lateinit var photosRepository : PhotosRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    private var photoOne: Photos = Photos()
    private var photoTwo: Photos = Photos()
    private var photoThree: Photos = Photos()

    override fun beforeTest(testCase: TestCase) {
        val ownerUserId = 1L

        val dateNow = LocalDate.now()
        val timeNow = LocalTime.now()

        photoOne.photoType = PhotoType.PNG
        photoOne.datePhotoWasTaken = dateNow
        photoOne.ownerUserId = ownerUserId
        photoOne.description = "A spiffy trekking photo"
        photoOne.setImageOriginal(SerialBlob(ByteArray(500)))
        photoOne.setImageCroppedThumbnail(SerialBlob(ByteArray(500)))
        photoOne.setImageCroppedStandard(SerialBlob(ByteArray(7000)))
        photoOne.dateTimeCreated = timeNow
        photoOne.dateTimeUpdated = timeNow
        photoOne = photosRepository.save(photoOne)

        photoTwo.photoType = PhotoType.PNG
        photoTwo.datePhotoWasTaken = dateNow
        photoTwo.ownerUserId = ownerUserId
        photoTwo.description = "Another spiffy trekking photo"
        photoTwo.setImageOriginal(SerialBlob(ByteArray(487)))
        photoTwo.setImageCroppedThumbnail(SerialBlob(ByteArray(5205)))
        photoTwo.setImageCroppedStandard(SerialBlob(ByteArray(8321)))
        photoTwo.dateTimeCreated = timeNow
        photoTwo.dateTimeUpdated = timeNow
        photoTwo = photosRepository.save(photoTwo)

        val tagsForPhotoOne = mutableListOf<String>()
        tagsForPhotoOne.add("RUSSIA")
        tagsForPhotoOne.add("PALACE")
        tagsForPhotoOne.add("SQUARE")

        tagService.saveTags(ownerUserId, tagsForPhotoOne, photoOne)

    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        // photosTagRepository.deleteAll()
        photosRepository.deleteAll()
        tagRepository.deleteAll()
    }

    init {
        test("checkIfTagExistsForOwnerUser(..): Non-existing tag - return false.") {
            tagService.checkIfTagExistsForOwnerUser(1L, "BLAH") shouldBe false
        }

        test("checkIfTagExistsForOwnerUser(..): Existing tag (already uppercase) - return true.") {
            tagService.checkIfTagExistsForOwnerUser(1L, "RUSSIA") shouldBe true
        }

        test("checkIfTagExistsForOwnerUser(..): Existing tag (lowercase - should get converted) - return true.") {
            tagService.checkIfTagExistsForOwnerUser(1L, "Russia") shouldBe true
        }

        test("saveTags(..):  three new tags; count of 3 (new) tags should be returned.") {
            val tagsForPhotoTwo = mutableListOf<String>()
            tagsForPhotoTwo.add("ABC")
            tagsForPhotoTwo.add("DEF")
            tagsForPhotoTwo.add("GHI")

            tagService.saveTags(1L, tagsForPhotoTwo, photoTwo) shouldBe 3
        }

        test("saveTags(..):  two old tags AND zero new tags; count of 0 (new) tags should be returned.") {
            val tagsForPhotoOne = mutableListOf<String>()
            tagsForPhotoOne.add("RUSSIA")
            tagsForPhotoOne.add("PALACE")

            tagService.saveTags(1L, tagsForPhotoOne, photoOne) shouldBe 0
        }

        test("saveTags(..):  two old tags AND one new tags; count of 1 (new) tags should be returned.") {
            val tagsForPhotoOne = mutableListOf<String>()
            tagsForPhotoOne.add("RUSSIA")
            tagsForPhotoOne.add("PALACE")
            tagsForPhotoOne.add("NEWBIE")

            tagService.saveTags(1L, tagsForPhotoOne, photoOne) shouldBe 1
        }

        test("saveTags(..):  no new or old tags; count of zero (new) tags should be returned.") {
            val tagsForPhotoOne = mutableListOf<String>()

            tagService.saveTags(1L, tagsForPhotoOne, photoOne) shouldBe 0
        }
    }

}