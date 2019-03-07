package kotlin_quick_monies.functionality.dataPopulation

import appcore.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.list.RelativePos
import org.joda.time.DateTime

class ProjectedTransactionGenerator {
    
    fun `generate and insert a number of monthly transcations from a template`(
        requestedGenerationAmount: Int = 12,
        transactionTemplate: Transaction,
        appFunctions: AppStateFunctions
    ) {
        fun generateTransactions(): List<Transaction> {
            var dateAccumulator = DateTime(transactionTemplate.date)
            return IntRange(1, requestedGenerationAmount).map {
                transactionTemplate
                    .copy(date = dateAccumulator.millis)
                    .also { dateAccumulator = dateAccumulator.plusMonths(1) }
            }
        }
        
        fun insertTransactions(transactions: List<Transaction>) {
            transactions.forEach {
                appFunctions.`apply command to current state`(
                    Command.Add(RelativePos.Last(), it)
                )
            }
        }
        
        with(generateTransactions()) {
            insertTransactions(this)
        }
    }
    
}
