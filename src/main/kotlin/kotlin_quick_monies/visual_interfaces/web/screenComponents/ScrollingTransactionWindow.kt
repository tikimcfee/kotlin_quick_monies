package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.accounting.SortedList
import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.monthDayYearFull
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.FormParam.ACTION_REMOVE_TRANSACTION_BY_ID
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowAfter
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowAfterHeader
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowAmount
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowAmountHeader
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowDate
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowDateHeader
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowDeleteButton
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowDescription
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowDescriptionHeader
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowInfoContainer
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowInfoContainerHeaders
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowSeparator
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionTableSectionContainer
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.div
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.hiddenInputButton
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setCssClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.span
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.Tag
import org.joda.time.DateTime

// ---------------------------------
// Table Builders
// ---------------------------------

fun AppStateFunctions.makeAllTransactionTables(parentTag: Tag) {
    with(parentTag) {
        transactionAccountant.computeTransactionDeltas(
            transactionList,
            dateGroupReceiver = { dateMap ->
                dateMap
                    .allEntries()
                    .forEach { mapEntry ->
                        makeTransactionTable(mapEntry)
                    }
            })
    }
}

typealias LongRangeToSnapshotEntry = Map.Entry<LongRange, SortedList<TransactionAccountant.Snapshot>>

fun Tag.makeTransactionTable(mapEntry: LongRangeToSnapshotEntry) {
    div {
        setCssClasses(transactionTableSectionContainer)
        
        snapshotEntryDataHeader(DateTime(mapEntry.key.first).monthDayYearFull())
        div { setCssClasses(transactionRowSeparator) }
        
        mapEntry.value.forEach { snapshot ->
            snapshotEntryDataRow(snapshot)
            div { setCssClasses(transactionRowSeparator) }
        }
    }
}

fun Tag.snapshotEntryDataHeader(
    headerText: String
) {
    div {
        setCssClasses(transactionRowInfoContainerHeaders)
        
        span {
            text(headerText)
        }
        
        span {
            setCssClasses(transactionRowDateHeader)
            text("Date")
        }
        span {
            setCssClasses(transactionRowDescriptionHeader)
            text("Description")
        }
        span {
            setCssClasses(transactionRowAmountHeader)
            text("Transaction Amount")
        }
        span {
            setCssClasses(transactionRowAfterHeader)
            text("After Transaction")
        }
    }
}

fun Tag.snapshotEntryDataRow(
    snapshot: TransactionAccountant.Snapshot
) {
    with(snapshot.transaction) {
        div {
            setCssClasses(transactionRowInfoContainer)
            
            hiddenInputButton(
                "Remove",
                snapshot.transaction.id,
                ACTION_REMOVE_TRANSACTION_BY_ID
            ).apply {
                setCssClasses(transactionRowDeleteButton)
            }
            
            span {
                setCssClasses(transactionRowDate)
                text(formattedDate())
            }
            span {
                setCssClasses(transactionRowDescription)
                text(description)
            }
            span {
                setCssClasses(transactionRowAmount)
                text(formattedAmount())
            }
            span {
                setCssClasses(transactionRowAfter)
                text(snapshot.formattedAfter())
            }
        }
    }
}
