package appcore.visual_interfaces.web

import appcore.functionality.ApplicationState
import appcore.functionality.SimpleStateTransformer
import appcore.functionality.Transaction
import appcore.functionality.commands.Command
import appcore.functionality.list.RelativePos
import appcore.functionality.restoreState
import appcore.transfomers.TransactionsAsText
import appcore.visual_interfaces.web.BasicTableRenderer.FormParam.*
import appcore.visual_interfaces.web.BasicTableRenderer.renderResponseTo
import io.javalin.Context
import io.javalin.Javalin
import java.text.ParseException

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
                    it.redirect(Route.Home.path)
                }
                Route.RemoveLast -> app.post(route.name) {
                    runtimeState.withContextRemoveLast(
                        simpleStateTransformer
                    )
                    it.redirect(Route.Home.path)
                }
                Route.RemoveIndex -> app.post(route.name) {
                    runtimeState.withContextRemoveFromPosition(
                        it,
                        simpleStateTransformer
                    )
                    it.redirect(Route.Home.path)
                }
            }
        }
        
    }
    
    fun ApplicationState.withContextRemoveFromPosition(
        requestContext: Context,
        stateTransformer: SimpleStateTransformer
    ) {
        with(requestContext) {
            val deletePosition = formInt(ACTION_REMOVE_FROM_POSITION_INDEX)
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
            val transactionDate = with(TransactionsAsText.QuickMoniesDates) {
                try {
                    formString(ADD_TRANSACTION_DATE).parse()
                } catch (e: ParseException) {
                    // Don't allow bad dates in, for now.
                    return
                }
            }
            val transactionAmount = formDouble(ADD_TRANSACTION_AMOUNT).let {
                if (!it.second) {
                    return
                }
                it.first
            }
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
    
    fun Context.formDouble(param: BasicTableRenderer.FormParam): Pair<Double, Boolean> {
        val invalidNumber = Double.MIN_VALUE
        val parsed = try {
            formParam(param.id)?.toDouble() ?: invalidNumber
        } catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            invalidNumber
        }
        return Pair(parsed, parsed != invalidNumber)
    }
    
    fun Context.formInt(param: BasicTableRenderer.FormParam): Pair<Int, Boolean> {
        val invalidNumber = Int.MIN_VALUE
        val parsed = try {
            formParam(param.id)?.toInt() ?: invalidNumber
        } catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            invalidNumber
        }
        return Pair(parsed, parsed != invalidNumber)
    }
    
    fun Context.formString(param: BasicTableRenderer.FormParam): String =
        formParam(param.id) ?: ""
    
}

