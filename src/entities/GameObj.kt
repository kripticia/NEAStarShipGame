package entities

abstract class GameObj(val team:Int = 0, val size:Float, width:Float, height:Float, shader:Int?=null, z:Float)
    : GeneralObj(width, height, shader, z) {

    open fun defaultFun() {} // Will be defined per subclass
}