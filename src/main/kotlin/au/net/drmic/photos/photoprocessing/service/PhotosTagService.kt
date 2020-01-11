package au.net.drmic.photos.photoprocessing.service

import au.net.drmic.photos.photoprocessing.repository.PhotosTagRepository
import au.net.drmic.photos.photoprocessing.repository.entity.PhotosTag
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotosTagService {

    @Autowired
    lateinit var photosTagRepository: PhotosTagRepository

    @Autowired
    lateinit var logger: Logger

    @Transactional
    fun save(photosTag : PhotosTag) : PhotosTag {
        return photosTagRepository.save(photosTag)
    }

}