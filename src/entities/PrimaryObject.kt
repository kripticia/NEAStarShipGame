package entities

abstract class PrimaryObject(var hp:Int, width:Float = 1.0f, height:Float = 1.0f, z: Float = 0.6f)
    : GameObject(z, width, height, null) {

    fun takeDamage(dmg: Int) {
        hp -= dmg
    }
}