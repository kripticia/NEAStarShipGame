import entities.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL45.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.sqrt

fun main() {
	val window = createWindow()
	val defaultShader = createDefaultShader()

	glClearColor(0.0f, 0.0f, 0.1f, 1.0f)

	// Main code will run in here
	while (!glfwWindowShouldClose(window)) {

		// Run the main in-game loop
		inGameLoop(window, defaultShader)
	}
}

fun createWindow() : Long {
	glfwInit()
	glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
	val window = glfwCreateWindow(600, 900, "Starship Game", MemoryUtil.NULL, MemoryUtil.NULL)
	glfwShowWindow(window)
	glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

	glfwSetFramebufferSizeCallback(window) { _, width, height -> glViewport(0, 0, width, height) }

	glfwMakeContextCurrent(window)
	GL.createCapabilities()
	GLUtil.setupDebugMessageCallback(System.out)

	return window
}

fun createDefaultShader() : Int {
	// Write default shader sources
	val vsSource: String = """
        #version 330 core
            
        layout (location = 0) in vec3 pos;
        layout (location = 1) in vec3 inColour;
        out vec3 outColour;
		
		uniform mat4 scale;
		uniform mat4 rotate;
		uniform mat4 view;
		uniform mat4 translate;
        
        void main () 
        {
            gl_Position = view * translate * rotate * scale * vec4(pos, 1.0);
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

fun drawFrame(window:Long, gameObjectList:List<GameObject>, defaultShader:Int) {
	// Draw the frame per loop
	glClear(GL_COLOR_BUFFER_BIT)    // Background colour behind all objects

	// Get window size
	val wPreWidth = MemoryUtil.memAllocInt(1)
	val wPreHeight = MemoryUtil.memAllocInt(1)
	glfwGetFramebufferSize(window, wPreWidth, wPreHeight)
	val wWidth = wPreWidth.get()
	val wHeight = wPreHeight.get()

	// Draw each object
	for (gameObject in gameObjectList) {
		if (gameObject.shader != null) {
			glUseProgram(gameObject.shader)
			gameObject.drawObject(wWidth, wHeight, gameObject.shader)
			glUseProgram(defaultShader)
		}
		else {gameObject.drawObject(wWidth, wHeight, defaultShader)}
	}

	glfwPollEvents()
	glfwSwapBuffers(window)
}

fun inGameLoop(window:Long, defaultShader:Int) {
	// Initialise the in-game setting

	// Object list can be created locally to save required params/args
	var gameObjectList: MutableList<GameObject> = ArrayList()

	// Create the player's ship
	val playerShip = PlayerShip(0f, -500f)
	gameObjectList += playerShip

	// TEMPORARY - Enemy ship to test with
	val enemyShip = EnemyShip(0f, 500f)
	enemyShip.xSpd = 3f
	gameObjectList += enemyShip

	while (!glfwWindowShouldClose(window) && playerShip.hp > 0) {
		// Process all objects for this frame
		// Create a buffer list for any newly made objects
		val newObjectBuffer:MutableList<GameObject?> = ArrayList()

		for (currentObj in gameObjectList) {
			// Check for certain objects with unique cases,
			// otherwise run its default function
			when (currentObj) {
				is PlayerShip -> newObjectBuffer.add(pShipFun(playerShip, window))
				is EnemyShip -> newObjectBuffer.add(eShipFun(enemyShip))
				else -> currentObj.defaultFun()
			}
		}

		// Remove any objects that are off-screen
		gameObjectList = gameObjectList.filter{!(abs(it.xPos.toDouble()) > 450 || abs(it.yPos.toDouble()) > 1000)}.toMutableList()

		// Add new objects to the game list
		// Null objects are filtered and not added to gameObjectList
		for (gameObject in newObjectBuffer.filterNotNull()) {
			gameObjectList.add(gameObject)
		}

		// Check for any collisions
		// gameObjectList = collisionChecks(gameObjectList)

		// Draw the frame
		drawFrame(window, gameObjectList, defaultShader)

		// Limit to 60 fps
		Thread.sleep(100/6.toLong())
	}
}

fun collisionChecks (gameObjectList: List<GameObject>) : List<GameObject> {
	// Create list of objects' data
	for ((indexA, objA) in gameObjectList.withIndex()){
		var indexB = indexA

		// Run checks against all objects with a higher index
		// This prevents two objects being compared twice
		while (indexB < gameObjectList.size) {
			val objB = gameObjectList[indexB]

			val xDist = objA.xPos - objB.xPos
			val yDist = objA.yPos - objB.yPos
			val absDist = sqrt((xDist*xDist) + (yDist*yDist))	// Calculate absolute distance between objects
			val minSpace = objA.size + objB.size	// Objects' spacing if objects were perfectly flush

			if (absDist < minSpace) {
				// Some process that deals with a collision
				TODO("Implement collision handling")
			}
			indexB += 1
		}
	}

	return gameObjectList
}

fun pShipFun(playerShip:PlayerShip, window: Long) : Projectile? {
	var ret: Projectile? = null
	// Decrease shooting cooldown
	if (playerShip.bulletCooldown > 0) {playerShip.bulletCooldown -= 1}

	// Process inputs
	// y-input
	if (glfwGetKey(window, GLFW_KEY_W) == glfwGetKey(window, GLFW_KEY_S)) {playerShip.ySpd = 0f}
	else if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {playerShip.ySpd = 5f}
	// Otherwise, S-key must be pressed, but not W-key
	else {playerShip.ySpd = -5f}

	// x-input
	if (glfwGetKey(window, GLFW_KEY_D) == glfwGetKey(window, GLFW_KEY_A)) {playerShip.xSpd = 0f}
	else if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {playerShip.xSpd = 5f}
	// Otherwise, S-key must be pressed, but not W-key
	else {playerShip.xSpd = -5f}

	// Move ship after processing WASD
	playerShip.move()

	if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
		ret = playerShip.shoot()
	}
	if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {TODO("Implement pause function")}

	return ret	// Return the new Bullet?
}

fun eShipFun(enemyShip: EnemyShip) : GameObject? {
	// Move ship
	enemyShip.move()

	// Reduce shooting cooldown
	enemyShip.bulletCooldown -= 1
	// Attempt to shoot Bullet? and return it to calling fun
	return enemyShip.shoot()
}

