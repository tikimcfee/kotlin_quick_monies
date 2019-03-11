package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.accounting.SortedList
import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.monthDayYearFull
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.button
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.lineBreak
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setAttribute
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.table
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.td
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.tr
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
                        lineBreak()
                    }
            })
    }
}

fun Tag.makeTransactionTable(mapEntry: Map.Entry<LongRange, SortedList<TransactionAccountant.Snapshot>>) {
    table {
        makeMonthTransactionSeparator(DateTime(mapEntry.key.first))
        tableHeader()
        mapEntry.value.forEach { snapshot ->
            makeTransactionSnapshotRow(snapshot)
        }
    }
}

fun SimpleHTML.Table.makeMonthTransactionSeparator(
    forDate: DateTime
) {
    tr {
        td { }
        td { }
        td { }
        td {
            text(forDate.monthDayYearFull())
        }
    }
}

fun SimpleHTML.Table.tableHeader() {
    tr {
        td(align = "left") { text("Date") }
        td(align = "left") { text("Description") }
        td(align = "left") { text("Transaction Amount") }
        td(align = "left") { text("After Transaction") }
    }
}

fun SimpleHTML.Table.makeTransactionSnapshotRow(
    snapshot: TransactionAccountant.Snapshot
) {
    tr {
        with(snapshot.transaction) {
            td { text(formattedDate()) }
            td { text(description) }
            td { text(formattedAmount()) }
            td { text(snapshot.formattedAfter()) }
            td {
                button {
                    text("Remove")
                    setAttribute("value", snapshot.transaction.id)
                    setAttribute(
                        "name",
                        BasicTableRenderer.FormParam.ACTION_REMOVE_TRANSACTION_BY_ID.id
                    )
                    setAttribute(
                        "form",
                        BasicTableRenderer.FormParam.ACTION_REMOVE_TRANSACTION_BY_ID.name
                    )
                }
            }
        }
    }
}
