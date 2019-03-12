package kotlin_quick_monies.visual_interfaces.web.htmlComponents

import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.hiddenExpenses.hiddenExpenseColumn
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.hiddenExpenses.hiddenExpenseColumnSpans
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.hiddenExpenses.hiddenExpenseRootContainer
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.mainTransaction.mainTransactionWindow
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.mainTransaction.transactionRowsGridItem
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.mainTransaction.transactionRowsGridParent
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionInputBox
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionRowDeleteButton
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.SimpleHTML.setStyles
import kotlin_quick_monies.visual_interfaces.web.htmlComponents.componentClasses.transactionInputRepeatedCountInput
import kotlinx.css.*
import kotlinx.css.Float

object componentClasses {
    val transactionInputBox = "transaction-input-box"
    val transactionInputRepeatedCountInput = "transaction-input-repeated-count-input"
    val transactionInputRepeatedDayGroupOption = "transaction-input-repeated-daygroup-option"
    val transactionInputRepeatedDayGroupSelection = "transaction-input-repeated-daygroup-selection"
    
    val transactionRowDeleteButton = "transaction-row-deleteButton"
    
    object mainTransaction {
        val mainTransactionWindow = "main-transaction-window"
        val transactionRowsGridParent = "transaction-row-info-grid"
        val transactionRowsGridItem = "transaction-row-info-grid-item"
    }
    
    object hiddenExpenses {
        val hiddenExpenseRootContainer = "hidden-expense-root-container"
        val hiddenExpenseDataRow = "hidden-expense-row"
        val hiddenExpenseColumn = "hidden-expense-column"
        val hiddenExpenseColumnSpans = "hidden-expense-column span"
    }
}

object namedRules {
    val clearfix = ".clearfix::after"
}

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
        
        // -----------------------------
        // Transaction Inputs
        // -----------------------------
        
        addClass(transactionInputBox) {
            display = Display.inlineGrid
            
            padding = "8"
            margin = "8"
            
            width = LinearDimension.fitContent
            height = LinearDimension.fitContent
            
            float = Float.left
            backgroundColor = Color("#EFEFEF44")
        }
    
        addClass("$transactionInputBox div") {
            backgroundColor = Color("#EFEFEF44")
            margin = "4pt"
        }
    
        addClass("$transactionInputBox form") {
            display = Display.inlineGrid
        }
        
        // -----------------------------
        // Hidden Expenses
        // -----------------------------
        addClass(hiddenExpenseRootContainer) {
            marginTop = 8.pt
            marginBottom = 8.pt
            padding = "4pt"
            backgroundColor = Color("#0000000a")
            float = Float.left
        }
        
        addClass(hiddenExpenseColumn) {
            float = Float.left
        }
        
        addClass(hiddenExpenseColumnSpans) {
            margin = "auto"
            display = Display.inherit
            textAlign = TextAlign.right
            padding = "4pt"
        }
        
        // -----------------------------
        // Main Transaction Scrollbox
        // -----------------------------
        
        addClass(mainTransactionWindow) {
            padding = "4 4 4 4"
            margin = "auto"
            width = 95.pct
            height = 75.pct
            
            scrollBehavior = ScrollBehavior.auto
            overflow = Overflow.auto
            
            backgroundColor = Color("#EFEFEF44")
        }
        
        addClass(transactionRowsGridParent) {
            display = Display.grid
            gridTemplateColumns = GridTemplateColumns(
                25.pct, 25.pct, 25.pct, 25.pct
            )
            
            borderRadius = 2.pt
            borderColor = Color.lightGrey
            borderWidth = 1.pt
            
            marginTop = 8.pt
            marginBottom = 8.pt
            padding = "2pt"
            backgroundColor = Color("#0000000a")
            
        }
        
        addClass(transactionRowsGridItem) {
            marginLeft = 2.pt
            marginRight = 2.pt
            textAlign = TextAlign.center
        }
        
        addClass(transactionInputRepeatedCountInput) {
            width = 10.pct
        }
        
        rule("span.transaction-row-info-grid-item:nth-child(-n+4)") {
            backgroundColor = Color.deepSkyBlue
            fontWeight = FontWeight.bold
            color = Color.white
            marginBottom = 4.pt
        }
        
        rule("span.transaction-row-info-grid-item:nth-child(n+5)") {
            backgroundColor = Color("#00beff1a")
            borderBottom = "1px solid #0000001a"
            marginBottom = 2.pt
        }
        
        addClass(transactionRowDeleteButton) {
            width = 20.pct
            margin = "auto"
        }
        
    }
    
    
}
