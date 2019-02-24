package appcore.functionality.commands

import appcore.functionality.Transaction
import appcore.functionality.list.RelativePos

sealed class Command(
    commandId: String,
    aliases: List<String> = listOf()
) {
    
    
    companion object {
        private val registeredCommandMap = mutableMapOf<String, Command>()
        
        fun allAliases() = registeredCommandMap.keys
        
        fun fromStringAlias(alias: String): Command? =
            registeredCommandMap[alias]
        
    }
    
    init {
        aliases.forEach {
            registeredCommandMap[it] = this
        }
    }
    
    
    object MainAppStop : Command(
        "MainAppStop",
        listOf("exit", "stop", "quit", "x", "xx", "---", "q")
    )
    
    object Test_AddMultiple : Command(
        "Test_AddMultiple",
        listOf("\\*")
    )
    
    class Add(
        val listPos: RelativePos,
        val transaction: Transaction
    ) : Command(
        "Add",
        listOf("\\+", "add")
    )
    
    class Remove(
        val listPos: RelativePos
    ) : Command(
        "Remove",
        listOf("\\-", "remove")
    )
    
    class Move(
        val from: RelativePos,
        val to: RelativePos
    ) : Command(
        "Move",
        listOf("m")
    )
    
}