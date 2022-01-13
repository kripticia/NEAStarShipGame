package entities

import org.lwjgl.opengl.GL45.*
import kotlin.math.cos
import kotlin.math.sin

abstract class GameObject (private val width:Float = 1.0f, private val height:Float = 1.0f,
                           val size:Float = 5.0f, val shader:Int?,
                           var xPos:Float = 0.0f, var yPos:Float = 0.0f, var rotation:Float = 0.0f, // Orientation
                           var xSpd:Float = 0.0f, var ySpd:Float = 0.0f)                            // Default speeds
{
    abstract val vertexData:FloatArray
    abstract val indices:IntArray

    fun drawObject(wWidth:Int, wHeight:Int, shaderProgram:Int) {
        // Create buffer/array objects
        val vbo = glGenBuffers()
        val vao = glGenVertexArrays()
        val ebo = glGenBuffers()

        // Bind data to buffers
        glBindVertexArray(vao)

        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_DYNAMIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW)

        // Attribute pointers
        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0)
        glEnableVertexAttribArray(0)
        // Colour attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12)
        glEnableVertexAttribArray(1)

        val wRatio = wHeight/wWidth.toFloat()

        val scale = floatArrayOf(
            width/(wRatio*400), 0.0f, 0.0f, 0.0f,
            0.0f, height/(wRatio*400), 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        val rotate = floatArrayOf(
            cos(rotation), -sin(rotation), 0.0f, 0.0f,
            sin(rotation), cos(rotation), 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        val translate = floatArrayOf(
            1.0f, 0.0f, 0.0f, xPos/wWidth,
            0.0f, 1.0f, 0.0f, yPos/wHeight,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        val view = floatArrayOf(
            wHeight.toFloat()/wWidth.toFloat(), 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )

        // Apply transformations
        val scaleLoc = glGetUniformLocation(shaderProgram, "scale")
        glUniformMatrix4fv(scaleLoc, true, scale)
        val rotateLoc = glGetUniformLocation(shaderProgram, "rotate")
        glUniformMatrix4fv(rotateLoc, true, rotate)
        val translateLoc = glGetUniformLocation(shaderProgram, "translate")
        glUniformMatrix4fv(translateLoc, true, translate)
        val viewLoc = glGetUniformLocation(shaderProgram, "view")
        glUniformMatrix4fv(viewLoc, true, view)

        // Draw object
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    fun setOrientation(x:Int, y:Int, rot:Float = rotation) {
        setOrientation(x.toFloat(), y.toFloat(), rot)
    }
    fun setOrientation(x:Float, y:Float, rot:Float = rotation) {
        xPos = x
        yPos = y
        rotation = rot
    }

    fun move(xSpeed: Int, ySpeed:Int, rotChange:Float = 0.0f) {
        move(xSpeed.toFloat(), ySpeed.toFloat(), rotChange)
    }
    fun move(xSpeed:Float = xSpd, ySpeed:Float = ySpd, rotChange: Float = 0.0f) {
        // xPos and yPos will be measured in units, defined elsewhere by the window renderer
        // Move speeds will be in units/frame
        // Rotation speed is entered as rad/s, and /60 allows for the 60fps
        xPos += xSpeed
        yPos += ySpeed
        rotation += rotChange / 60
    }

    open fun defaultFun() {}  // Will be defined per subclass
}