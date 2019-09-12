package au.net.drmic.photos.photoprocessing.repository.entity

import au.net.drmic.photos.photoprocessing.repository.entity.support.JpaPersistCapable
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class PhotosTag : JpaPersistCapable() {

    @ManyToOne
    lateinit var photo: Photos

    @ManyToOne
    lateinit var tag: Tag

}