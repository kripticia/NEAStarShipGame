package entities

abstract class PrimaryObj(var hp:Int, team:Int = 0, size:Float = 50.0f, width:Float = 1.0f, height:Float = 1.0f,
                          shader:Int? = null, z:Float)
    : GameObj(team, size, width, height, shader, z) {

    fun takeDamage(dmg: Int) {
        hp -= dmg
    }
}