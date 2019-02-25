package appcore.functionality.list

sealed class RelativePos(val index: Int) {
    object First : RelativePos(-1)
    object Last : RelativePos(-2)
    class Explicit(pos: Int) : RelativePos(pos)
    
    fun serialize() = "$index"
}