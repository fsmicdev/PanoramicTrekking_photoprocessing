package au.net.drmic.photos.photoprocessing.repository;

import au.net.drmic.photos.photoprocessing.repository.entity.Photos
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository;
import java.sql.Date

@Repository
interface PhotosRepository : JpaRepository<Photos, Long> {

    fun findByOwnerUserId(ownerUserId: Long): List<Photos>

    fun findByDescriptionIgnoreCaseContaining(description: String): List<Photos>

    fun findByDatePhotoWasTakenBefore(beforeDatePhotoWasTaken: Date): List<Photos>

    fun findByDatePhotoWasTakenAfter(afterDatePhotoWasTaken: Date): List<Photos>

    fun findByDatePhotoWasTakenBetween(startDatePhotoWasTaken: Date, endDatePhotoWasTaken: Date): List<Photos>

    fun findByDateTimeUpdatedAfter(dateTimeUpdated: Date): List<Photos>

}
