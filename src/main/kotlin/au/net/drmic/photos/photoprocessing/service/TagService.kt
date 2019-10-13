package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.repository.TagRepository
import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import au.net.drmic.photos.photoprocessing.repository.entity.Tag
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*

@Service
class TagService {

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var logger: Logger

    fun saveTags(ownerUserId: Long, tags: List<String>, photos: Photos): Int {
        var tagsAddedCount = 0

        tags.forEach {
            val trimmedTagUpperCase = it.toUpperCase().trim()

            val isTagPreExisting = checkIfTagExistsForOwnerUser(ownerUserId, trimmedTagUpperCase)

            if ( ! isTagPreExisting ) {
                val timestampNow = Timestamp(Date().time)

                val tag = Tag()
                tag.ownerUserId = ownerUserId
                tag.tagWord = trimmedTagUpperCase
                tag.dateTimeCreated = timestampNow
                tag.dateTimeUpdated = timestampNow

                tagRepository.save(tag)

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
