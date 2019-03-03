package kotlin_quick_monies.functionality.dataPopulation

import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import java.util.*

class ProjectedTransactionGenerator {
    
    fun createMultiple(
        transactionTemplate: Transaction,
        daysBetween: Int,
        count: Int
    ): List<Transaction> {
        return IntRange(1, count).map { i ->
            val newDate = transactionTemplate
                .date
                .let { Date(it).toInstant() }
                .plusSeconds(i * daysBetween.toLong() * 24 * 60 * 60)
                .let { Date(it.toEpochMilli()).time }
            with(transactionTemplate) {
                Transaction(newDate, amount, describeAsTemplate(i, count))
            }
        }
    }
    
    private fun Transaction.describeAsTemplate(
        instanceNumber: Int,
        total: Int
    ) = "($instanceNumber/$total from '$description')"
    
}