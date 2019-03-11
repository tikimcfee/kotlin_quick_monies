package kotlin_quick_monies.visual_interfaces.web

import kotlin_quick_monies.visual_interfaces.web.componentClasses.mainTransactionWindow
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionInputBox
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowDeleteButton
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowInfoContainerAllSpans
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowInfoContainerHeaderAllSpans
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowInfoContainerHeaderFirstSpan
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowInfoContainerHeaders
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionRowSeparator
import kotlin_quick_monies.visual_interfaces.web.componentClasses.transactionTableSectionContainer
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setStyles
import kotlinx.css.*
import kotlinx.css.Float
import kotlinx.css.pt
import kotlinx.css.pct

object componentClasses {
    val transactionInputBox = "transaction-input-box"
    val mainTransactionWindow = "main-transaction-window"
    
    val transactionInputRepeatedDayGroupOption = "transaction-input-repeated-daygroup-option"
    val transactionInputRepeatedDayGroupSelection = "transaction-input-repeated-daygroup-selection"
    
    val transactionTableSectionContainer = "transaction-table-section-container"
    
    val transactionRowInfoContainer = "transaction-row-info-container"
    val transactionRowDeleteButton = "transaction-row-deleteButton"
    val transactionRowSeparator = "transaction-row-separator"
    
    val transactionRowInfoContainerAllSpans = "transaction-row-info-container span"
    val transactionRowDate = "transaction-row-date"
    val transactionRowDescription = "transaction-row-description"
    val transactionRowAmount = "transaction-row-amount"
    val transactionRowAfter = "transaction-row-after"
    
    val transactionRowInfoContainerHeaderAllSpans = "transaction-row-info-container-headers span"
    val transactionRowInfoContainerHeaderFirstSpan = "$transactionRowInfoContainerHeaderAllSpans:first-child"
    val transactionRowInfoContainerHeaders = "transaction-row-info-container-headers"
    val transactionRowDateHeader = "transaction-row-date-header"
    val transactionRowDescriptionHeader = "transaction-row-description-header"
    val transactionRowAmountHeader = "transaction-row-amount-header"
    val transactionRowAfterHeader = "transaction-row-after-header"
}

object namedRules {
    val clearfix = ".clearfix::after"
}

fun String.target() = ".$this"

fun CSSBuilder.addClass(
    name: String,
    block: RuleSet
) = rule(".$name", block)

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
        
        addClass(transactionInputBox) {
            padding = "8"
            margin = "8"
            
            width = LinearDimension.fitContent
            height = LinearDimension.fitContent
            
            float = Float.left
            backgroundColor = Color("#EFEFEF44")
        }
        
        addClass(mainTransactionWindow) {
            padding = "4 4 4 4"
            margin = "auto"
            width = 95.pct
            height = 75.pct
            
            scrollBehavior = ScrollBehavior.auto
            overflow = Overflow.auto
            
            backgroundColor = Color("#EFEFEF44")
        }
    
        addClass(transactionTableSectionContainer) {
            marginTop = 8.pt
            marginBottom = 8.pt
            padding = "4pt"
            backgroundColor = Color("#0000000a")
        }
        
        addClass(transactionRowInfoContainerHeaders) {
            marginBottom = 4.pt
        }
        
        addClass(transactionRowInfoContainerHeaderAllSpans) {
            width = 20.pct
            margin = "auto"
            textAlign = TextAlign.center
            display = Display.inlineBlock
            fontStyle = FontStyle.italic
        }
        
        addClass(transactionRowInfoContainerHeaderFirstSpan) {
            color = Color.dimGray
            fontWeight = FontWeight.bold
            fontStyle = FontStyle.normal
            textAlign = TextAlign.left
        }
        
        addClass(transactionRowInfoContainerAllSpans) {
            width = 20.pct
            margin = "auto"
            textAlign = TextAlign.center
            display = Display.inlineBlock
        }
        
        addClass(transactionRowDeleteButton) {
            width = 20.pct
            margin = "auto"
        }
        
        addClass(transactionRowSeparator) {
            width = 100.pct
            background = "#a5a5a542"
            height = 1.px
            marginTop = 2.pt
            marginBottom = 2.pt
        }
    }
    
    
}
