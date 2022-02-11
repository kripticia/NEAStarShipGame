package entities

abstract class PrimaryObj(vertexData:FloatArray, indices:IntArray,
                          width:Float = 1.0f, height:Float = 1.0f, shader:Int? = null, z:Float,
                          var hp:Int, team:Int = 0, size:Float = 50.0f)
    : GameObj(vertexData, indices, width, height, shader, z, team, size) {

    fun takeDamage(dmg: Int) {
        hp -= dmg
    }
}