# Football World Cup Score Board 

The project has been developed as a result of participating in SportsRadar coding challenge. Its purpose is to return live scores ordered by number of goals and if that number happens to be the same - by the time of adding the match into the system. Finished matches go out of the board. 

### Notice

Please be aware that this is not a standalone application. The project is meant to be used as a library 

## Getting Started

These instructions will help you to get to know the essentials of this application.

### Prerequisites

To run this project you are required to have JRE 17 installed. Maven is not required to be installed, bundled wrapper is recommended to use.

### Building

The project can be built with:

```
./mvnw clean install
```

You can then later use it as a library in other projects.

## Usage

The entry point class for module is: `ScoreBoardService`. There are **four** methods:

-`startMatch`: adds new match with *score* defaulted to 0 - 0, *start date* defaulted current date and *added date* defaulted to current timestamp,

-`updateMatchScore`: updates the *score* for specified match,

-`finishMatch`: marks match as *finished*,

-`getLiveScoreBoard`: returns all the matches that are in progress along with their scores, order by total goals count first, then - time the match was added to the system.

## Unit tests

Tests can validate if eventual changes performed in source code haven't affected the validity of solution. 

### Running tests

Tests can be easily run with the following command:

```
./mvnw test
```
In case of errors, the user will get notified:

```
Failed tests:   testStartMatch
```

## Built With

* [Maven](https://maven.apache.org/) - Maven wrapper


## Author

* **Piotr Ławniczak** - [GitHub profile](https://github.com/plawa)
