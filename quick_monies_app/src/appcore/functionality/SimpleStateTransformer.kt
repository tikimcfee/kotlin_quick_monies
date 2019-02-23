package appcore.functionality


class SimpleStateTransformer {

    fun runTransform(applicationState: ApplicationState,
                     input: String) {

        with(applicationState) {
            commandHistorian.recordRawCommand(input)

            commandProcessor
                    .parseStringCommand(input)
                    .execute(
                            transactionList,
                            projectedTransactionGenerator
                    )
        }
    }

}