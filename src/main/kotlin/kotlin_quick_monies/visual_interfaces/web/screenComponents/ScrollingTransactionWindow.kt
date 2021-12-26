package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.accounting.SortedList
import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.functionality.commands.UPDATE_TRANSACTION_AMOUNT
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.longMonthLongYear
import kotlin_quick_monies.visual_interfaces.web.HomeScreenRenderer
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper
import kotlin_quick_monies.visual_interfaces.web.LensOfTruth
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.addActionAndMethod
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.mainTransaction.transactionRowsGridItem
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.mainTransaction.transactionRowsGridParent
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.div
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.form
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.inputTag
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setAttribute
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setCssClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.span
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.Tag
import org.joda.time.DateTime

// ---------------------------------
// Table Builders
// ---------------------------------

typealias LongRangeToSnapshotEntry = Map.Entry<LongRange, SortedList<TransactionAccountant.Snapshot>>

fun AppStateFunctions.makeAllTransactionTables(parentTag: Tag) {
    with(parentTag) {
        transactionAccountant.computeTransactionDeltas(
            transactionList,
            dateGroupReceiver = { dateMap ->
                dateMap.allEntries().forEach { group ->
                    makeTransactionTable(group)
                }
            })
    }
}


fun Tag.makeTransactionTable(mapEntry: LongRangeToSnapshotEntry) {
    val (dateRange, sortedSnapshots) = mapEntry
    
    /**
     * The author would like to state how absolutely happy it has made him
     * to type, in a semi-serious fashion, and with a completely and perfectly
     * valid set of syntactic and semantic uses, the equivalent of:
     *
     * --- with the lens of truth, isolate the hidden transactions
     *          and { pretend, somewhere else, the other side of the lens
     *          is showing the truth } - [on this side, we allow the hidden,
     *          to hide]
     *          ---- ~ (a proud and humble dork)
     */
    val nonHiddenTransactions = with(LensOfTruth) {
        sortedSnapshots.removeHiddenTransactions().toList()
    }.also {
        if (it.isEmpty()) return
    }
    
    div {
        span {
            text(DateTime(dateRange.first).longMonthLongYear())
        }
    }
    
    div {
        setCssClasses(transactionRowsGridParent)
        
        span {
            setCssClasses(transactionRowsGridItem)
            text("When ⏳")
        }
        
        span {
            setCssClasses(transactionRowsGridItem)
            text("What 🔍")
        }
        
        span {
            setCssClasses(transactionRowsGridItem)
            text("Moneys 💸")
        }

        span {
            setCssClasses(transactionRowsGridItem)
            text("Update $")
        }

        span {
            setCssClasses(transactionRowsGridItem)
            text("After ✅")
        }
        
        nonHiddenTransactions.forEach { snapshot ->
            mainTransactionDateBlock(snapshot)
            mainTransactionDescriptionBlock(snapshot)
            mainTransactionAmountBlock(snapshot)
            mainTransactionUpdateAmountBlock(snapshot)
            mainTransactionAmountAfterBlock(snapshot)
        }
    }
}

fun Tag.mainTransactionDateBlock(snapshot: TransactionAccountant.Snapshot) {
    with(snapshot.transaction) {
        span {
            setCssClasses(transactionRowsGridItem)
            text(formattedDate())
        }
    }
}

fun Tag.mainTransactionDescriptionBlock(snapshot: TransactionAccountant.Snapshot) {
    with(snapshot.transaction) {
        span {
            setCssClasses(transactionRowsGridItem)
            text(description)
        }
    }
}

fun Tag.mainTransactionAmountBlock(snapshot: TransactionAccountant.Snapshot) {
    with(snapshot.transaction) {
        span {
            setCssClasses(transactionRowsGridItem)
            text(formattedAmount())
        }
    }
}

fun Tag.mainTransactionUpdateAmountBlock(snapshot: TransactionAccountant.Snapshot) {
    with(snapshot.transaction) {
        span {
            setCssClasses(transactionRowsGridItem)
            form {
                addActionAndMethod(JavalinWebFrameworkWrapper.Route.UpdateTransactionAmount)
                inputTag(
                    "...",
                    HomeScreenRenderer.FormParam.TRANSACTION_UPDATE_AMOUNT,
                    uniqueId = id
                ).setAttribute("style", "width: 100%")
            }
        }
    }
}

fun Tag.mainTransactionAmountAfterBlock(snapshot: TransactionAccountant.Snapshot) {
    with(snapshot.transaction) {
        span {
            setCssClasses(transactionRowsGridItem)
            text(snapshot.formattedAfter())
        }
    }
}
