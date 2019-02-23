package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.SimpleStateTransformer
import appcore.functionality.accounting.TransactionAccountant
import appcore.functionality.restoreState
import appcore.transfomers.TransactionsAsText
import appcore.visual_interfaces.web.BasicTableRenderer.renderTo
import io.javalin.Context
import io.javalin.Javalin

fun main(args: Array<String>) {
    JavalinWebFrameworkWrapper().start(
            SimpleStateTransformer()
    )
}

class JavalinWebFrameworkWrapper {


    fun start(simpleStateTransformer: SimpleStateTransformer) {
        val app = Javalin.create().enableDebugLogging().start(7000)

        val runtimeState = ApplicationState().apply { restoreState() }

        app.get("/") { requestContext ->
            with(BasicTableRenderer) {
                runtimeState.renderTo(requestContext)
            }
        }

        app.post("/remove_last") { requestContext: Context ->
            simpleStateTransformer.runTransform(
                    runtimeState, "-"
            )

            requestContext.redirect("/")
        }

        app.post("/") { requestContext: Context ->
            with(BasicTableRenderer) {
                val transactionAmount = requestContext.formParam(
                        TRANSACTION_AMOUNT_ID,
                        "+ 0.00"
                )?.toDouble() ?: 0.00

                simpleStateTransformer.runTransform(
                        runtimeState, "+ $transactionAmount"
                )
            }

            runtimeState.renderTo(requestContext)
        }
    }

}

