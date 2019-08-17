package kotlin_quick_monies.visual_interfaces.web

import io.javalin.Context
import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.visual_interfaces.web.HomeScreenRenderer.FormParam.ACTION_REMOVE_TRANSACTION_BY_ID
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper.Route.RemoveTransactionById
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.setGlobalStyles
import kotlin_quick_monies.visual_interfaces.web.screenComponents.*

object HomeScreenRenderer {
    
    enum class FormParam(val id: String) {
        ADD_TRANSACTION_AMOUNT("inputTransactionAmount"),
        ADD_TRANSACTION_DESCRIPTION("inputTransactionDescription"),
        ADD_TRANSACTION_DATE("inputTransactionDate"),
        
        ADD_REPEATED_TRANSACTION_AMOUNT("inputRepeatedTransactionAmount"),
        ADD_REPEATED_TRANSACTION_INSTANCES_TO_ADD("inputRepeatedTransactionCount"),
        ADD_REPEATED_TRANSACTION_START_DATE("inputRepeatedTransactionStartDate"),
        ADD_REPEATED_TRANSACTION_SEPARATOR("inputRepeatedTransactionSeparator"),
        ADD_REPEATED_TRANSACTION_DESCRIPTION("inputRepeatedTransactionDescription"),
        ADD_REPEATED_TRANSACTION_MAKE_HIDDEN_EXPENSE("inputRepeatedTransactionMakeDailyExpense"),
        
        ACTION_REMOVE_TRANSACTION_BY_ID("actionRemoveTransactionById")
    }
    
    fun AppStateFunctions.renderResponseTo(context: Context) {
        val rawHtml = with(SimpleHTML) {
            html {
                // Page setup (style, meta, etc.)
                setMetaData()
                setGlobalStyles()
                
                // Page content
                body {
                    div {
                        setCssClasses(componentClasses.mainPage.mainBodyWrapper)
    
                        // All inputs
                        fun transactionInputs() {
                            allInputsAsVerticalBar()
                        }
    
                        // The scrolling transaction window
                        fun transactionTable() {
                            div {
                                setCssClasses(componentClasses.mainTransaction.mainTransactionWindow)
                                makeAllTransactionTables(this)
                            }
                        }
                        
                        // The 'high level' picture of how to draw it
                        transactionTable()
                        transactionInputs()
    
                        // For now, slide hidden stuff to the bottom
                        renderHiddenTransactions(this)
    
                        // Hidden form input for 'remove' button functionality; a little AJAX would do it but eh... not yet.
                        form {
                            addActionAndMethod(RemoveTransactionById)
                            setAttribute("id", ACTION_REMOVE_TRANSACTION_BY_ID.name)
                            hiddenInput(ACTION_REMOVE_TRANSACTION_BY_ID)
                        }
                    }
                }
            }
        }.toString()
        
        context.result(rawHtml).header("Content-Type", "text/html")
    }
    
    
}
