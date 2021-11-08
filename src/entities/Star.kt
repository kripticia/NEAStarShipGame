package entities

import rendering.Drawable
import kotlin.math.sqrt

class Star(z: Float = 0.0f) : GameObject(null, 1.0f, 1.0f) {
	private val val1 = sqrt(0.75).toFloat()   // Value represents (surd 3) / 2, used to calc exact
												 // positions for hexagon, purely for ease of editing
	override val vertexData = floatArrayOf(
		// Position               Colour
		0.0f, 0.0f, z,       0.7f, 0.7f, 0.7f,   // Centre
		-0.5f, val1, z,      0.7f, 0.7f, 0.7f,   // Top-left
		0.5f, val1, z,       0.7f, 0.7f, 0.7f,   // Top-right
		1.0f, 0.0f, z,       0.7f, 0.7f, 0.7f,   // Far-right
		0.5f, -val1, z,      0.7f, 0.7f, 0.7f,   // Bottom-right
		-0.5f, -val1, z,     0.7f, 0.7f, 0.7f,   // Bottom-left
		-1.0f, 0.0f, z,      0.7f, 0.7f, 0.7f,   // Far-left
	)
	override val indices = intArrayOf(
		0, 1, 2,
		0, 2, 3,
		0, 3, 4,
		0, 4, 5,
		0, 5, 6,
		0, 6, 1
	)
}