package au.net.drmic.photos.photoprocessing.model

/**
 * Adapted from https://github.com/maltesander/java-image-scaling-thumbnail/blob/master/src/main/java/com/tutorialacademy/img/imgscalr/ImageResolution.java
 */
class PhotoResolution {

    var width: Int = 0
    var height: Int = 0
    var numPixels: Int = 0

    constructor(numPixels: Int) {
        this.numPixels = numPixels
    }

    constructor(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    constructor(width: Int, height: Int, numPixels: Int) {
        this.width = width
        this.height = height
        this.numPixels = numPixels
    }

    override fun toString(): String {
        return "PhotoResolution [width=$width, height=$height, numPixels=$numPixels]"
    }
}
