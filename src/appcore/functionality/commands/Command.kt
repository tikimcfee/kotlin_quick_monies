package appcore.functionality.commands

import appcore.functionality.Transaction
import appcore.functionality.list.RelativePos

abstract sealed class Command() {
    abstract fun singleLineSerialization(): String
    
    object MainAppStop : Command() {
        override fun singleLineSerialization() = "MainAppStop"
    }
    
    object Test_AddMultiple : Command() {
        override fun singleLineSerialization() = "Test_AddMultiple"
    }
    
    class Add(
        val listPos: RelativePos,
        val transaction: Transaction
    ) : Command() {
        override fun singleLineSerialization() =
            "add ${listPos.serialize()} " +
            "${transaction.amount} " +
            "${transaction.date.time} " +
            transaction.description
    }
    
    class Remove(
        val listPos: RelativePos
    ) : Command() {
        override fun singleLineSerialization() =
            "remove ${listPos.serialize()}"
    }
    
    class Move(
        val from: RelativePos,
        val to: RelativePos
    ) : Command() {
        override fun singleLineSerialization() =
            "move ${from.serialize()} ${to.serialize()}"
    }
    
}