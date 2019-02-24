package appcore.functionality.list

sealed class RelativePos {
    object First : RelativePos()
    object Last : RelativePos()
    class Explicit(val pos: Int) : RelativePos()
}