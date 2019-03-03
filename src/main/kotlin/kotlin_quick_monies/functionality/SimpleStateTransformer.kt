package appcore.functionality

import kotlin_quick_monies.functionality.commands.Command


class SimpleStateTransformer {
    
    /**
     * So long - you've been fun.
     */
    //fun runTransform(
    //    applicationState: ApplicationState,
    //    input: String
    //) {
    //    with(applicationState) {
    //        runTransform(
    //            this,
    //            commandProcessor.parseStringCommand(input)
    //        )
    //    }
    //}
    fun runTransform(
        applicationState: ApplicationState,
        command: Command
    ) {
        with(applicationState) {
            commandHistorian.recordCommand(command)
            command.execute(
                transactionList, projectedTransactionGenerator
            )
        }
    }
    
}