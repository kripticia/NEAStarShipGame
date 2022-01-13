package entities

class EnemyShip(xPos:Float, yPos:Float, var bulletCooldown:Int = 0) : PrimaryObject(100, 45.0f, 60.0f) {

    init {
        this.ySpd = -2.5f
        this.xPos = xPos
        this.yPos = yPos
    }

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

    fun shoot() : Bullet? {
        // NOTE that bulletCooldown is decreased outside this method, to run the cooldown automatically
        val newBullet: Bullet?

        if (this.bulletCooldown <= 0) {
            // Shoot another bullet
            newBullet = Bullet(this.xSpd, -20.0f)
            newBullet.setOrientation(this.xPos, this.yPos)  // Place bullet under ship

            // Reset shot cooldown
            bulletCooldown = 20
        }
        else {
            newBullet = null
        }
        return newBullet
    }
}
