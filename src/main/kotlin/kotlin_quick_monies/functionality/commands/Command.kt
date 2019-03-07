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
    
    data class Add(
        val listPos: RelativePos,
        val transaction: Transaction
    ) : Command("add")
    
    data class Remove(
        val listPos: RelativePos
    ) : Command("remove")
    
    data class Move(
        val from: RelativePos,
        val to: RelativePos
    ) : Command("move")
    
    data class AddMonthlyTransaction(
        val monthsToAdd: Int,
        val transactionTemplate: Transaction
    ) : Command("add_monthly_transaction")
    
}

class CommandTypeAdapter : TypeAdapter<Command> {
    override fun classFor(type: Any): KClass<out Command> = when (type as String) {
        "MainAppStop" -> MainAppStop::class
        "add" -> Add::class
        "remove" -> Remove::class
        "move" -> Move::class
        "add_monthly_transaction" -> AddMonthlyTransaction::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
    
}
