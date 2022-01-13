package entities

abstract class PrimaryObject(var hp:Int, width:Float = 1.0f, height:Float = 1.0f, size:Float = 50.0f, shader:Int? = null)
    : GameObject(width, height, size, shader) {

    fun takeDamage(dmg: Int) {
        hp -= dmg
    }
}