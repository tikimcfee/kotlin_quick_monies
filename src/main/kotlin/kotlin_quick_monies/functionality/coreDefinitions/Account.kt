package kotlin_quick_monies.functionality.coreDefinitions

import kotlin_quick_monies.functionality.list.TransactionList

private const val SavingsAccountNumber = "SavingsAccountNumber"
private const val SavingsRoutingNumber = "SavingsRoutingNumber"
private const val CheckingAccountNumber = "CheckingAccountNumber"
private const val CheckingRoutingNumber = "CheckingRoutingNumber"

sealed class Account constructor(
    val identifier: QuickMoneyIdentifier,
    val transactions: TransactionList,
    val metadata: Map<String, String>
) {
    
    class SavingsAccount(
        identifier: String,
        transactions: TransactionList,
        accountNumberAsText: String,
        routingNumberAsText: String
    ) : Account(
        identifier = QuickMoneyIdentifier(identifier),
        transactions = transactions,
        metadata = mapOf(
            SavingsAccountNumber to accountNumberAsText,
            SavingsRoutingNumber to routingNumberAsText
        )
    ) {
        
        fun accountNumber() = metadata[SavingsAccountNumber]
        fun routingNumber() = metadata[SavingsRoutingNumber]
        
    }
    
    class CheckingAccount(
        identifier: String,
        transactions: TransactionList,
        accountNumberAsText: String,
        routingNumberAsText: String
    ) : Account(
        identifier = QuickMoneyIdentifier(identifier),
        transactions = transactions,
        metadata = mapOf(
            CheckingAccountNumber to accountNumberAsText,
            CheckingRoutingNumber to routingNumberAsText
        )
    ) {
        
        fun accountNumber() = metadata[CheckingAccountNumber]
        fun routingNumber() = metadata[CheckingRoutingNumber]
        
    }
}





