package entities

class EnemyShip(z: Float = 2.5f) : PrimaryObject(100, 10.0f, 20.0f, z) {

    // Declare drawing values for the Drawable interface
    override val vertexData:FloatArray = floatArrayOf(
        //   Position                Colour
        0.0f, -1.0f, z,      0.6f, 0.0f, 0.0f,   // Bottom-middle    (Front)
        0.0f,  0.5f, z,      0.6f, 0.0f, 0.0f,   // Top-middle       (Back)
        -1.0f, 1.0f, z,      0.6f, 0.0f, 0.0f,   // Top-left         (Right wingtip)
        1.0f, 1.0f, z,       0.6f, 0.0f, 0.0f    // Top-right        (Left wingtip)
    )
    override val indices: IntArray = intArrayOf(
        0, 1, 2,
        0, 1, 3
    )

    fun shoot() {
        // Fire weapons on input
    }
}