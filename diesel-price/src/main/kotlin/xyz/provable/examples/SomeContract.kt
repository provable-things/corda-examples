package xyz.provable.examples

import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction
import xyz.provable.states.Answer

class SomeContract : Contract {
    companion object {
        val CONTRACT_ID = "xyz.provable.examples.SomeContract"
    }

    override fun verify(tx: LedgerTransaction)  = requireThat {
        val answ = tx.commandsOfType<Answer>().single().value

        "The answer is not empty" using (!answ.isEmpty())
    }
}