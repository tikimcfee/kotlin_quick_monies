package kotlin_quick_monies.visual_interfaces.web

import io.javalin.Context
import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper.Route.RemoveIndex
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.screenComponents.makeAllTransactionTables
import kotlin_quick_monies.visual_interfaces.web.screenComponents.monthlyTransactionInput
import kotlin_quick_monies.visual_interfaces.web.screenComponents.singleTransactionInput

object BasicTableRenderer {
    
    enum class FormParam(val id: String) {
        ADD_TRANSACTION_AMOUNT("inputTransactionAmount"),
        ADD_TRANSACTION_DESCRIPTION("inputTransactionDescription"),
        ADD_TRANSACTION_DATE("inputTransactionDate"),
        
        ADD_SIMPLE_MONTHLY_TRANSACTION_AMOUNT("inputMonthlyTransactionAmount"),
        ADD_SIMPLE_MONTHLY_TRANSACTION_MONTHS_TO_ADD("inputMonthlyTransactionCount"),
        ADD_SIMPLE_MONTHLY_TRANSACTION_START_DATE("inputMonthlyTransactionStartDate"),
        ADD_SIMPLE_MONTHLY_TRANSACTION_DESCRIPTION("inputMonthlyTransactionDescription"),
        
        ACTION_REMOVE_TRANSACTION_BY_ID("actionRemoveTransactionById")
    }
    
    fun AppStateFunctions.renderResponseTo(context: Context) {
        val rawHtml = with(SimpleHTML) {
            html {
                // Page setup (style, meta, etc.)
                applyDeviceDependentSizing()
                setGlobalStyles()
                
                // Page content
                body {
                    // All inputs, wrapped in a floating block
                    fun transactionInputs() {
                        div {
                            singleTransactionInput()
                            monthlyTransactionInput()
                        }
                    }
                    
                    // The scrolling transaction window
                    fun transactionTable() {
                        div {
                            setCssClasses(componentClasses.mainTransactionWindow)
                            makeAllTransactionTables(this)
                        }
                    }
                    
                    // The 'high level' picture of how to draw it
                    lineBreak()
                    transactionInputs()
                    lineBreak()
                    transactionTable()
                    
                    // Hidden form input for 'remove' button functionality; a little AJAX would do it but eh... not yet.
                    form {
                        addActionAndMethod(RemoveIndex)
                        setAttribute("id", ACTION_REMOVE_TRANSACTION_BY_ID.name)
                        hiddenInput(ACTION_REMOVE_TRANSACTION_BY_ID)
                    }
                }
            }
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
    
    
    
    
}
