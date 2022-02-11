package entities

abstract class Projectile (vertexData:FloatArray, indices:IntArray,
						   width:Float, height:Float, shader:Int? = null, z:Float,
						   val damage:Int = 1, team:Int = 0, size:Float = 5.0f)
	: GameObj(vertexData, indices, width, height, shader, z, team, size)
