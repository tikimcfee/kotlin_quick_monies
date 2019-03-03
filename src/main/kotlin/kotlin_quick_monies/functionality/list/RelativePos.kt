package kotlin_quick_monies.functionality.list

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import kotlin.reflect.KClass

@TypeFor(field = "index", adapter = RelativePosTypeAdapter::class)
sealed class RelativePos(
    open val index: Int
) {
    
    class First : RelativePos(-1)
    class Last : RelativePos(-2)
    class Explicit(override val index: Int) : RelativePos(index)
}

class RelativePosTypeAdapter : TypeAdapter<RelativePos> {
    override fun classFor(type: Any): KClass<out RelativePos> = when (type as Int) {
        -1 -> RelativePos.First::class
        -2 -> RelativePos.Last::class
        else -> RelativePos.Explicit::class
    }
    
}