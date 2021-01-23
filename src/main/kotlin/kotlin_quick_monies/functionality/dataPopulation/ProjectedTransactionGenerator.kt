package kotlin_quick_monies.functionality.dataPopulation

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.coreDefinitions.DayGroup
import kotlin_quick_monies.functionality.coreDefinitions.DayGroup.*
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.coreDefinitions.newTransactionId
import kotlin_quick_monies.functionality.executeWithStateFunctions
import org.joda.time.DateTime

class ProjectedTransactionGenerator {
    
    private fun DayGroup.makeGenerator(
        startDate: Long,
        produceNextDateFrom: (DayGroup, Long) -> Long,
        shouldKeepRunning: () -> Boolean = { true }
    ) = sequence {
        var nextDateToYield = startDate
        while (shouldKeepRunning()) {
            yield(nextDateToYield)
            nextDateToYield = produceNextDateFrom(this@makeGenerator, nextDateToYield)
        }
    }
    
    fun dateGenerator(group: DayGroup, date: Long): Long {
        return with(DateTime(date)) {
            when (group) {
                Day -> plusDays(1)
                Week -> plusWeeks(1)
                Month -> plusMonths(1)
                Year -> plusYears(1)
            }.millis
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
                        ::dateGenerator
                    )
                    .take(repetitionAmount)
                    .mapIndexed { index, nextDate ->
                        val newId = newTransactionId()
                        transactionTemplate.copy(
                            id = newId,
                            // todo: numbering tries to show when something ends; maybe mark 'last' instead?
                            description = when (transactionTemplate.groupInfo.inHiddenExpenses) {
                                true -> transactionTemplate.description
                                false -> transactionTemplate.description +
                                    (if (index + 1 == repetitionAmount) {
                                        " (last one)"
                                    } else {
                                        ""
                                    })
                            },
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
