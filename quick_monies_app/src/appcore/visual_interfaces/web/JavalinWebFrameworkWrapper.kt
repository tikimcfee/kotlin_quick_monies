package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.SimpleStateTransformer
import appcore.functionality.restoreState
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.TRANSACTION_AMOUNT
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.TRANSACTION_DESCRIPTION
import appcore.visual_interfaces.web.BasicTableRenderer.renderTo
import io.javalin.Context
import io.javalin.Javalin

typealias NotANumber = NumberFormatException

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
            with(requestContext) {
                val transactionAmount = formDouble(TRANSACTION_AMOUNT)
                val transactionDescription = formString(TRANSACTION_DESCRIPTION)
                
                simpleStateTransformer.runTransform(
                    runtimeState, "+ $transactionAmount"
                )
            }
            
            runtimeState.renderTo(requestContext)
        }
    }
    
    fun Context.formDouble(param: BasicTableRenderer.FormParam): Double =
        try {
            formParam(param.id)?.toDouble() ?: -9999.9999
        }
        catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            -9999.9999
        }
    
    fun Context.formString(param: BasicTableRenderer.FormParam): String =
        formParam(param.id) ?: ""
    
}

