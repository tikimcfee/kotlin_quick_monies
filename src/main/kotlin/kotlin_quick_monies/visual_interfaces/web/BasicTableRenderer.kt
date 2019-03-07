package kotlin_quick_monies.visual_interfaces.web

import appcore.functionality.AppStateFunctions
import io.javalin.Context
import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.transfomers.TransactionsAsText
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import kotlin_quick_monies.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper.Route.*
import org.joda.time.DateTime

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
    
    fun SimpleHTML.Form.addActionAndMethod(route: JavalinWebFrameworkWrapper.Route) {
        with(SimpleHTML) {
            this@addActionAndMethod.setAttribute("action", route.path)
            this@addActionAndMethod.setAttribute("method", route.method)
        }
    }
    
    fun AppStateFunctions.renderResponseTo(context: Context) {
        val rawHtml = with(SimpleHTML) {
            html {
                fun singleTransactionInput() {
                    lineBreak()
                    text("<strong>- Transaction Info -</strong>")
                    lineBreak()
                    
                    form {
                        addActionAndMethod(AddTransaction)
                        
                        newField("How much money is coming in, or going out? : ", ADD_TRANSACTION_AMOUNT)
                        lineBreak()
                        
                        newField("What it's for? : ", ADD_TRANSACTION_DESCRIPTION)
                        lineBreak()
                        
                        newField(
                            "When did it happen? : ",
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
                
                fun monthlyTransactionInput() {
                    lineBreak()
                    text("<strong>- Simple Repeated Transactions -</strong>")
                    lineBreak()
                    
                    form {
                        addActionAndMethod(AddMonthlyTransaction)
                        
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
                        
                        newField("What it's for? : ", ADD_SIMPLE_MONTHLY_TRANSACTION_DESCRIPTION)
                        lineBreak()
                        
                        button { text("Stick a bunch of 'em in there.") }
                    }
                    
                }
                
                fun transactionTable() {
                    table {
                        tr {
                            td(align = "left") { text("Date") }
                            td(align = "left") { text("Transaction Amount") }
                            td(align = "left") { text("After Transaction") }
                            td(align = "left") { text("Description") }
                        }
                        
                        fun makeRow(
                            snapshot: TransactionAccountant.Snapshot
                        ) {
                            tr {
                                with(snapshot.transaction) {
                                    td { text(formattedDate()) }
                                    td { text(formattedAmount()) }
                                    td { text(snapshot.formattedAfter()) }
                                    td { text(description) }
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
                        
                        transactionAccountant
                            .computeTransactionDeltas(transactionList)
                            .forEach(::makeRow)
                    }
                }
                
                singleTransactionInput()
                monthlyTransactionInput()
                lineBreak()
                transactionTable()
                
                form {
                    addActionAndMethod(RemoveIndex)
                    setAttribute("id", ACTION_REMOVE_FROM_POSITION_INDEX.name)
                    hiddenInput(ACTION_REMOVE_FROM_POSITION_INDEX)
                }
            }
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
}
