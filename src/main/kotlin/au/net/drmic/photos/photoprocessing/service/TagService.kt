package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.repository.TagRepository
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.repository.entity.PhotosTag
import au.net.drmic.photos.photoprocessing.repository.entity.Tag
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Service
class TagService {

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var photosTagService: PhotosTagService

    @Autowired
    lateinit var logger: Logger

    fun saveTags(ownerUserId: Long, tags: List<String>, photos: Photos): Int {
        var tagsAddedCount = 0

        tags.forEach {
            val trimmedTagUpperCase = it.toUpperCase().trim()

            val isTagPreExisting = checkIfTagExistsForOwnerUser(ownerUserId, trimmedTagUpperCase)

            if ( ! isTagPreExisting ) {
                val timestampNow = Timestamp(Date().time)

                val timeNow = LocalDateTime.now()

                val tag = Tag()
                tag.ownerUserId = ownerUserId
                tag.tagWord = trimmedTagUpperCase
                tag.dateTimeCreated = timeNow
                tag.dateTimeUpdated = timeNow

                tagRepository.save(tag)

                // Create join association table record in photos_tag between photos and tag record
                val photosTag = PhotosTag()
                photosTag.photo = photos
                photosTag.tag = tag
                photosTag.dateTimeCreated = timeNow
                photosTag.dateTimeUpdated = timeNow

                photosTagService.save(photosTag)

                tagsAddedCount++
            }
        }

        return tagsAddedCount
    }

    fun checkIfTagExistsForOwnerUser(ownerUserId: Long, tagWord: String): Boolean {
        val trimmedTagUpperCase = tagWord.toUpperCase().trim()

        val tag = tagRepository.findFirstByOwnerUserIdAndTagWord(ownerUserId, trimmedTagUpperCase)

        return tag.isPresent
    }

}
