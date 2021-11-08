package entities

import org.lwjgl.opengl.GL45
import kotlin.math.cos
import kotlin.math.sin

abstract class GameObject (val shader:Int?, val width:Float = 1.0f, val height:Float = 1.0f,
                           var xPos:Int = 0, var yPos:Int = 0, var rotation:Float = 0.0f) {

    abstract val vertexData:FloatArray
    abstract val indices:IntArray

    fun drawObject(wWidth:Int, wHeight:Int, shaderProgram:Int) {
        // Create buffer/array objects
        val vbo = GL45.glGenBuffers()
        val vao = GL45.glGenVertexArrays()
        val ebo = GL45.glGenBuffers()

        // Bind data to buffers
        GL45.glBindVertexArray(vao)

        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, vbo)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, vertexData, GL45.GL_DYNAMIC_DRAW)

        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, ebo)
        GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, indices, GL45.GL_DYNAMIC_DRAW)

        // Attribute pointers
        // Position attribute
        GL45.glVertexAttribPointer(0, 3, GL45.GL_FLOAT, false, 24, 0)
        GL45.glEnableVertexAttribArray(0)
        // Colour attribute
        GL45.glVertexAttribPointer(1, 3, GL45.GL_FLOAT, false, 24, 12)
        GL45.glEnableVertexAttribArray(1)

        // Create transformation matrices
        val scale = floatArrayOf(
            (width / wWidth), 0.0f, 0.0f, 0.0f,
            0.0f, (height / wHeight), 0.0f, 0.0f,
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
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )

        // Apply transformations
        val scaleLoc = GL45.glGetUniformLocation(shaderProgram, "scale")
        GL45.glUniformMatrix4fv(scaleLoc, true, scale)
        val rotateLoc = GL45.glGetUniformLocation(shaderProgram, "rotate")
        GL45.glUniformMatrix4fv(rotateLoc, true, rotate)
        val translateLoc = GL45.glGetUniformLocation(shaderProgram, "translate")
        GL45.glUniformMatrix4fv(translateLoc, true, translate)

        // Draw object
        GL45.glBindVertexArray(vao)
        GL45.glDrawElements(GL45.GL_TRIANGLES, indices.size, GL45.GL_UNSIGNED_INT, 0)
        GL45.glBindVertexArray(0)
    }

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