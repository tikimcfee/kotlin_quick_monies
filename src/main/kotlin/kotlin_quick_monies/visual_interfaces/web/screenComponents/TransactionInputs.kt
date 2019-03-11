package kotlin_quick_monies.visual_interfaces.web.screenComponents

import kotlin_quick_monies.functionality.coreDefinitions.IdealCore
import kotlin_quick_monies.functionality.coreDefinitions.IdealCore.CoreConstants.DayGroup.*
import kotlin_quick_monies.transfomers.TransactionsAsText
import kotlin_quick_monies.visual_interfaces.web.BasicTableRenderer.FormParam.*
import kotlin_quick_monies.visual_interfaces.web.JavalinWebFrameworkWrapper
import kotlin_quick_monies.visual_interfaces.web.componentClasses
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionInputRepeatedDayGroupOption
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionInputRepeatedDayGroupSelection
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.addActionAndMethod
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.button
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.div
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.form
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.lineBreak
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.newCheckbox
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.newField
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.selectionDropdown
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setAttribute
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setCssClasses
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.text
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.Tag
import org.joda.time.DateTime

/**
 *  Creates an input component, asking someone to stick stuff
 *  in place to record a single new transaction.
 */
fun Tag.singleTransactionInput(): Tag = div {
    lineBreak()
    text("<strong>- Transaction Info -</strong>")
    lineBreak()
    
    form {
        addActionAndMethod(JavalinWebFrameworkWrapper.Route.AddTransaction)
        
        newField("What it's for? : ", ADD_TRANSACTION_DESCRIPTION)
        lineBreak()
        
        newField("How much? : ", ADD_TRANSACTION_AMOUNT)
        lineBreak()
        
        newField(
            "For what date? : ",
            ADD_TRANSACTION_DATE,
            input = { dateInputField ->
                dateInputField.setAttribute("value",
                    with(TransactionsAsText.QuickMoniesDates) {
                        DateTime().format()
                    })
            }
        )
        lineBreak()
        
        button { text("Stick it in there") }
    }
}.apply {
    setCssClasses(componentClasses.transactionInputBox)
}

/**
 *  Creates an input component, asking someone to stick stuff
 *  in place to record a a bevy of transactions, in this case
 *  monthly. So very fancy.
 */
fun Tag.repeatedTransactionInput(): Tag = div {
    lineBreak()
    text("<strong>- Simple Repeated Transactions -</strong>")
    lineBreak()
    
    form {
        addActionAndMethod(JavalinWebFrameworkWrapper.Route.AddRepeatedTransaction)
        newField("What it's for? : ", ADD_REPEATED_TRANSACTION_DESCRIPTION)
        lineBreak()
        
        
        newField("Monthly amount : ", ADD_REPEATED_TRANSACTION_AMOUNT)
        lineBreak()
        
        newField(
            "Start date : ",
            ADD_REPEATED_TRANSACTION_START_DATE,
            input = { dateInputField ->
                dateInputField.setAttribute("value",
                    with(TransactionsAsText.QuickMoniesDates) {
                        DateTime().format()
                    })
            })
        lineBreak()
    
        text("Separation between ")
        selectionDropdown(
            ADD_REPEATED_TRANSACTION_SEPARATOR,
            { setCssClasses(transactionInputRepeatedDayGroupSelection) },
            { setCssClasses(transactionInputRepeatedDayGroupOption) },
            Atom.name, Week.name, Month.name, Year.name
        )
        lineBreak()
        
        newField(
            "Stop after : ",
            ADD_REPEATED_TRANSACTION_INSTANCES_TO_ADD,
            input = { dateInputField ->
                dateInputField.setAttribute("value", "12")
            })
        lineBreak()
        
        newCheckbox("Mark as hidden, everyday expense", ADD_REPEATED_TRANSACTION_MAKE_HIDDEN_EXPENSE)
        lineBreak()
        
        button { text("Stick a bunch of 'em in there.") }
    }
}.apply {
    setCssClasses(componentClasses.transactionInputBox)
}
