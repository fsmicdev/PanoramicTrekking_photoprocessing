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

    override fun beforeTest(testCase: TestCase) {
        val ownerUserId = 1L

        var photoOne = Photos()
        photoOne.photoType = PhotoType.PNG
        photoOne.datePhotoWasTaken = Date(java.util.Date().time)
        photoOne.ownerUserId = ownerUserId
        photoOne.description = "A spiffy trekking photo"
        photoOne.imageOriginal = SerialBlob(ByteArray(500))
        photoOne.imageCroppedThumbnail = SerialBlob(ByteArray(500) )
        photoOne.imageCroppedStandard = SerialBlob(ByteArray(7000))
        photoOne.dateTimeCreated = Timestamp(java.util.Date().time)
        photoOne.dateTimeUpdated = Timestamp(java.util.Date().time)

        photoOne = photosRepository.save(photoOne)

        var photoTwo = Photos()
        photoTwo.photoType = PhotoType.PNG
        photoTwo.datePhotoWasTaken = Date(java.util.Date().time)
        photoTwo.ownerUserId = ownerUserId
        photoTwo.description = "Another spiffy trekking photo"
        photoTwo.imageOriginal = SerialBlob(ByteArray(487))
        photoTwo.imageCroppedThumbnail = SerialBlob(ByteArray(5205) )
        photoTwo.imageCroppedStandard = SerialBlob(ByteArray(8321))
        photoTwo.dateTimeCreated = Timestamp(java.util.Date().time)
        photoTwo.dateTimeUpdated = Timestamp(java.util.Date().time)

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

        /*
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

        test("searchOwnerPhotosByTagFilter(..): Photos (more than one) returned for existing owner and a matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_ONE)).size shouldBe 2
        }

        test("searchOwnerPhotosByTagFilter(..): Photo (only one) returned for existing owner and another [single] matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_TWO)).size shouldBe 1
        }

        test("searchOwnerPhotosByTagFilter(..): Photo (only one) returned for existing owner and lowercase [converted to uppercase] matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_TWO.toLowerCase())).size shouldBe 1
        }

        test("searchOwnerPhotosByTagFilter(..): Photo returned for existing owner and two matching tags.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_ONE, TAG_TWO)).size shouldBe 1
        }

        test("searchOwnerPhotosByTagFilter(..): No photos returned for existing owner, two matching tags, and one non-matching tag.") {
            photosService.searchOwnerPhotosByTagFilter(1L,
                    mutableListOf(TAG_ONE, TAG_TWO, NON_EXISTING_TAG)) shouldBe emptySet()
        }
        */
    }

}