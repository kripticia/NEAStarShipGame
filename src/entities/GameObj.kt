package entities

abstract class GameObj(vertexData:FloatArray, indices:IntArray,
                       width:Float, height:Float, shader:Int?=null, z:Float,
                       val team:Int = 0, val size:Float = 10f)
    : GeneralObj(vertexData, indices, width, height, shader, z) {

    open fun defaultFun() {} // Will be defined per subclass
}