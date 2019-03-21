package kotlin_quick_monies.visual_interfaces.web

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.restoreState
import kotlin_quick_monies.visual_interfaces.web.HomeScreenRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.HomeScreenRenderer.renderResponseTo
import io.javalin.Context
import io.javalin.Javalin
import kotlin_quick_monies.functionality.coreDefinitions.*
import kotlin_quick_monies.transfomers.TransactionsAsText.QuickMoniesDates.parse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.joda.time.DateTime
import java.lang.Exception
import java.net.InetAddress
import java.util.*

typealias NotANumber = NumberFormatException

fun main() {
    JavalinWebFrameworkWrapper().start()
}

class JavalinWebFrameworkWrapper {
    
    object IPHelper {
        const val preferredPort = "7000"
        const val protocol = "http"
        val localNetworkIp: String
            get() = InetAddress.getLocalHost()?.hostAddress ?: "localhost"
    }
    
    sealed class Route(name: String, val method: String) {
        companion object {
            const val port: String = IPHelper.preferredPort
            val host: String = IPHelper.localNetworkIp
            val protocol: String = IPHelper.protocol
            val root: String = "$protocol://$host:$port"
            
            val startupRouteSet = setOf(
                Home, AddTransaction, RemoveTransactionById, AddRepeatedTransaction
            )
        }
        
        val path = "$root/$name"
        val name = "/$name"
        
        object Home : Route("", "get")
        object AddTransaction : Route("add_transaction", "post")
        object AddRepeatedTransaction : Route("add_repeated_transaction", "post")
        object RemoveTransactionById : Route("remove_transaction_by_id", "post")
    }
    
    fun start() {
        val app = Javalin.create()
            .enableDebugLogging()
            .server {
                Server().apply {
                    connectors = arrayOf(ServerConnector(this).apply {
                        host = Route.host
                        port = Route.port.toInt()
                    })
                }
            }
            .start(7000)
        
        val runtimeState = AppStateFunctions().apply { restoreState() }
        
        Route.startupRouteSet.forEach { route ->
            // little trick to make the compiler enforce full enum set usage
            // *Do not add an else!*
            val noopReturnHandle = when (route) {
                Route.Home -> app.get(route.name) {
                    runtimeState.renderResponseTo(it)
                    
                    YNABIntegration.testFetch()
                    
                }
                Route.AddTransaction -> app.post(route.name) {
                    runtimeState.withContextAddTransaction(it)
                    it.redirect(Route.Home.path)
                }
                Route.RemoveTransactionById -> app.post(route.name) {
                    runtimeState.withContextRemoveFromPosition(it)
                    it.redirect(Route.Home.path)
                }
                Route.AddRepeatedTransaction -> app.post(route.name) {
                    runtimeState.withContextAddRepeatedTransaction(it)
                    it.redirect(Route.Home.path)
                }
            }
        }
    }
    
    private fun AppStateFunctions.withContextAddTransaction(
        requestContext: Context
    ) {
        with(requestContext) {
            val transactionDate = tryFormStringToDate(ADD_TRANSACTION_DATE) ?: return
            val transactionAmount = formDouble(ADD_TRANSACTION_AMOUNT) ?: return
            val transactionDescription = formString(ADD_TRANSACTION_DESCRIPTION)
            
            saveAndRun(
                Command.Add(
                    Transaction(
                        "${UUID.randomUUID()}::${transactionDate.millis}",
                        transactionDate.millis,
                        transactionAmount,
                        transactionDescription,
                        TransactionGroupInfo(
                            id = "individual_transactions_with_no_assigned_group",
                            resultTransactions = mutableListOf("individual_transactions_with_no_assigned_group"),
                            sourceSchedule = SingleDaySchedule,
                            inHiddenExpenses = false
                        )
                    )
                )
            )
        }
    }
    
    private fun AppStateFunctions.withContextAddRepeatedTransaction(
        requestContext: Context
    ) {
        with(requestContext) {
            val instancesToAdd = formInt(ADD_REPEATED_TRANSACTION_INSTANCES_TO_ADD) ?: return
            val startDate = tryFormStringToDate(ADD_REPEATED_TRANSACTION_START_DATE) ?: return
            val monthlyAmount = formDouble(ADD_REPEATED_TRANSACTION_AMOUNT) ?: return
            val transactionDescription = formString(ADD_REPEATED_TRANSACTION_DESCRIPTION)
            
            val transactionDayGroup = try {
                IdealCore.CoreConstants.DayGroup.valueOf(
                    formString(ADD_REPEATED_TRANSACTION_SEPARATOR)
                )
            } catch (e: Exception) {
                println("Invalid date separator; ${e.message}")
                return
            }
            
            val isHidden = isChecked(ADD_REPEATED_TRANSACTION_MAKE_HIDDEN_EXPENSE)
    
            val newId = newTransactionId()
            saveAndRun(
                Command.AddRepeatedTransaction(
                    Transaction(
                        newId,
                        startDate.millis,
                        monthlyAmount,
                        transactionDescription,
                        groupInfo = TransactionGroupInfo(
                            "repeated-transactions::$newId::${startDate.millis}",
                            mutableListOf(),
                            TransactionSchedulingData(
                                "repeated-schedules::$newId::${startDate.millis}",
                                instancesToAdd,
                                transactionDayGroup
                            ),
                            isHidden
                        )
                    )
                )
            )
        }
    }
    
    private fun AppStateFunctions.withContextRemoveFromPosition(
        requestContext: Context
    ) {
        with(requestContext) {
            saveAndRun(
                Command.RemoveTransaction(
                    formString(ACTION_REMOVE_TRANSACTION_BY_ID)
                )
            )
        }
    }
    
    private fun Context.formDouble(param: HomeScreenRenderer.FormParam): Double? {
        return try {
            formParam(param.id)?.toDouble()
        } catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            null
        }
    }
    
    private fun Context.formInt(param: HomeScreenRenderer.FormParam): Int? {
        return try {
            formParam(param.id)?.toInt()
        } catch (badInput: NotANumber) {
            println("Looked for $param, scored a ${formParam(param.id)}")
            null
        }
    }
    
    private fun Context.formString(param: HomeScreenRenderer.FormParam): String =
        formParam(param.id) ?: ""
    
    private fun Context.isChecked(param: HomeScreenRenderer.FormParam): Boolean =
        (formParam(param.id) ?: "") == param.id
    
    private fun Context.tryFormStringToDate(param: HomeScreenRenderer.FormParam): DateTime? =
        with(formString(param)) {
            (parseAsFormattedDate() ?: parseAsEpochLong()).also {
                if (it == null) {
                    println("Tried to parse a string into a Date. No such luck: ${this@with}")
                }
            }
        }
    
    private fun String.parseAsFormattedDate() = try {
        parse()
    } catch (badInput: Exception) {
        null
    }
    
    private fun String.parseAsEpochLong() = try {
        DateTime(toLong())
    } catch (badInput: Exception) {
        null
    }
    
    
}

