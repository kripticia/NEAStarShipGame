package entities

abstract class Projectile (val damage:Int = 1, team:Int = 0, size:Float = 5.0f, width:Float, height:Float, z:Float)
	: GameObj(team, size, width, height, z=z)
