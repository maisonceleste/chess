# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Link to full diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdADZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYHSQ4AAaz5HRgyQyqRgotGMGACClHDCKAAHtCNIziSyTqDcSpyvyoIycSIVKbCkdLjAFJqUMBtfUZegAKK6lTYAiJW3HXKnbLmcoAFicAGZuv1RupgOTxlMfVBvGUVR07uq3R6wvJpeg+gd0BxMEbmeoHUU7ShymgfAgECG8adqyTVKUQFLMlbaR1GQztMba6djKUFBwOHKBdp2-bO2Oaz2++7MgofGBUrDgDvUiOq6vu2ypzO59vdzawcvToCLtmEdDkWoW1hH8C607s9dc2KYwwOUqxPAeu71BAJZoJMIH7CGlB1hGGDlAATE4TgJgMAGjLBUyPFM4GpJB0F4as5acKYXi+P4ATQOw5IwAAMhA0RJAEaQZFkyDmGyv7lNUdRNK0BjqAkaAJqqKCzC8bwcAcv5sl+zr-kMgG7F8slLBp8HKSCjrmo2MAIKxPKwixbGouisTYnehgrkSa5khSg5SaOjlnpOHLcryVqCsKOZqeKkrurKVp5mgEDMAAZr4lFdiyYb5HZTYtm2dk-sUzqUsEh7QEgABeKAcH6AZBmgCEguGPEoTAMYAIxYUmqgpvc6aZtA5Q+Llu75UVywUZWIUyjAACSaAgNAkLgDAWnvCeHmJQUKXztai4ZUl9YlJaPWpH1xWlSggbiZVSE1WA0ZOI1vR9M1rVpn0GZZl1u37QNZjxaeS3JYZlrDtoMCwnNHCspsYAgKktmGWyCWkhucgoNee5EcesOsl5pTRTyV6HjaaOZQ25RI7e0ObXp5QWTykSqB+mB6QT20wKptzqcB+FfERJGlmz5GnZtyEXTA6GYTdUlkQRfSc1B3NwWWH2Vp43h+IEXgoOgzGsb4zAcekmSYALfFZdmFTSD6TE+vUPrNC0omqOJ3RS9BfOFOTTOS4eXMwTzunnN+ZqE8Zms7uZQdgFZGJQw2MNfaSMBY5wSP7rj2juUynmFFOPk4zeAMBZATvDbKSPmIQ8SJDAnswJFMVxZWaNJStzatku9n+4zOVEW9h3HcGin8+dl3XYmLL3SBT2dSqr1QIVxVyxWC1p99K3E+tpMEjHPYcCg3BboeSc5-IqfjujGccpEwwQDQLrJ-ILd1q7Fk1Du1O0-Tm38UzClG2dOSC8LCaDSokrWikI5xMWhDAAA4oBVkOsuL63OobagzoKiQIttbewgEHYe2lhVPuLtfYqXdhBXB4s5ZvwMgHZAsRzLQnDjZO+DlF6x3jhwROKMU4L2Pueby2Nr4H2AEKIsuCJRSiLoeEuIAy5JErtXOOtcuFrgbr9KuaVGFt2ylPGeJV-RHXKnzaqv9B5NRHqmMeHVszdU7tPfqc9PqLXUMogOK9b4bXXg4ns5IwDQNGPvI8nD8YYy8TATBow8YbycVQcEITAIkwbElB+dD3wIE-IQqAkSVJTFCWoNMFR+jZNGtINM9VUKxijE8TiA4cL3D6GsbofQdAIFANKcK6kJhfGyQAOUAu0vYMBGhf2QVVQoAs0IYQTFkmBuT8mAUKcU0p5SpiVJQK03C7TZj1Mac01ZNSOmAW6aMXp-TAGKxooEbAPgoDYG4PAfshgfGGDgXrA2Gjja1AaBgrBu1PYJi6YBQZoYCFAmdN83Bvz9n-LpmkpBUSLQwHhpkWEcA7n0KxIw9xzCexx2xuwm+wAj5rh4VyPhLjBF5xEYXfhqRJHSIrrgquUV5HdTrhE5aKim7pVJq8na1jtHd30X3QxkY6pXRMcmMx7VnqT15bYwB9c2XOLxS3AoaNygIpQA82E2TUYRPyFOD0c5tWryjmTNJ5RkWbhQC-FJULgXDK2pkvoBSinlBKWUmAALEL9yMULcZvRJmjDma6hZHr5ZALOQESw28TLJBgAAKQgDyKBgFAhbJANKBBv8YUoKqJSYSLRsnYJIdBBM1zgBRqgHACAJkoCzGdZ6-SZw7XlEdugUtTSK1VprXW2Z0gATQrbtEgAVomtAsIE1UxQGiCO6KZAb3KKw3FAiCXp3ZMS3kpKhEwHzugURoUqU0vKnS6CDKa7MsUd2SJ0SOXqMoe3LR-V+UnUFSMgeIqh63VMW1R6FiXoytnnK1lP1FUCOVfkVVISKQPIvYlPVHJsW8iNfILdzq90jStAqJUgUWZqg1FqHU+pYiGg3gzaJDzAZ4fdNqbd3o0CR1ha3O9zpXT4a9NBJ9vdv7euFTGeMN07oSp-VK7J+Z8M0dIoBjxSUOgMdKAa2aHbEKQBiWE41DH75mvjaO61qS7UMxUg2n+wr-69BOdRZWAQvDlvgNwPA6psDXNLkep53Es3csqKbc2ltrbGGdk2p8Vx+16cHXCkAtm4SRDAJctAqL6MdgxcfUoW8d6Iz3hww+MGJyn1KOfS+hhSWztVclikGrAJatiQEkjGNctXyQ8AQr87itVMoEOBcGXAnZZq4YG4cT1OmubTZ90UAdO2qfPpv8hnuO1RMz0QBQA) Also [here](FullArchitectureDiagram.svg)
## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
