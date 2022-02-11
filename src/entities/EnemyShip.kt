package entities

private const val z = 0.65f
private val vertexData:FloatArray = floatArrayOf(
    //   Position                Colour
    0.0f, -1.0f, z,      0.6f, 0.0f, 0.0f,   // Bottom-middle    (Front)
    0.0f,  0.5f, z,      0.6f, 0.0f, 0.0f,   // Top-middle       (Back)
    -1.0f, 1.0f, z,      0.6f, 0.0f, 0.0f,   // Top-left         (Right wingtip)
    1.0f, 1.0f, z,       0.6f, 0.0f, 0.0f    // Top-right        (Left wingtip)
)
private val indices: IntArray = intArrayOf(
    0, 1, 2,
    0, 1, 3
)

class EnemyShip(xPos:Float, yPos:Float, private var bulletCooldown:Int = 0)
    : PrimaryObj(vertexData, indices, 45f, 60f, null, z, 50, 2) {

    init {
        this.ySpd = -2.5f
        this.xPos = xPos
        this.yPos = yPos
    }

    fun eShipFun() : Projectile? {
        // Move ship
        move()
        // Attempt to shoot Bullet? and return it to calling fun
        return shoot()
    }

    private fun shoot() : Bullet? {
        val newBullet: Bullet?
        if(bulletCooldown > 0) {bulletCooldown -= 1}    // Reduce shot cooldown

        if (this.bulletCooldown <= 0) {
            // Shoot another bullet
            newBullet = Bullet(xPos, yPos, xSpd, -20.0f, 2)

            // Reset shot cooldown
            bulletCooldown = 60
        }
        else {
            newBullet = null
        }
        return newBullet
    }
}
