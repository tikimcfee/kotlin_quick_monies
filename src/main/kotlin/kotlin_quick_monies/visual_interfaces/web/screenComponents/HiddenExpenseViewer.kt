package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.longMonthLongYear
import kotlin_quick_monies.visual_interfaces.web.componentClasses
import kotlin_quick_monies.visual_interfaces.web.componentClasses.hiddenExpenses.hiddenExpenseColumn
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
                        setCssClasses(componentClasses.hiddenExpenses.hiddenExpenseHeaderDate)
                        text("Date")
                    }
                }
                
                val descriptionColumn = div {
                    setCssClasses(hiddenExpenseColumn)
                    span {
                        setCssClasses(componentClasses.hiddenExpenses.hiddenExpenseHeaderDescription)
                        text("Description")
                    }
                }
                
                val amountColumn = div {
                    setCssClasses(hiddenExpenseColumn)
                    span {
                        setCssClasses(componentClasses.hiddenExpenses.hiddenExpenseHeaderAmount)
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
                        dateColumn.dateBlock(it)
                        descriptionColumn.descriptionBlock(it)
                        amountColumn.amountBlock(it)
                    }
            }
        }
    }
}

fun Transaction.separatorName() = groupInfo.sourceSchedule.repetitionSeparator.name

fun Transaction.separatorAmount() = groupInfo.sourceSchedule.repetitionAmount

fun Tag.dateBlock(transaction: Transaction) {
    span {
        setCssClasses(componentClasses.hiddenExpenses.hiddenExpenseDataRowDate)
        text(
            DateTime(transaction.date).longMonthLongYear()
        +"\n(${transaction.separatorAmount()} ${transaction.separatorName()})"
        )
    }
}

fun Tag.descriptionBlock(transaction: Transaction) {
    span {
        setCssClasses(componentClasses.hiddenExpenses.hiddenExpenseDataRowDescription)
        text(
            transaction.description
        )
    }
}

fun Tag.amountBlock(transaction: Transaction) {
    span {
        setCssClasses(componentClasses.hiddenExpenses.hiddenExpenseDataRowAmount)
        text(
            transaction.amount
        )
    }
}
