package appcore.functionality

sealed class Command {
    object MainAppStop : Command()

    class Add(val listPos: Int, val transaction: Transaction) : Command()

    class Remove(val listPos: Int) : Command()
}