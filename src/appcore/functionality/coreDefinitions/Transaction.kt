package appcore.functionality.coreDefinitions

import java.util.*

data class Transaction(
    val date: Date,
    val amount: Double,
    val description: String
)