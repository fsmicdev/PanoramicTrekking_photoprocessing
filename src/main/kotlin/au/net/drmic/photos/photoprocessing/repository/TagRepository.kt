package au.net.drmic.photos.photoprocessing.repository;

import au.net.drmic.photos.photoprocessing.repository.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository;
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, Long> {

    @Query
    fun findFirstByOwnerUserIdAndTagWord(ownerUserId: Long, tagWord: String): Optional<Tag>
}
