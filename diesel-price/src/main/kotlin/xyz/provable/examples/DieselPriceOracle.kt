package xyz.provable.examples

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import xyz.provable.flows.ProvableQueryAwaitFlow
import xyz.provable.flows.ProvableSignFlow
import xyz.provable.states.ProofStorage
import xyz.provable.states.ProofType
import xyz.provable.utils.ProofVerificationTool
import xyz.provable.utils.ProvableUtils
import java.util.function.Predicate

@StartableByRPC
@InitiatingFlow
class DieselPriceOracle : FlowLogic<SignedTransaction>() {
    companion object {
        object QUERYING_ORACLIZE : ProgressTracker.Step("Querying Oraclize")
        object CREATING_TRANSACTION : ProgressTracker.Step("Creating the transaction")
        object GATHERING_SIGNATURES : ProgressTracker.Step("Gathering signatures")
        object FINALIZING_TRANSACTION : ProgressTracker.Step("Finalizing transaction")

        fun tracker() = ProgressTracker(QUERYING_ORACLIZE, CREATING_TRANSACTION,
                GATHERING_SIGNATURES, FINALIZING_TRANSACTION)
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call() : SignedTransaction {
        progressTracker.currentStep = QUERYING_ORACLIZE

        val query = "xml(https://www.fueleconomy.gov/ws/rest/fuelprices).fuelPrices.diesel"
        val answer = subFlow(ProvableQueryAwaitFlow(
                "URL",
                query,
                ProofType.TLSNOTARY,
                0))

        progressTracker.currentStep = CREATING_TRANSACTION

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val oracle = ProvableUtils.getPartyNode(serviceHub)
        val builder = TransactionBuilder(notary)
        val commandWithAnswer = Command(answer, listOf(oracle.owningKey, ourIdentity.owningKey))

        builder.addCommand(commandWithAnswer)
        builder.addOutputState(SomeState(10, ourIdentity), SomeContract.CONTRACT_ID)
        builder.verify(serviceHub)

        progressTracker.currentStep = GATHERING_SIGNATURES

        val onceSigned = serviceHub.signInitialTransaction(builder)
        val filtering = ProvableUtils()::filtering
        val ftx = builder
                .toWireTransaction(serviceHub)
                .buildFilteredTransaction(Predicate { filtering(oracle.owningKey, it) })

        val oracleSignature = subFlow(ProvableSignFlow(ftx))
        val fullSigned = onceSigned + oracleSignature

        progressTracker.currentStep = FINALIZING_TRANSACTION

        return subFlow(FinalityFlow(fullSigned))
    }
}