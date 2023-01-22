# Hotel Booking System API

Booking API which allows users to place, modify or cancel reservations from hotels' rooms. It also let hotel administrators get all the created reservations.

It has been developed in Java and deployed in AWS Lambda using a custom runtime powered by GraalVM. It includes unit testing with JUnit 5 and Mockito. It uses PostgreSQL as its main database and CloudFormation templates to create all the infrastructure.

## Technologies used
<img src="https://user-images.githubusercontent.com/25181517/183896132-54262f2e-6d98-41e3-8888-e40ab5a17326.png" width="70" height="70" /> &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" width="70" height="70" /> &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/25181517/183017085-067f30b6-1032-4f89-adc4-ba917d6d0f3a.png" width="70" height="70" /> &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/25181517/117207242-07d5a700-adf4-11eb-975e-be04e62b984b.png" width="70" height="70" /> &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/25181517/117533873-484d4480-afef-11eb-9fad-67c8605e3592.png" width="70" height="70" /> &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/25181517/183892181-ad32b69e-3603-418c-b8e7-99e976c2a784.png" width="70" height="70" /> &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/25181517/117208740-bfb78400-adf5-11eb-97bb-09072b6bedfc.png" width="70" height="70" />

## Data model
Data was organized in three tables: hotels, rooms and reservations, which are described below.

#### Hotels
| Column name  | Data type    | Description                                                  |
| :-----       | :---         | :---                                                         |
| id     	   | UUID         |	Unique identifier                                            |
| name	       | VARCHAR(255) |	Hotel name                                                   |
| country_code | VARCHAR(2)   |	Hotel country code (based on ISO 3166-1), e.g. PE, CA, etc.  |
| city         | VARCHAR(60)  |	Hotel city                                                   |
| latitude     | float8	      | Hotel location latitude                                      |
| longitude    | float8	      | Hotel location longitude                                     |

#### Rooms
| Column name      | Data type   | Description                                                   |
| :-----           | :---        | :---                                                          |
| id     	       | UUID        |	Unique identifier                                            |
| hotel_id         | UUID        |	Foreign key to hotel identifier                              |
| type             | VARCHAR(50) |	Room type, e.g. Standard, Premium, etc.                      |
| number_of_guests | smallint    |	Amount of people which can share the room                    |
| price            | int4	     | Price                                                         |
| currency_code    | VARCHAR(3)	 | Price's currency code (based on ISO 4217), e.g. PEN, CAD etc. |

#### Reservations
| Column name   | Data type | Description                           |
| :-----        | :---      | :---                                  |
| id	        | UUID	    | Unique identifier                     |
| room_id	    | UUID	    | Foreign key to room identifier        |
| checkin_date	| date	    | Date on which the accomodation begins |
| checkout_date	| date	    | Date on which the accomodation ends   |
| guest_id	    | UUID	    | Cognito's guest identifier            |

The relationship between these tables can be seen in the following Entity-Relationship diagram:
![Database ER Diagram](./img/db_er_diagram.png)

## Architecture

The architectured solution consists of a collection of serverless functions (AWS Lambda) with native authentication handling (Amazon Cognito), native API handling (Amazon API Gateway) and a relational database (Amazon RDS) with credentials stored in a secured way (AWS Secrets Manager). Deployment must be done in **multiple availability zones** to ensure **high availability**.

![Architecture](./img/architecture.png)

| Id    | Description
| :---:  | :---
| 1     | User sends his/her credentials to Cognito (using Hosted UI) to retrieve a JWT
| 2     | The credentials are sent together with the request to API Gateway
| 3     | API Gateway validates the credentials against Cognito and if valid, authorizes the request 
| 4     | API Gateway sends the payload to Lambda function including the user identifier from Cognito
| 5     | Lambda function retrieves the database credentials from Secrets Manager
| 6     | Lambda function connects to database using the retrieved credentials and performs the operation requested

Important considerations:
* Lambda functions and Database are deployed in private subnets from a VPC. Therefore, to have access to an external service such as Secrets Manager, a VPC endpoint per availability zone must be added.

* "Get Hotels" and "Get Room's Availability" functions do not require user login, i.e. API Gateway does not validate the JWT against Cognito. Clients should be able to know a room's availability without having to create an account to place a reservation.

* The administrator workflow is similar to the client user's one. The only difference is that it validates the JWT against a different User Pool from Cognito (which only has credentials from the hotel administrators).