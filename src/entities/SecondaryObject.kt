package entities

abstract class SecondaryObject (val width:Float = 1.0f, val height:Float = 1.0f, z:Float = 0.4f)
	: GameObject(z, 1.0f, 1.0f, null) {
}