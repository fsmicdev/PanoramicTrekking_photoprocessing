package au.net.drmic.photos.photoprocessing.repository.entity

import au.net.drmic.photos.photoprocessing.repository.entity.support.JpaPersistCapable
import javax.persistence.*

@Entity
class Tag : JpaPersistCapable() {

    @Column(nullable = false)
    var ownerUserId: Long? = null

    @Column(nullable = false)
    lateinit var tagWord: String

    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY, mappedBy = "tag")
    private val photoTags = mutableListOf<PhotosTag>()

}