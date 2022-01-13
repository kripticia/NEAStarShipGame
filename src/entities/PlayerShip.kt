package entities

class PlayerShip (xPos:Float, yPos:Float, var bulletCooldown:Int = 0): PrimaryObject(100, 45.0f, 60.0f) {

    init {
        this.xPos = xPos
        this.yPos = yPos
    }

    override val vertexData:FloatArray = floatArrayOf(
        //  Position              Colour
        0.0f, 1.0f, 0.7f,       0.0f, 1.0f, 0.0f,   // Top-middle       (Front)
        0.0f, -0.5f, 0.7f,      0.0f, 1.0f, 0.0f,   // Bottom-middle    (Back)
        -1.0f, -1.0f, 0.7f,     0.0f, 1.0f, 0.0f,   // Bottom-left      (Left wingtip)
        1.0f, -1.0f, 0.7f,      0.0f, 1.0f, 0.0f    // Bottom-right     (Right wingtip)
    )
    override val indices: IntArray = intArrayOf(
        0, 1, 2,
        0, 1, 3
    )

    fun shoot() : Bullet? {
        // NOTE that bulletCooldown is decreased outside this method, to run the cooldown without spacebar pressed

        val newBullet: Bullet?

        if (this.bulletCooldown <= 0) {
            // Shoot another bullet
            newBullet = Bullet(this.xSpd, 20.0f)
            newBullet.setOrientation(this.xPos, this.yPos)  // Place bullet under ship
            newBullet.ySpd = 20.0f   // Set bullet to shoot upwards

            // Reset shot cooldown
            bulletCooldown = 10
        }
        else {
            newBullet = null
        }
        return newBullet
    }
}