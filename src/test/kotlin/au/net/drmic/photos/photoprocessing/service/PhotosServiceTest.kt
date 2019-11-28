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
import java.time.LocalDate
import java.time.LocalDateTime
import javax.sql.rowset.serial.SerialBlob

const val TAG_ONE = "SOME_TAG"
const val TAG_TWO = "ANOTHER_TAG"
const val NON_EXISTING_TAG = "NON_EXISTING_TAG"

@RunWith(SpringRunner::class)
@SpringBootTest
class PhotosServiceTest : FunSpec() {

    override fun listeners(): List<TestListener> {
        return listOf(SpringListener)
    }

    @Autowired
    private lateinit var photosService: PhotosService

    @Autowired
    private lateinit var photosRepository: PhotosRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var photosTagRepository: PhotosTagRepository

    override fun beforeTest(testCase: TestCase) {
        var dateTimeNow = LocalDateTime.now()

        var photoOne = Photos()
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
    }

    init {
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

    }

}