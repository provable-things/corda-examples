# Corda Examples

Provable API for R3 Corda.

#### [Join testnet](https://docs.corda.net/head/corda-testnet-intro.html)

#### Build and deploy

After cloning the repo:

```bash
cd corda-example
./gradlew clean jar
```

Copy the cordapp on the remote node:

```bash
scp ./diesel-price/build/libs/diesel-price-0.1.jar remote-server:/opt/corda/cordapps 
```

And restart the service:

```bash
sudo service corda --full-restart 
```

Login into the crash shell
```bash 
ssh username@localhost -p 2222 
```

Then start the flow 
```
>>> flow start DiesePriceOracle

 ✓ Starting
 ✓ Querying Oraclize
          Querying Provable
              Submitting the query.
          Waiting for the result 
     ✓ Giving back the result
 ✓ Creating the transaction
 ✓ Gathering signatures
 ✓ Finalizing transaction
▶︎ Done
Flow completed with result: SignedTransaction(id=8A665A86CCB41E6F085B6537708A7760CD9F3E64CE672AF007EDD426334868B2)
```