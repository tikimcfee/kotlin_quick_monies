package kotlin_quick_monies.visual_interfaces.web

import io.javalin.Context
import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.accounting.SortedList
import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.transfomers.TransactionsAsText
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.monthDayYearFull
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper.Route.*
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.Form
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.Table
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.Tag
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.button
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.div
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.form
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.lineBreak
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.newField
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.setAttribute
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.table
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.td
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.SimpleHTML.tr
import kotlinx.css.*
import kotlinx.css.Float
import org.joda.time.DateTime
import java.util.*


object BasicTableRenderer {
    
    enum class FormParam(val id: String) {
        ADD_TRANSACTION_AMOUNT("inputTransactionAmount"),
        ADD_TRANSACTION_DESCRIPTION("inputTransactionDescription"),
        ADD_TRANSACTION_DATE("inputTransactionDate"),
        
        ADD_SIMPLE_MONTHLY_TRANSACTION_AMOUNT("inputMonthlyTransactionAmount"),
        ADD_SIMPLE_MONTHLY_TRANSACTION_MONTHS_TO_ADD("inputMonthlyTransactionCount"),
        ADD_SIMPLE_MONTHLY_TRANSACTION_START_DATE("inputMonthlyTransactionStartDate"),
        ADD_SIMPLE_MONTHLY_TRANSACTION_DESCRIPTION("inputMonthlyTransactionDescription"),
        
        ACTION_REMOVE_FROM_POSITION_INDEX("actionRemoveFromPositionIndex")
    }
    
    fun AppStateFunctions.renderResponseTo(context: Context) {
        val rawHtml = with(SimpleHTML) {
            html {
                applyDeviceDependentSizing()
                
                setStyles {
                    rule(".clearfix::after") {
                        content = QuotedString("")
                        clear = Clear.both
                        display = Display.table
                    }
                    
                    kotlinx.css.table {
                        padding = "10 10 10 10"
                        backgroundColor = Color("#EFEFEF")
                        borderRadius = 4.pt
                        width = 100.pct
                    }
                    
                    rule("tr:nth-child(even)") {
                        backgroundColor = Color.lightGrey
                    }
                    
                    rule(".transaction-input-box") {
                        backgroundColor = Color("#EFEFEF44")
                        padding = "8 8 8 8"
                        
                        width = LinearDimension.fitContent
                        height = LinearDimension.fitContent
                        
                        float = Float.left
                        marginRight = 16.pt
                    }
                    
                    rule(".main-transaction-window") {
                        padding = "2 2 2 2"
                        width = 100.pct
                        height = 60.pct
                        
                        scrollBehavior = ScrollBehavior.auto
                        overflow = Overflow.auto
                        
                        backgroundColor = Color("#EFEFEF44")
                    }
                }
                
                body {
                    
                    fun transactionInputs() {
                        div {
                            singleTransactionInput().apply {
                                setAttribute("class", "transaction-input-box")
                            }
                            monthlyTransactionInput().apply {
                                setAttribute("class", "transaction-input-box")
                            }
                        }
                        
                    }
                    
                    fun transactionTable() {
                        div {
                            setAttribute("class", "main-transaction-window")
                            makeAllTransactionTables(this)
                        }
                    }
                    
                    lineBreak()
                    transactionInputs()
                    lineBreak()
                    transactionTable()
                    
                    // Hidden form input for 'remove' button functionality; a little AJAX would do it but eh... not yet.
                    form {
                        addActionAndMethod(RemoveIndex)
                        setAttribute("id", ACTION_REMOVE_FROM_POSITION_INDEX.name)
                        hiddenInput(ACTION_REMOVE_FROM_POSITION_INDEX)
                    }
                }
            }
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
    
    // ---------------------------------
    // Transaction Actions
    // ---------------------------------
    fun Tag.singleTransactionInput(): Tag = div {
        lineBreak()
        text("<strong>- Transaction Info -</strong>")
        lineBreak()
        
        form {
            addActionAndMethod(AddTransaction)
            
            newField("What it's for? : ", ADD_TRANSACTION_DESCRIPTION)
            lineBreak()
            
            newField("How much? : ", ADD_TRANSACTION_AMOUNT)
            lineBreak()
            
            newField(
                "For what date? : ",
                ADD_TRANSACTION_DATE,
                input = { dateInputField ->
                    dateInputField.setAttribute("value",
                        with(TransactionsAsText.QuickMoniesDates) {
                            DateTime().format()
                        })
                })
            lineBreak()
            
            button { text("Stick it in there") }
        }
    }
    
    fun Tag.monthlyTransactionInput(): Tag = div {
        lineBreak()
        text("<strong>- Simple Repeated Transactions -</strong>")
        lineBreak()
        
        form {
            addActionAndMethod(AddMonthlyTransaction)
            newField("What it's for? : ", ADD_SIMPLE_MONTHLY_TRANSACTION_DESCRIPTION)
            lineBreak()
            
            newField("Monthly amount : ", ADD_SIMPLE_MONTHLY_TRANSACTION_AMOUNT)
            lineBreak()
            
            newField(
                "Start date : ",
                ADD_SIMPLE_MONTHLY_TRANSACTION_START_DATE,
                input = { dateInputField ->
                    dateInputField.setAttribute("value",
                        with(TransactionsAsText.QuickMoniesDates) {
                            DateTime().format()
                        })
                })
            lineBreak()
            
            newField(
                "Months to adds : ",
                ADD_SIMPLE_MONTHLY_TRANSACTION_MONTHS_TO_ADD,
                input = { dateInputField ->
                    dateInputField.setAttribute("value", "12")
                })
            lineBreak()
            
            
            CSSBuilder().apply {
            
            }
            
            button { text("Stick a bunch of 'em in there.") }
        }
    }
    
    
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
    
    fun Table.makeMonthTransactionSeparator(
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
    
    fun Table.tableHeader() {
        tr {
            td(align = "left") { text("Date") }
            td(align = "left") { text("Description") }
            td(align = "left") { text("Transaction Amount") }
            td(align = "left") { text("After Transaction") }
        }
    }
    
    fun Table.makeTransactionSnapshotRow(
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
                        setAttribute("value", snapshot.sourceListPosition.toString())
                        setAttribute(
                            "name",
                            ACTION_REMOVE_FROM_POSITION_INDEX.id
                        )
                        setAttribute(
                            "form",
                            ACTION_REMOVE_FROM_POSITION_INDEX.name
                        )
                    }
                }
            }
        }
    }
    
    // ---------------------------------
    // Form helpers
    // ---------------------------------
    
    private fun Form.addActionAndMethod(route: JavalinWebFrameworkWrapper.Route) {
        with(SimpleHTML) {
            this@addActionAndMethod.setAttribute("action", route.path)
            this@addActionAndMethod.setAttribute("method", route.method)
        }
    }
}
