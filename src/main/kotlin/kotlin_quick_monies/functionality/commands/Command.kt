package kotlin_quick_monies.functionality.commands

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import kotlin_quick_monies.functionality.commands.Command.*
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.list.RelativePos
import kotlin.reflect.KClass

@TypeFor(field = "commandName", adapter = CommandTypeAdapter::class)
sealed class Command(
    val commandName: String
) {
    
    class MainAppStop : Command("MainAppStop")
    
    class Test_AddMultiple : Command("Test_AddMultiple")
    
    class Add(
        val listPos: RelativePos,
        val transaction: Transaction
    ) : Command("add")
    
    class Remove(
        val listPos: RelativePos
    ) : Command("remove")
    
    class Move(
        val from: RelativePos,
        val to: RelativePos
    ) : Command("move")
    
}

class CommandTypeAdapter : TypeAdapter<Command> {
    override fun classFor(type: Any): KClass<out Command> = when (type as String) {
        "rectangle" -> MainAppStop::class
        "circle" -> Test_AddMultiple::class
        "add" -> Add::class
        "remove" -> Remove::class
        "move" -> Move::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
    
}