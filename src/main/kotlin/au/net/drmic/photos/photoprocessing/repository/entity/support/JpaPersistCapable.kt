package au.net.drmic.photos.photoprocessing.repository.entity.support

import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * Class/pattern adapted from https://kotlinexpertise.com/hibernate-with-kotlin-spring-boot/
 */
@MappedSuperclass
abstract class JpaPersistCapable {

    companion object {
        private val serialVersionUID = -464636789593824L
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (this === other) return true

        if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as JpaPersistCapable // <*>

        return if (null == this.id) false else this.id == other.id
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString() = "Entity of type ${this.javaClass.name} with id: $id"

}