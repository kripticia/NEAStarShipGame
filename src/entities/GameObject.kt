package entities

import rendering.Drawable

abstract class GameObject (val z:Float, width:Float = 1.0f, height:Float = 1.0f,
                           val shader:Int?,
                           var xPos:Int = 0, var yPos:Int = 0, var rotation:Float = 0.0f) : Drawable {

    override val drawWidth:Float = width
    override val drawHeight:Float = height
    override val drawXPos:Int = xPos
    override val drawYPos:Int = yPos
    override val drawRot:Float = rotation

    fun setOrientation(x:Int, y:Int, rot:Float) {
        xPos = x
        yPos = y
        rotation = rot
    }

    fun move(xSpeed:Int, ySpeed:Int) {
        // xPos and yPos will be measured in units, defined elsewhere by the window renderer
        // Speeds will be in units/frame
        xPos += xSpeed
        yPos += ySpeed
    }
}