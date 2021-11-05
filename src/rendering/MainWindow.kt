package rendering

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL45.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.*

class MainWindow {
    val window:Long

    // Set up the window and its properties
    init {
        glfwInit()
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
        window = glfwCreateWindow(600, 900, "Starship Game", MemoryUtil.NULL, MemoryUtil.NULL)
        glfwShowWindow(window)

        glfwSetFramebufferSizeCallback(window) {_, width, height -> glViewport(0, 0, width, height)}

        glfwMakeContextCurrent(window)
        createCapabilities()
        GLUtil.setupDebugMessageCallback(System.out)
    }

    fun render() {
        // Any prerequisites here
        glClearColor(0.0f, 0.0f, 0.1f,1.0f)

        val vbo = glGenBuffers()
        val vao = glGenVertexArrays()
        val ebo = glGenBuffers()

        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0)
        glEnableVertexAttribArray(0)
        // Colour attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12)
        glEnableVertexAttribArray(1)

        while (!glfwWindowShouldClose(window)) {
            // Render the screen here
            glClear(GL_COLOR_BUFFER_BIT)

            // Draw objects
            glBindVertexArray(vao)
            glDrawElements(GL_TRIANGLES, 18, GL_UNSIGNED_INT, 0)
            glBindVertexArray(0)

            glfwPollEvents()
            glfwSwapBuffers(window)
        }
    }
}