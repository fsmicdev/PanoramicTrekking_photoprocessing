package au.net.drmic.photos.photoprocessing.repository.entity.support

import java.sql.Timestamp
import javax.persistence.*

/**
 *
 */
@MappedSuperclass
abstract class JpaPersistCapable {

    companion object {
        private val serialVersionUID = -464636789593824L
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @Column(nullable = false)
    lateinit var dateTimeCreated: Timestamp

    @Column(nullable = false)
    lateinit var dateTimeUpdated: Timestamp

    override fun toString() = "Entity of type [${this.javaClass.name}] has id: [$id]"

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dateTimeCreated.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JpaPersistCapable

        if (id != other.id) return false
        if (dateTimeCreated != other.dateTimeCreated) return false

        return true
    }

}