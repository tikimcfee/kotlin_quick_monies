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
        actionEndpoint: String = "http://localhost:7000/",
        actionMethod: String = "post"
    ) {
        val rawHtml = with(SimpleHTML()) {
            html {
                form {
                    set("action", actionEndpoint)
                    set("method", actionMethod)
                    
                    fun header() {
                        lineBreak()
                        lineBreak()
                        text("-- Transaction Info --")
                        lineBreak()
                        lineBreak()
                    }
                    
                    fun inputFields() {
                        newField(
                            labelText = "How much in or out of your monies?",
                            forAttr = TRANSACTION_AMOUNT
                        )
                        lineBreak()
                        newField(
                            labelText = "Type a description here to re",
                            forAttr = TRANSACTION_DESCRIPTION
                        )
                    }
                    
                    
                    
                    header()
                    inputFields()
                    
                    button {
                        text("Stick it in there")
                    }
                }
                
                form {
                    set("action", "${actionEndpoint}remove_last")
                    set("method", "post")
                    
                    button {
                        text("Remove last")
                    }
                }
                
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
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
}