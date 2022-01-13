package entities

abstract class Projectile (val damage:Int = 1, width:Float, height:Float, size:Float = 5.0f, shader:Int? = null)
	: GameObject(width, height, size, shader) {
}
