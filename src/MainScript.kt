import entities.GameObject
import entities.PlayerShip
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL45.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil

fun main() {
	val window = createWindow()
	val gameObjectList:MutableList<GameObject> = ArrayList()
	val defaultShader = createDefaultShader()
	glUseProgram(defaultShader)
	glClearColor(0.0f, 0.0f, 0.1f, 1.0f)

	// For testing purposes, can be removed at any point
	gameObjectList.add(PlayerShip())

	while (!glfwWindowShouldClose(window)){
		// Main code will run in here
		drawFrame(window, gameObjectList, defaultShader)
	}
}

fun createWindow() : Long {
	glfwInit()
	glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
	val window = glfwCreateWindow(600, 900, "Starship Game", MemoryUtil.NULL, MemoryUtil.NULL)
	glfwShowWindow(window)

	glfwSetFramebufferSizeCallback(window) { _, width, height -> glViewport(0, 0, width, height) }

	glfwMakeContextCurrent(window)
	GL.createCapabilities()
	GLUtil.setupDebugMessageCallback(System.out)

	return window
}

fun createDefaultShader() : Int {
	// Write default shader programs
	val vsSource: String = """
        #version 330 core
            
        layout (location = 0) in vec3 pos;
        layout (location = 1) in vec3 inColour;
        out vec3 outColour;
		
		uniform mat4 scale;
		uniform mat4 rotate;
		uniform mat4 translate;
        
        void main () 
        {
            gl_Position = translate * rotate * scale * vec4(pos, 1.0);
            outColour = inColour;
        }
        
        """.trimIndent()

	val fsSource: String = """
        #version 330 core
            
        out vec4 FragColour;
        in vec3 outColour;
            
        void main () {
            FragColour = vec4(outColour, 1.0);
        }
    """.trimIndent()

	// Create shader program
	val vertexShader = glCreateShader(GL_VERTEX_SHADER)
	glShaderSource(vertexShader, vsSource)
	glCompileShader(vertexShader)

	val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
	glShaderSource(fragmentShader, fsSource)
	glCompileShader(fragmentShader)

	val shaderProgram = glCreateProgram()
	glAttachShader(shaderProgram, vertexShader)
	glAttachShader(shaderProgram, fragmentShader)
	glLinkProgram(shaderProgram)
	glDeleteShader(vertexShader)
	glDeleteShader(fragmentShader)
	glUseProgram(shaderProgram)

	return shaderProgram
}

fun drawFrame(window:Long, gameObjectList:MutableList<GameObject>, defaultShader:Int) {
	// Draw the frame per loop
	glClear(GL_COLOR_BUFFER_BIT)    // Background colour behind all objects

	// Get window size
	val wWidth = MemoryUtil.memAllocInt(1)
	val wHeight = MemoryUtil.memAllocInt(1)
	glfwGetFramebufferSize(window, wWidth, wHeight)

	// Draw each object
	for (gameObject in gameObjectList) {
		gameObject.setOrientation(50,-50, 0.0f)
		if (gameObject.shader != null) {
			glUseProgram(gameObject.shader)
			gameObject.drawObject(wWidth.get(), wHeight.get(), gameObject.shader)
			glUseProgram(defaultShader)
		}
		else {gameObject.drawObject(wWidth.get(), wHeight.get(), defaultShader)}
	}

	glfwPollEvents()
	glfwSwapBuffers(window)
}
