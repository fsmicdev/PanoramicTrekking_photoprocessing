package au.net.drmic.photos.photoprocessing.repository.entity

import au.net.drmic.commonsupport.persistence.jpa.JpaPersistCapable
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class PhotosTag : JpaPersistCapable() {

    @ManyToOne
    lateinit var photo: Photos

    @ManyToOne
    lateinit var tag: Tag

}