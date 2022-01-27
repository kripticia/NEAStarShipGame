package entities

import kotlin.math.*

class Bullet(xPos:Float, yPos:Float, xSpd:Float, ySpd:Float, team:Int = 0, z:Float=0.31f)
    : Projectile(10, team, 5.0f, 5.0f, 15.0f, z=z) {

    init {
        this.xPos = xPos
        this.yPos = yPos
        this.xSpd = xSpd
        this.ySpd = ySpd

        this.rotation = atan(-xSpd/ySpd)
        if (ySpd < 0) {this.rotation += PI.toFloat()}
    }

    override val vertexData = floatArrayOf(
        //  Position              Colour
        0.0f, 1.0f, z,      0.6f, 0.4f, 0.1f,   // Bullet tip
        -0.7f, 0.66f, z,    0.6f, 0.4f, 0.1f,   // Left curve
        0.7f, 0.66f, z,     0.6f, 0.4f, 0.1f,   // Right curve
        -1.0f, 0.33f, z,    0.6f, 0.4f, 0.1f,   // Left edge
        1.0f, 0.33f, z,     0.6f, 0.4f, 0.1f,   // Right edge

        -1.0f, 0.33f, z,    1.0f, 1.0f, 0.0f,   // Casing... TL
        1.0f, 0.33f, z,     1.0f, 1.0f, 0.0f,   //           TR
        -1.0f, -1.0f, z,    1.0f, 1.0f, 0.0f,   //           BL
        1.0f, -1.0f, z,     1.0f, 1.0f, 0.0f    //           BR
    )
    override val indices = intArrayOf(
        0, 1, 2,
        1, 2, 3,
        2, 3, 4,
        5, 6, 7,
        6, 7, 8
    )

    override fun defaultFun() {
        move()
    }
}