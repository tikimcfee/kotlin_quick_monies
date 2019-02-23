package appcore.functionality.dataPopulation

import appcore.functionality.Transaction
import java.util.*

class ProjectedTransactionGenerator {

    fun createMultiple(transactionTemplate: Transaction,
                       daysBetween: Int,
                       count: Int): List<Transaction> {
        return IntRange(1, count).map { i ->
            val newDate = transactionTemplate
                    .date
                    .toInstant()
                    .plusSeconds(i * daysBetween.toLong() * 24 * 60 * 60)
                    .let { Date(it.toEpochMilli()) }
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