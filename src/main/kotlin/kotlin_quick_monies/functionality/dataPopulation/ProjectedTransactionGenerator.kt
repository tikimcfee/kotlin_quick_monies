package kotlin_quick_monies.functionality.dataPopulation

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.coreDefinitions.IdealCore
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.coreDefinitions.newTransactionId
import kotlin_quick_monies.functionality.executeWithStateFunctions
import org.joda.time.DateTime

class ProjectedTransactionGenerator {
    
    private fun IdealCore.CoreConstants.DayGroup.makeGenerator(
        startDate: Long,
        produceNextDateFrom: (IdealCore.CoreConstants.DayGroup, Long) -> Long,
        shouldKeepRunning: () -> Boolean = { true }
    ) = sequence {
        var nextDateToYield = startDate
        while (shouldKeepRunning()) {
            yield(nextDateToYield)
            nextDateToYield = produceNextDateFrom(this@makeGenerator, nextDateToYield)
        }
    }
    
    fun insertTransactionsFromTemplate(
        transactionTemplate: Transaction,
        appFunctions: AppStateFunctions
    ) {
        fun generateTransactions(): List<Transaction> {
            return with(transactionTemplate.groupInfo.sourceSchedule) {
                repetitionSeparator
                    .makeGenerator(
                        transactionTemplate.date,
                        { _, date -> DateTime(date).plusMonths(1).millis }
                    )
                    .take(repetitionAmount)
                    .mapIndexed { index, nextDate ->
                        val newId = newTransactionId()
                        transactionTemplate.copy(
                            id = newId,
                            description = "${transactionTemplate.description} (${index + 1} of $repetitionAmount)",
                            date = nextDate,
                            groupInfo = transactionTemplate.groupInfo.apply {
                                resultTransactions.add(newId)
                            }
                        )
                    }
                    .toList()
            }
        }
        
        fun insertTransactions(transactions: List<Transaction>) {
            transactions.forEach { Command.Add(it).executeWithStateFunctions(appFunctions) }
        }
        
        with(generateTransactions()) {
            insertTransactions(this)
        }
    }
    
}
