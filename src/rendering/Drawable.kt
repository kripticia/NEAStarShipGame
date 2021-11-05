package rendering

import org.lwjgl.opengl.GL45.*
import kotlin.math.*

interface Drawable {
    // Local space draw data
    val vertexData:FloatArray
    val indices:IntArray

    // Other data for transformation matrices
    val drawWidth:Float
    val drawHeight:Float
    val drawXPos:Int
    val drawYPos:Int
    val drawRot:Float

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
        glVertexAttribPointer(0,3, GL_FLOAT, false, 24, 0)
        glEnableVertexAttribArray(0)
        // Colour attribute
        glVertexAttribPointer(1,3, GL_FLOAT, false, 24, 12)
        glEnableVertexAttribArray(1)

        // Create transformation matrices
        val scale = floatArrayOf(
            (drawWidth / wWidth), 0.0f, 0.0f, 0.0f,
            0.0f, (drawWidth / wWidth), 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        val rotate = floatArrayOf(
            cos(drawRot), -sin(drawRot), 0.0f, 0.0f,
            sin(drawRot), cos(drawRot), 0.0f, 0.0f,
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
        val scaleLoc = glGetUniformLocation(shaderProgram, "scale")
        glUniformMatrix4fv(scaleLoc, true, scale)
        val rotateLoc = glGetUniformLocation(shaderProgram, "rotate")
        glUniformMatrix4fv(rotateLoc, true, rotate)
        val translateLoc = glGetUniformLocation(shaderProgram, "translate")
        glUniformMatrix4fv(translateLoc, true, translate)

        // Draw object
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }
}