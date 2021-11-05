package entities

class PlayerShip : PrimaryObject(100, 10.0f, 20.0f, 0.7f) {

    // Declare local space drawing values for the Drawable interface
    override val vertexData:FloatArray = floatArrayOf(
        //  Position              Colour
        0.0f, 1.0f, z,       0.0f, 1.0f, 0.0f,   // Top-middle       (Front)
        0.0f, -0.5f, z,      0.0f, 1.0f, 0.0f,   // Bottom-middle    (Back)
        -1.0f, -1.0f, z,     0.0f, 1.0f, 0.0f,   // Bottom-left      (Left wingtip)
        1.0f, -1.0f, z,      0.0f, 1.0f, 0.0f    // Bottom-right     (Right wingtip)
    )
    override val indices: IntArray = intArrayOf(
        0, 1, 2,
        0, 1, 3
    )

    fun shoot() {
        // Fire weapons on input
    }
}