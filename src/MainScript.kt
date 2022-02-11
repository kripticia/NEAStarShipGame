import entities.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL45.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil
import java.util.*
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

fun drawObjects(window:Long, defaultShader:Int, objList:List<GeneralObj>) {
	// Get window size
	val wPreWidth = MemoryUtil.memAllocInt(1)
	val wPreHeight = MemoryUtil.memAllocInt(1)
	glfwGetFramebufferSize(window, wPreWidth, wPreHeight)
	val wWidth = wPreWidth.get()
	val wHeight = wPreHeight.get()

	// Draw each object
	for (obj in objList) {
		if (obj.shader != null) {
			glUseProgram(obj.shader)
			obj.drawObject(wWidth, wHeight, obj.shader)
			glUseProgram(defaultShader)
		}
		else {obj.drawObject(wWidth, wHeight, defaultShader)}
	}
}

fun inGameLoop(window:Long, defaultShader:Int) {
	// Initialise the in-game setting
	glClear(GL_COLOR_BUFFER_BIT)

	// Used to spawn objects using random generation
	val random = Random(System.nanoTime())

	// Object lists for different object types
	val starList :MutableList<Star> = ArrayList()
	val gameObjList :MutableList<GameObj> = ArrayList()
	// var uiObjList :MutableList<GameObj> = ArrayList()

	// Create the player's ship
	val playerShip = PlayerShip(0f, -500f)
	gameObjList.add(playerShip)

	// Place stars around the screen at random
	for (i in 0..30) {starList.add(Star())}

	var lastSpawnTime = 0.0
	var nextSpawnDelay = 1 + (7 * random.nextDouble())

	var enemiesKilled = 0
	// Game uses "window open" time to track how long the player has survived
	// This is reset to zero when the loop starts

	// Main cycle will run in here
	while (!glfwWindowShouldClose(window) && playerShip.hp > 0) {
		glClear(GL_COLOR_BUFFER_BIT)

		// Spawn enemies after random intervals
		if (glfwGetTime() - lastSpawnTime >= nextSpawnDelay) {
			// Spawn a new ship
			val xPos = (800 * random.nextFloat()) - 400
			gameObjList.add(EnemyShip(xPos, 1000f))

			lastSpawnTime = glfwGetTime()
			// Spawn times will vary between 1 and 8 seconds
			nextSpawnDelay = 1 + (7 * random.nextDouble())
		}

		processStars(window, defaultShader, starList)
		enemiesKilled += processGameObjs(window, defaultShader, gameObjList)

		// Check for pause
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			TODO("Implement pause function")
		}

		glfwSwapBuffers(window)
		glfwPollEvents()
		// Limit to 60 fps
		Thread.sleep(100/6.toLong())
	}
}

fun processStars(window:Long, defaultShader: Int, starList:MutableList<Star>) {
	for (star in starList) {
		star.move()
		if(star.yPos < -1100f) {
			star.recycle()
		}
	}
	drawObjects(window, defaultShader, starList)
}

fun processGameObjs(window: Long, defaultShader:Int, gameObjList:MutableList<GameObj>) : Int {
	// Create buffer lists for adding objects after iterations
	// This will avoid ConcurrentModificationException
	val newObjBuffer:MutableList<GameObj?> = ArrayList()

	// Keep track of whether any enemies are killed
	var enemiesKilled = 0

	for (currentObj in gameObjList) {
		// Check for certain objects with unique cases,
		// otherwise run its default function
		when (currentObj) {
			is PlayerShip -> newObjBuffer.add(currentObj.pShipFun(window))
			is EnemyShip -> newObjBuffer.add(currentObj.eShipFun())
			else -> currentObj.defaultFun()
		}
	}

	// Remove any objects that are now off-screen
	gameObjList.filter{!(abs(it.xPos) > 500 || abs(it.yPos) > 1200)}

	// Add new objects to the game list
	// Null objects are filtered and not added to gameObjList
	gameObjList += (newObjBuffer.filterNotNull())

	// Check for any collisions
	// Objects can be removed within the procedure
	enemiesKilled += collisionChecks(gameObjList)

	// Sort gameObjList in z order - This fixes layering issues
	gameObjList.sortedBy{it.z}
	// Draw the frame
	drawObjects(window, defaultShader, gameObjList)

	return enemiesKilled
}

fun collisionChecks (gameObjList: MutableList<GameObj>) : Int {
	// Prepare list of obj to remove
	val removeObjBuffer = ArrayList<GameObj?>()

	// Compare every object
	for ((indexA, objA) in gameObjList.withIndex()){
		var indexB = indexA + 1

		// Run checks against all objects with a higher index
		// This prevents two objects being compared twice
		while (indexB < gameObjList.size) {
			val objB = gameObjList[indexB]

			val xDist = objA.xPos - objB.xPos
			val yDist = objA.yPos - objB.yPos
			val absDist = sqrt((xDist*xDist) + (yDist*yDist))	// Calculate absolute distance between objects
			val minSpace = objA.size + objB.size	// Objects' spacing if objects were perfectly flush

			if (absDist < minSpace && objA.team != objB.team) {
				removeObjBuffer += collisionHandle(objA, objB)
			}
			indexB += 1
		}
	}

	// Check for primary objects with no hp, and remove them
	// Enemy kills will increment the kill counter
	var enemiesKilled = 0
	for (obj in gameObjList) {
		if (obj is PrimaryObj && obj.hp <= 0) {
			removeObjBuffer += obj
			if (obj is EnemyShip) {enemiesKilled += 1}
		}
	}

	for (obj in removeObjBuffer) {gameObjList.remove(obj)}
	return enemiesKilled
}

// Handles collisions and returns which, if either, object should be removed
fun collisionHandle(objA:GameObj, objB:GameObj): GameObj? {
	return when(objA) {
		is Projectile -> {
			if(objB is PrimaryObj){
				objB.takeDamage(objA.damage)
				objA
			} else {null}
		}
		is PrimaryObj -> {
			when(objB){
				is Projectile -> {objA.takeDamage(objB.damage); objB}
				is PrimaryObj -> {objA.hp = 0; objB.hp = 0; null}
				else -> null
			}
		}
		else -> null
	}
}

