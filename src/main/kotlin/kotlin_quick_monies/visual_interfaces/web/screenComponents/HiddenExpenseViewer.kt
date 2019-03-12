package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.longMonthLongYear
import kotlin_quick_monies.visual_interfaces.web.componentClasses.hiddenExpenses.hiddenExpenseColumn
import kotlin_quick_monies.visual_interfaces.web.componentClasses.hiddenExpenses.hiddenExpenseDataRow

import kotlin_quick_monies.visual_interfaces.web.componentClasses.hiddenExpenses.hiddenExpenseRootContainer
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.div
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.lineBreak
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setCssClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.span
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.Tag
import org.joda.time.DateTime


fun AppStateFunctions.renderHiddenTransactions(parentTag: Tag) {
    with(parentTag) {
        with(LensOfTruth) {
            div {
                lineBreak()
                text("<strong>- Hidden Expenses -</strong>")
                lineBreak()
                
                setCssClasses(hiddenExpenseRootContainer)
                
                val dateColumn = div {
                    setCssClasses(hiddenExpenseColumn)
                    span {
                        
                        text("Date")
                    }
                }
                
                val descriptionColumn = div {
                    setCssClasses(hiddenExpenseColumn)
                    span {
                        
                        text("Description")
                    }
                }
                
                val amountColumn = div {
                    setCssClasses(hiddenExpenseColumn)
                    span {
                        
                        text("Transaction Amount")
                    }
                }
                
                val knownGroups = mutableSetOf<String>()
                transactionList
                    .transactions
                    .justTheHiddenOnes()
                    .filter { it.groupInfo.id !in knownGroups }
                    .onEach { knownGroups += it.groupInfo.id }
                    .forEach {
                        dateColumn.hiddenExpenseDateBlock(it)
                        descriptionColumn.hiddenExpenseDescriptionBlock(it)
                        amountColumn.hiddenExpenseAmountBlock(it)
                    }
            }
        }
    }
}

fun Transaction.separatorName() = groupInfo.sourceSchedule.repetitionSeparator.name

fun Transaction.separatorAmount() = groupInfo.sourceSchedule.repetitionAmount

fun Tag.hiddenExpenseDateBlock(transaction: Transaction) {
    span {
        setCssClasses(hiddenExpenseDataRow)
        text(
            DateTime(transaction.date).longMonthLongYear()
                + "\n(${transaction.separatorAmount()} ${transaction.separatorName()})"
        )
    }
}

fun Tag.hiddenExpenseDescriptionBlock(transaction: Transaction) {
    span {
        setCssClasses(hiddenExpenseDataRow)
        text(
            transaction.description
        )
    }
}

fun Tag.hiddenExpenseAmountBlock(transaction: Transaction) {
    span {
        setCssClasses(hiddenExpenseDataRow)
        text(
            transaction.amount
        )
    }
}
