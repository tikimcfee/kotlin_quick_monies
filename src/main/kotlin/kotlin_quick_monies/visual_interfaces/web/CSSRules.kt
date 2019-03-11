package kotlin_quick_monies.visual_interfaces.web

import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setStyles
import kotlinx.css.*
import kotlinx.css.Float

object componentClasses {
    val transactionInputBox = "transaction-input-box"
    val mainTransactionWindow = "main-transaction-window"
}

object namedRules {
    val clearfix = ".clearfix::after"
    
    val evenTableRows = "tr:nth-child(even)"
}

fun SimpleHTML.Html.setGlobalStyles() {
    setStyles {
        rule(namedRules.clearfix) {
            content = QuotedString("")
            clear = Clear.both
            display = Display.table
        }
        
        kotlinx.css.table {
            padding = "10"
            width = 100.pct
            
            borderRadius = 4.pt
            
            backgroundColor = Color("#EFEFEF")
        }
        
        rule(namedRules.evenTableRows) {
            backgroundColor = Color.lightGrey
        }
        
        rule(".${componentClasses.transactionInputBox}") {
            padding = "8"
            margin = "8"
            
            width = LinearDimension.fitContent
            height = LinearDimension.fitContent
            
            float = Float.left
            backgroundColor = Color("#EFEFEF44")
        }
        
        rule(".${componentClasses.mainTransactionWindow}") {
            padding = "4 4 4 4"
            margin = "auto"
            width = 75.pct
            height = 75.pct
            
            scrollBehavior = ScrollBehavior.auto
            overflow = Overflow.auto
            
            backgroundColor = Color("#EFEFEF44")
        }
    }
}
