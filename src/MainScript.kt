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

	glfwPollEvents()
	glfwSwapBuffers(window)
}

fun inGameLoop(window:Long, defaultShader:Int) {
	// Initialise the in-game setting
	// Object list can be created locally to save required params/args
	var gameObjList: MutableList<GameObj> = ArrayList()

	// Create the player's ship
	val playerShip = PlayerShip(0f, -500f)
	gameObjList.add(playerShip)

	// TEMPORARY - Enemy ship to test with
	val enemyShip = EnemyShip(0f, 500f)
	gameObjList.add(enemyShip)

	// Main cycle will run in here
	while (!glfwWindowShouldClose(window) && playerShip.hp > 0) {
		glClear(GL_COLOR_BUFFER_BIT)

		gameObjList = processGameObjs(window, defaultShader, gameObjList)

		// Check for pause
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			TODO("Implement pause function")
		}

		// Limit to 60 fps
		Thread.sleep(100/6.toLong())
	}
}

fun processGameObjs(window: Long, defaultShader:Int, gameObjListIn:List<GameObj>) : MutableList<GameObj> {
	// Create a buffer list for any newly made objects
	val newObjectBuffer:MutableList<GameObj?> = ArrayList()

	for (currentObj in gameObjListIn) {
		// Check for certain objects with unique cases,
		// otherwise run its default function
		when (currentObj) {
			is PlayerShip -> newObjectBuffer.add(currentObj.pShipFun(window))
			is EnemyShip -> newObjectBuffer.add(currentObj.eShipFun())
			else -> currentObj.defaultFun()
		}
	}

	// Remove any objects that are off-screen
	var gameObjList = gameObjListIn.filter{!(abs(it.xPos.toDouble()) > 500 || abs(it.yPos.toDouble()) > 1400)}.toMutableList()

	// Add new objects to the game list
	// Null objects are filtered and not added to gameObjList
	gameObjList += newObjectBuffer.filterNotNull()

	// Check for any collisions
	gameObjList = collisionChecks(gameObjList)

	// Sort gameObjList in z order - This fixes layering issues
	gameObjList = gameObjList.sortedBy{it.z} as MutableList<GameObj>
	// Draw the frame
	drawObjects(window, defaultShader, gameObjList)

	return gameObjList
}

fun collisionChecks (gameObjList: MutableList<GameObj>) : MutableList<GameObj> {
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
				// println("Collision between ${objA.javaClass} and ${objB.javaClass}")
				removeObjBuffer += collisionHandle(objA, objB) // Currently, not implemented fully
			}
			indexB += 1
		}
	}

	for(obj in removeObjBuffer.filterNotNull()){
		gameObjList.remove(obj)
	}
	return gameObjList
}

// Handles collisions and returns which, if either, object should be removed
fun collisionHandle(objA:GameObj, objB:GameObj): GameObj? {
	return when(objA) {
		is Projectile -> {
			if(objB is PrimaryObject && objA.team != objB.team){
				objB.takeDamage(objA.damage)
				objA
			} else {null}
		}
		is PrimaryObject -> {
			when(objB){
				is Projectile -> objB
				is PrimaryObject -> {objA.hp = 0; objB.hp = 0; null}
				else -> null
			}
		}
		else -> null
	}
}

