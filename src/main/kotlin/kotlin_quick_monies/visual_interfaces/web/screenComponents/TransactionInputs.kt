package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.coreDefinitions.IdealCore.CoreConstants.DayGroup.*
import kotlin_quick_monies.transfomers.TransactionsAsText
import kotlin_quick_monies.visual_interfaces.web.HomeScreenRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.addActionAndMethod
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.button
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.div
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.form
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.inputTag
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.newCheckbox
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.selectionDropdown
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setAttribute
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setCssClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.Tag
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionInput.transactionInputBox
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionInput.transactionInputRepeatedCountInput
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionInput.transactionInputRepeatedDayGroupOption
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionInput.transactionInputRepeatedDayGroupSelection
import org.joda.time.DateTime

fun Tag.allInputsAsVerticalBar(): Tag = div {
    setCssClasses(componentClasses.transactionInput.container.vertical)
    
    singleTransactionInput()
    repeatedTransactionInput()
}

/**
 *  Creates an input component, asking someone to stick stuff
 *  in place to record a single new transaction.
 */
fun Tag.singleTransactionInput(): Tag = div {
    setCssClasses(transactionInputBox)
    
    div { text("<strong>- Transaction Info -</strong>") }
    
    form {
        addActionAndMethod(JavalinWebFrameworkWrapper.Route.AddTransaction)
        
        div {
            inputTag("What it's for? : ", ADD_TRANSACTION_DESCRIPTION)
        }
        
        div {
            inputTag("How much? : ", ADD_TRANSACTION_AMOUNT)
        }
        
        div {
            inputTag(
                "When? (yyyy-mm-dd)? : ", ADD_TRANSACTION_DATE
            ) {
                setValueAsToday()
            }
        }
        
        div {
            button { text("Stick it in there") }
        }
    }
}

fun SimpleHTML.Input.setValueAsToday() {
    setAttribute("value",
        with(TransactionsAsText.QuickMoniesDates) {
            DateTime().format()
        })
}

/**
 *  Creates an input component, asking someone to stick stuff
 *  in place to record a a bevy of transactions, in this case
 *  monthly. So very fancy.
 */
fun Tag.repeatedTransactionInput(): Tag = div {
    setCssClasses(transactionInputBox)
    
    text("<strong>- Simple Repeated Transactions -</strong>")
    
    form {
        addActionAndMethod(JavalinWebFrameworkWrapper.Route.AddRepeatedTransaction)
        
        div {
            inputTag("What it's for?", ADD_REPEATED_TRANSACTION_DESCRIPTION)
        }
        
        div {
            inputTag("Amount for each", ADD_REPEATED_TRANSACTION_AMOUNT)
        }
        
        div {
            inputTag(
                "Start these on... ",
                ADD_REPEATED_TRANSACTION_START_DATE
            ) {
                setValueAsToday()
            }
        }
        
        div {
            text("Every: ")
            selectionDropdown(
                ADD_REPEATED_TRANSACTION_SEPARATOR,
                { setCssClasses(transactionInputRepeatedDayGroupSelection) },
                { setCssClasses(transactionInputRepeatedDayGroupOption) },
                Day.name, Week.name, Month.name, Year.name
            )
            text(", ")
            inputTag(
                "Stop after:",
                ADD_REPEATED_TRANSACTION_INSTANCES_TO_ADD
            ) {
                setCssClasses(transactionInputRepeatedCountInput)
                setAttribute("value", "12")
            }
            
            text(" times.")
        }
        
        div {
            newCheckbox("Mark as hidden, everyday expense", ADD_REPEATED_TRANSACTION_MAKE_HIDDEN_EXPENSE)
        }
        
        div {
            button { text("Stick a bunch of 'em in there.") }
        }
        
    }
}
