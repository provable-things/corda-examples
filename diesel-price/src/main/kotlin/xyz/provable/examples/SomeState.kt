package xyz.provable.examples

import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party

data class SomeState(val amount: Int, val owner: Party) : ContractState {
    override val participants = listOf(owner)
}