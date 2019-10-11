# Corda Examples

Provable API for R3 Corda.

#### [Join testnet](https://docs.corda.net/head/corda-testnet-intro.html)

#### Build and deploy

After cloning the repo:

```bash
cd corda-example
./gradlew clean build
```

Copy the cordapp on the remote node:

```bash
scp ./diesel-price/build/libs/diesel-price-0.1.jar remote-server:/opt/corda/cordapps 
```

And restart the service:

```bash
sudo service corda restart 
```