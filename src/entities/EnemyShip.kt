package entities

class EnemyShip : PrimaryObject(100, 45.0f, 60.0f) {

    // Declare drawing values for the Drawable interface
    override val vertexData:FloatArray = floatArrayOf(
        //   Position                Colour
        0.0f, -1.0f, 0.65f,      0.6f, 0.0f, 0.0f,   // Bottom-middle    (Front)
        0.0f,  0.5f, 0.65f,      0.6f, 0.0f, 0.0f,   // Top-middle       (Back)
        -1.0f, 1.0f, 0.65f,      0.6f, 0.0f, 0.0f,   // Top-left         (Right wingtip)
        1.0f, 1.0f, 0.65f,       0.6f, 0.0f, 0.0f    // Top-right        (Left wingtip)
    )
    override val indices: IntArray = intArrayOf(
        0, 1, 2,
        0, 1, 3
    )

    fun shoot() {
        // Fire weapons on input
    }
}