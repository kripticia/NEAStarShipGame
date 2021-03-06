package entities

import kotlin.math.sqrt
import kotlin.random.Random

private const val z = 0f
private val val1 = sqrt(0.75).toFloat()   // Value represents (surd 3) / 2, used to calc exact
											 // positions for hexagon, purely for ease of editing
private val vertexData = floatArrayOf(
	// Position               Colour
	0.0f, 0.0f, z,       0.7f, 0.7f, 0.7f,   // Centre
	-0.5f, val1, z,      0.7f, 0.7f, 0.7f,   // Top-left
	0.5f, val1, z,       0.7f, 0.7f, 0.7f,   // Top-right
	1.0f, 0.0f, z,       0.7f, 0.7f, 0.7f,   // Far-right
	0.5f, -val1, z,      0.7f, 0.7f, 0.7f,   // Bottom-right
	-0.5f, -val1, z,     0.7f, 0.7f, 0.7f,   // Bottom-left
	-1.0f, 0.0f, z,      0.7f, 0.7f, 0.7f,   // Far-left
)
private val indices = intArrayOf(
	0, 1, 2,
	0, 2, 3,
	0, 3, 4,
	0, 4, 5,
	0, 5, 6,
	0, 6, 1
)

class Star : GeneralObj(vertexData, indices, 5.0f, 5.0f, null, 0.0f) {
	private val random = Random(System.nanoTime())

	init {
		// Created at a random position
	    xPos = (800 * random.nextFloat()) - 400
		yPos = (2000 * random.nextFloat()) - 1000
		ySpd = -1f
	}

	fun recycle() {
		// Star will be given a new, random x-position at the top of the screen
		xPos = (800 * random.nextFloat()) - 400
		yPos = (300 * random.nextFloat()) + 1200
	}
}