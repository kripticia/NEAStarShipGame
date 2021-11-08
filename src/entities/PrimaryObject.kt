package entities

abstract class PrimaryObject(var hp:Int, width:Float = 1.0f, height:Float = 1.0f)
    : GameObject(null, width, height) {

    fun takeDamage(dmg: Int) {
        hp -= dmg
    }
}