# Basic Idea

The service receives Instrument and Quote updates from a remote service. Service sends Websocket messages. 

The task is to receive instrument and quotes and generate candlesticks out of them. 


# Implementation details
The service is using Spring boot framework. Due to time constraint and my experience with Spring boot, it was easier to set up DB, Cache and integration tests.

Since the project was set up on Java 11, I had to use spring boot 2.x instead of 3.x.

Refactored the received code. For example separated all the domains into their own data class for more readability and packaged them.

The service uses DB and avoids any in memory data structure in order to avoid loss of data if an instance goes down.

Since incoming quotes from websocket don't have timestamp, it is assumed that they are coming in order, therefore upon saving them, current time is used for the timestamp.

## Workflow
When project starts, `StreamPopulator` starts calling `instrument` and `quote` streams. Instruments are saved in the `instrument` table and quotes are saved in the `quote` table.

Instrument table has a field called `scheduled_to` which is used for the job to decide which instruments should be updated to populate candlestick for them.

There is a job (i.e. `CandlestickJob`) which runs at the beginning of every minute to populate candlesticks. The job looks for instruments that have `scheduled_to` field less than current minute, updates the `scheduled_to` to the future, so they don't get updated again in this round by other instances.

After getting the candidate instruments, it will get the quotes of each instrument from the past minute and create a single candlestick out them.

The job doesn't get locked by one running instance, instead the query (i.e. `findScheduledInstruments`) handles multiple simultaneous calls. If one instance is updating 500 of instruments to populate the candlesticks, the next running instance skips these locked instruments and proceeds with other 500 instruments.

## Drawbacks of current architecture and setup
The current architecture handles only 500 instruments per instance per minute. The assumption was that the candlesticks for all the instruments must get populated in the beginning of every minute, which makes it challenging. However, there is a possibility to run the job more often to cover bigger size of instruments which is out of the scope of this task for now and can be discussed later.

The developed solution of course can handle more than 500 instruments, but it will put a lot of read pressure on the database since a lot of parallel calls are done to the DB.

There should be a job deleting old quotes from the `quote` table regularly otherwise the DB's disk/memory will get full supper quickly. I skipped this part due to time constraint.

No metrics are sent, which can be covered in the future.

## Endpoints

Caching is used for the endpoint to avoid calling the DB over and over. When a new candlestick is added for a specified isin, then the cache for that specific isin will be evicted.

Candlestick endpoint: `http://localhost:9000/api/v1/candlesticks?isin={ISIN}`

Swagger endpoint: `http://localhost:9000/swagger-ui/index.html`

**NOTE:** If you start the application locally, you need to wait until a new minute starts, so the job starts to populate some candlesticks.

## Tests

I've tried to put as many tests as possible, however tests are covering most important aspects of the code, and the coverage is not 100% due to time constraint.

Test for the streams and websocket is not covered, assuming that part works properly, however it should be covered by stubbing the websocket.

Integration tests have the naming convention to end with `*IT.kt` and unit tests have the naming convention to end with `*Test.kt`.

## Starting the application

First call `./start-deps.sh` to start the dependencies, then `StartApplication` in the test package can be called to start the application which starts the application with `local` profile.

For running tests, the docker dependencies should be running `./start-deps.sh`.

To stop the dependencies call `./stop-deps.sh`

## Observation

It is observed that sometimes during the startup of the application the websocket sends a quote for an instrument that does not exist. Since Quote has a foreign key to Instrument `isin`, a runtime exception is thrown, so do not wonder why there is an exception.

## Tech Stack

- Spring boot 2.x
- Kotlin
- Java 11
- Postgres
- Redis

## Future Development

- Handling thousands of instruments
- More test coverage
- Job to delete old quotes to avoid filling up the DB quickly
- Workflow diagram


