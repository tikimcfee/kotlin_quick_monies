package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.accounting.TransactionAccountant
import appcore.transfomers.TransactionsAsText.IndividualFormatting.formattedAfter
import appcore.transfomers.TransactionsAsText.IndividualFormatting.formattedAmount
import appcore.transfomers.TransactionsAsText.IndividualFormatting.formattedDate
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.TRANSACTION_AMOUNT
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.TRANSACTION_DESCRIPTION
import io.javalin.Context

object BasicTableRenderer {
    
    enum class FormParam(val id: String) {
        TRANSACTION_AMOUNT("inputTransactionAmount"),
        TRANSACTION_DESCRIPTION("inputTransactionDescription"),
    }
    
    fun ApplicationState.renderTo(
        context: Context,
        actionRootUrl: String = "http://localhost:7000/",
        actionMethod: String = "post"
    ) {
        val rawHtml = with(SimpleHTML()) {
            html {
                fun header() {
                    lineBreak()
                    text("-- Transaction Info --")
                    lineBreak()
                }
                
                fun userTransactionInput() {
                    form {
                        set("action", actionRootUrl)
                        set("method", actionMethod)
                        
                        newField("Monies, in or out", TRANSACTION_AMOUNT)
                        lineBreak()
                        
                        newField("What it's for: ", TRANSACTION_DESCRIPTION)
                        lineBreak()
                        
                        button {
                            text("Stick it in there")
                        }
                    }
                }
                
                fun extraCommands() {
                    form {
                        set("action", "${actionRootUrl}remove_last")
                        set("method", "post")
                        
                        button {
                            text("Remove last")
                        }
                    }
                }
                
                fun transactionTable() {
                    table {
                        tr {
                            td { text("Date") }
                            td { text("Transaction Amount") }
                            td { text("After Transaction") }
                        }
                        
                        fun makeRow(snapshot: TransactionAccountant.Snapshot) {
                            tr {
                                with(snapshot.transaction) {
                                    td(align = "right") { text(formattedDate()) }
                                    td(align = "right") { text(formattedAmount()) }
                                }
                                td(align = "right") { text(snapshot.formattedAfter()) }
                            }
                        }
                        
                        transactionAccountant
                            .computeTransactionDeltas(transactionList)
                            .asReversed()
                            .forEach { makeRow(it) }
                    }
                }
                
                header()
                userTransactionInput()
                lineBreak()
                extraCommands()
                lineBreak()
                transactionTable()
            }
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
}