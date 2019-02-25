package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.SimpleStateTransformer
import appcore.functionality.Transaction
import appcore.functionality.commands.Command
import appcore.functionality.list.RelativePos
import appcore.functionality.restoreState
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.*
import appcore.visual_interfaces.web.BasicTableRenderer.renderResponseTo
import io.javalin.Context
import io.javalin.Javalin
import java.util.*

typealias NotANumber = NumberFormatException

fun main(args: Array<String>) {
    JavalinWebFrameworkWrapper().start(
        SimpleStateTransformer()
    )
}

class JavalinWebFrameworkWrapper {
    
    sealed class Route(
        name: String,
        val method: String
    ) {
        
        private val port = "7000"
        private val host = "192.168.1.70"
        private val protocol = "http"
        private val root = "$protocol://$host:$port"
        val path = "$root/$name"
        val name = "/$name"
        
        object Home : Route("", "get")
        object AddTransaction : Route("add_transaction", "post")
        object RemoveLast : Route("remove_last", "post")
        object RemoveIndex : Route("remove_index", "post")
        
        companion object {
            val allRoutes = setOf(
                Home, AddTransaction, RemoveLast, RemoveIndex
            )
        }
    }
    
    fun start(simpleStateTransformer: SimpleStateTransformer) {
        val app = Javalin.create().enableDebugLogging().start(7000)
        val runtimeState = ApplicationState().apply { restoreState() }
        
        Route.allRoutes.forEach { route ->
            when (route) {
                Route.Home -> app.get(route.name) {
                    runtimeState.renderResponseTo(it)
                }
                Route.AddTransaction -> app.post(route.name) {
                    runtimeState.withContextAddTransaction(
                        it,
                        simpleStateTransformer
                    )
                    runtimeState.renderResponseTo(it)
                }
                Route.RemoveLast -> app.post(route.name) {
                    runtimeState.withContextRemoveLast(
                        simpleStateTransformer
                    )
                    runtimeState.renderResponseTo(it)
                }
                Route.RemoveIndex -> app.post(route.name) {
                    runtimeState.withContextRemoveFromPosition(
                        it,
                        simpleStateTransformer
                    )
                    runtimeState.renderResponseTo(it)
                }
            }
        }
        
    }
    
    fun ApplicationState.withContextRemoveFromPosition(
        requestContext: Context,
        stateTransformer: SimpleStateTransformer
    ) {
        with(requestContext) {
            val deletePosition = formInt(ACTION_REMOVE_FROM_POSITION)
            if (deletePosition.second) {
                stateTransformer.runTransform(
                    this@withContextRemoveFromPosition,
                    Command.Remove(RelativePos.Explicit(deletePosition.first))
                )
            }
        }
    }
    
    fun ApplicationState.withContextRemoveLast(
        stateTransformer: SimpleStateTransformer
    ) {
        stateTransformer.runTransform(
            this,
            Command.Remove(RelativePos.Last)
        )
    }
    
    fun ApplicationState.withContextAddTransaction(
        requestContext: Context,
        stateTransformer: SimpleStateTransformer
    ) {
        with(requestContext) {
            val transactionDate = Date()
            val transactionAmount = formDouble(ADD_TRANSACTION_AMOUNT)
            val transactionDescription = formString(ADD_TRANSACTION_DESCRIPTION)
            
            stateTransformer.runTransform(
                this@withContextAddTransaction,
                Command.Add(
                    RelativePos.Last,
                    Transaction(
                        transactionDate,
                        transactionAmount,
                        transactionDescription
                    )
                )
            )
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
    
    fun Context.formInt(param: BasicTableRenderer.FormParam): Pair<Int, Boolean> {
        val invalid_number = Int.MIN_VALUE
        val parsed = try {
            formParam(param.id)?.toInt() ?: invalid_number
        }
        catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            invalid_number
        }
        return Pair(parsed, parsed != invalid_number)
    }
    
    fun Context.formString(param: BasicTableRenderer.FormParam): String =
        formParam(param.id) ?: ""
    
}

