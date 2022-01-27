package entities

import org.lwjgl.glfw.GLFW.*

class PlayerShip (xPos:Float, yPos:Float, private var bulletCooldown:Int = 0)
    : PrimaryObject(100, 1, 50.0f, 45.0f, 60.0f, z=0.65f) {

    init {
        this.xPos = xPos
        this.yPos = yPos
    }

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

    fun pShipFun(window: Long): Projectile? {
        // Process inputs
        // y-input
        ySpd = if (glfwGetKey(window, GLFW_KEY_W) == glfwGetKey(window, GLFW_KEY_S)) {0f}
        else if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {5f}
        // Otherwise, S-key must be pressed, but not W-key
        else {-5f}
        // x-input
        xSpd = if (glfwGetKey(window, GLFW_KEY_D) == glfwGetKey(window, GLFW_KEY_A)) {0f}
        else if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {5f}
        // Otherwise, A-key must be pressed, but not D-key
        else {-5f}
        // Move ship after processing WASD
        move()

        // Check for shooting and return a new Bullet?
        return shoot(window)
    }

    private fun shoot(window:Long) : Bullet? {
        val newBullet: Bullet?
        if(bulletCooldown > 0) {bulletCooldown -= 1}    // Reduce shot cooldown

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && bulletCooldown <= 0) {
            // Shoot another bullet
            newBullet = Bullet(xPos, yPos, xSpd, 20.0f, 1)

            bulletCooldown = 10     // Reset shot cooldown
        }
        else {
            newBullet = null
        }
        return newBullet

    }
}