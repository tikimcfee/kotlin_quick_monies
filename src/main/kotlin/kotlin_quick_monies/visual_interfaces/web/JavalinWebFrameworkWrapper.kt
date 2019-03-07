package kotlin_quick_monies.visual_interfaces.web

import appcore.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.list.RelativePos
import appcore.functionality.restoreState
import kotlin_quick_monies.transfomers.TransactionsAsText
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.renderResponseTo
import io.javalin.Context
import io.javalin.Javalin
import org.joda.time.DateTime
import java.text.ParseException

typealias NotANumber = NumberFormatException

fun main() {
    JavalinWebFrameworkWrapper().start()
}

class JavalinWebFrameworkWrapper {
    
    sealed class Route(
        name: String,
        val method: String
    ) {
        
        private val port = "7000"
        private val host = "localhost"
        private val protocol = "http"
        private val root = "$protocol://$host:$port"
        val path = "$root/$name"
        val name = "/$name"
        
        object Home : Route("", "get")
        object AddTransaction : Route("add_transaction", "post")
        object RemoveIndex : Route("remove_index", "post")
        object AddMonthlyTransaction : Route("add_monthly_transaction", "post")
        
        companion object {
            val startupRouteSet = setOf(
                Home, AddTransaction, RemoveIndex, AddMonthlyTransaction
            )
        }
    }
    
    fun start() {
        val app = Javalin.create().enableDebugLogging().start(7000)
        val runtimeState = AppStateFunctions().apply { restoreState() }
        
        Route.startupRouteSet.forEach { route ->
            // little trick to make the compiler enforce full enum set usage
            // *Do not add an else!*
            val noopReturnHandle = when (route) {
                Route.Home -> app.get(route.name) {
                    runtimeState.renderResponseTo(it)
                }
                Route.AddTransaction -> app.post(route.name) {
                    runtimeState.withContextAddTransaction(it)
                    it.redirect(Route.Home.path)
                }
                Route.RemoveIndex -> app.post(route.name) {
                    runtimeState.withContextRemoveFromPosition(it)
                    it.redirect(Route.Home.path)
                }
                Route.AddMonthlyTransaction -> app.post(route.name) {
                    runtimeState.withContextAddMonthlyTransaction(it)
                    it.redirect(Route.Home.path)
                }
            }
        }
    }
    
    fun AppStateFunctions.withContextAddMonthlyTransaction(
        requestContext: Context
    ) {
        with(requestContext) {
            val monthsToAdd = formInt(ADD_SIMPLE_MONTHLY_TRANSACTION_MONTHS_TO_ADD) ?: return
            
            val startDate = tryFormStringToDate(ADD_SIMPLE_MONTHLY_TRANSACTION_START_DATE) ?: return
            val monthlyAmount = formDouble(ADD_SIMPLE_MONTHLY_TRANSACTION_AMOUNT) ?: return
            val transactionDescription = formString(ADD_SIMPLE_MONTHLY_TRANSACTION_DESCRIPTION)
            
            `apply command to current state`(
                Command.AddMonthlyTransaction(
                    monthsToAdd,
                    Transaction(
                        startDate.millis,
                        monthlyAmount,
                        transactionDescription
                    )
                )
            )
        }
    }
    
    fun AppStateFunctions.withContextRemoveFromPosition(
        requestContext: Context
    ) {
        with(requestContext) {
            formInt(ACTION_REMOVE_FROM_POSITION_INDEX)?.let {
                `apply command to current state`(
                    Command.Remove(RelativePos.Explicit(it))
                )
            }
        }
    }
    
    fun AppStateFunctions.withContextAddTransaction(
        requestContext: Context
    ) {
        with(requestContext) {
            val transactionDate = tryFormStringToDate(ADD_TRANSACTION_DATE) ?: return
            val transactionAmount = formDouble(ADD_TRANSACTION_AMOUNT) ?: return
            val transactionDescription = formString(ADD_TRANSACTION_DESCRIPTION)
            
            `apply command to current state`(
                Command.Add(
                    RelativePos.Last(),
                    Transaction(
                        transactionDate.millis,
                        transactionAmount,
                        transactionDescription
                    )
                )
            )
        }
    }
    
    fun Context.formDouble(param: BasicTableRenderer.FormParam): Double? {
        return try {
            formParam(param.id)?.toDouble()
        } catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            null
        }
    }
    
    fun Context.formInt(param: BasicTableRenderer.FormParam): Int? {
        return try {
            formParam(param.id)?.toInt()
        } catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            null
        }
    }
    
    fun Context.formString(param: BasicTableRenderer.FormParam): String =
        formParam(param.id) ?: ""
    
    fun Context.tryFormStringToDate(param: BasicTableRenderer.FormParam): DateTime? =
        with(TransactionsAsText.QuickMoniesDates) {
            val dateString = formString(param)
            try {
                dateString.parse()
            } catch (e: ParseException) {
                // Don't allow bad dates in, for now.
                println("Tried to parse a string into a Date. No such luck: $dateString\n$e")
                null
            }
        }
}

