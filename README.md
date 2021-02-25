
Credit Decision Engine

## Application form takes the Input
#### SSN Number :
#### Loan Amount :
#### Current Annual Income :

## Steps to Run the App
1. Credit Score Engine
The application should be run from
https://github.com/bhanukiranch/credit-score.git


2. Run the Credit Engine
##### Apply Loan
`POST http://localhost:8080/api/v1/loan/apply`

###### Request Body
`{
	"ssnNumber" : "519319209",
	"loanAmount" : 24000,
	"annualIncome" : 90000
}`

###### Response
`{
    "referenceNumber": "20210225135775097",
    "sanctionedAmount": 45000
}`