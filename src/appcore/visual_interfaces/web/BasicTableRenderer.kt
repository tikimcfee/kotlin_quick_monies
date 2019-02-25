package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.accounting.TransactionAccountant
import appcore.transfomers.TransactionsAsText
import appcore.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import appcore.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import appcore.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.*
import appcore.visual_interfaces.web.JavalinWebFrameworkWrapper.Route.*
import io.javalin.Context
import java.util.*

object BasicTableRenderer {
    
    enum class FormParam(val id: String) {
        ADD_TRANSACTION_AMOUNT("inputTransactionAmount"),
        ADD_TRANSACTION_DESCRIPTION("inputTransactionDescription"),
        ADD_TRANSACTION_DATE("inputTransactionDate"),
        
        ACTION_REMOVE_FROM_POSITION_INDEX("actionRemoveFromPositionIndex")
    }
    
    fun SimpleHTML.Form.addActionAndMethod(route: JavalinWebFrameworkWrapper.Route) {
        with(SimpleHTML) {
            this@addActionAndMethod.set("action", route.path)
            this@addActionAndMethod.set("method", route.method)
        }
    }
    
    fun ApplicationState.renderResponseTo(context: Context) {
        val rawHtml = with(SimpleHTML) {
            html {
                fun header() {
                    lineBreak()
                    text("-- Transaction Info --")
                    lineBreak()
                }
                
                fun userTransactionInput() {
                    form {
                        addActionAndMethod(AddTransaction)
                        
                        newField("How much money is coming in, or going out? : ", ADD_TRANSACTION_AMOUNT)
                        lineBreak()
                        
                        newField("What it's for? : ", ADD_TRANSACTION_DESCRIPTION)
                        lineBreak()
                        
                        newField(
                            "When did it happen? : ",
                            ADD_TRANSACTION_DATE,
                            input = {
                                it.set(
                                    "value",
                                    with(TransactionsAsText.QuickMoniesDates) {
                                        Date().format()
                                    })
                            })
                        lineBreak()
                        
                        button { text("Stick it in there") }
                    }
                }
                
                fun extraCommands() {
                    lineBreak()
                    text("-- Shortcuts --")
                    lineBreak()
                    form {
                        addActionAndMethod(RemoveLast)
                        button { text("Remove last") }
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
                                            set("value", snapshot.originalPos.toString())
                                            set(
                                                "name",
                                                ACTION_REMOVE_FROM_POSITION_INDEX.id
                                            )
                                            set(
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
                
                header()
                userTransactionInput()
                lineBreak()
                extraCommands()
                lineBreak()
                transactionTable()
                form {
                    addActionAndMethod(RemoveIndex)
                    set("id", ACTION_REMOVE_FROM_POSITION_INDEX.name)
                    hiddenInput(ACTION_REMOVE_FROM_POSITION_INDEX)
                }
            }
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
}