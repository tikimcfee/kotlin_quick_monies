package kotlin_quick_monies.visual_interfaces.cli

import appcore.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import appcore.functionality.execute
import kotlin_quick_monies.functionality.list.TransactionList
import appcore.functionality.restoreState
import kotlin_quick_monies.transfomers.TransactionsAsText

class TerminalInteractionLoop {
    
    private var shouldContinue = true
    
    fun loop(appStateFunctions: AppStateFunctions) {
        clear()
        with(appStateFunctions) {
            restoreState()
            drawScreen(this)
            stateLoop()
        }
    }
    
    private fun AppStateFunctions.stateLoop() {
        while (shouldContinue) {
            // Grab Input
            print("What's your poison? :: ")
            val input = readLine()
            commandHistorian.recordRawCommand(input ?: "--end-of-input--")
            
            val commandInput = commandProcessor.parseStringCommand(input)
            
            // Run it
            commandInput.execute(
                this
            )
            
            drawScreen(this)
        }
    }
    
    private fun drawScreen(appStateFunctions: AppStateFunctions) {
        with(appStateFunctions) {
            // Redraw the basic stuff, including a clear
            clear()
            
            outputTransactions(transactionList)
            println()
            
            outputAccounting(transactionList, transactionAccountant)
            println()
        }
    }
    
    private fun clear() {
        //        println("\u001Bc")
    }
    
    
    private fun stop() {
        shouldContinue = false
    }
    
    private fun outputTransactions(transactionList: TransactionList) {
        transactionList
            .transactions
            .forEachIndexed { index, transaction ->
                println("${index + 1}. ${TransactionsAsText.Simple.render(transaction)}")
            }
    }
    
    private fun outputAccounting(
        transactionList: TransactionList,
        transactionAccountant: TransactionAccountant
    ) {
        transactionAccountant
            .computeTransactionDeltas(transactionList)
            .forEachIndexed { index, snapshot ->
                println("${index + 1}. ${TransactionsAsText.Simple.render(snapshot)}")
            }
    }
    
}

