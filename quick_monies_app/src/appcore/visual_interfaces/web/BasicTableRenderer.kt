package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.accounting.TransactionAccountant
import appcore.transfomers.TransactionsAsText
import io.javalin.Context

object BasicTableRenderer {

    const val TRANSACTION_AMOUNT_ID = "inputTransactionAmount"

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

                    lineBreak()

                    newField(
                            labelText = "The amount was: ",
                            forAttr = TRANSACTION_AMOUNT_ID,
                            label = { },
                            input = { }
                    )

                    button {
                        text("Stick it in there")
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
                            with(TransactionsAsText.IndividualFormatting) {
                                td(align = "right") { text(snapshot.transaction.formattedDate()) }
                                td(align = "right") { text(snapshot.transaction.formattedAmount()) }
                                td(align = "right") { text(snapshot.formattedAfter()) }
                            }
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