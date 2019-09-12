package au.net.drmic.photos.photoprocessing.repository;

import au.net.drmic.photos.photoprocessing.repository.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository;

@Repository
interface TagRepository : JpaRepository<Tag, Long> {


}
