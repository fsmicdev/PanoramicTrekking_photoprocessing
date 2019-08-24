package au.net.drmic.photos.photoprocessing.model

/**
 * Adapted from https://github.com/maltesander/java-image-scaling-thumbnail/blob/master/src/main/java/com/tutorialacademy/img/imgscalr/ImageSize.java
 */
enum class PhotoSize private constructor(private val `val`: String) {

    // width x height
    WXH_64X64("WXH_64X64"),
    WXH_128X128("WXH_128X128"),
    WXH_256X256("WXH_256X256"),
    WXH_512X512("WXH_512X512"),

    // pixel
    P_250("P_250"),
    P_500("P_500"),
    P_1000("P_1000"),

    // source
    SOURCE("SOURCE");

    override fun toString(): String {
        return this.`val`
    }

    fun toImageResolution(): PhotoResolution {
        var res: PhotoResolution? = null

        when (this) {
            WXH_64X64 -> res = PhotoResolution(64, 64)

            WXH_128X128 -> res = PhotoResolution(128, 128)

            WXH_256X256 -> res = PhotoResolution(256, 256)

            WXH_512X512 -> res = PhotoResolution(512, 512)

            P_250 -> res = PhotoResolution(250)

            P_500 -> res = PhotoResolution(500)

            P_1000 -> res = PhotoResolution(1000)

            SOURCE -> res = PhotoResolution(-1, -1, -1)
        }

        return res
    }
}