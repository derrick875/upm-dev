
## NPS Paynow Technical Design
---
### Reference Documentation
>NETS QR Switch - Paynow Interface Specification v1.0.1-DRAFT.docx
>NPS Message Spec in JSON v2.4 - Final.


### Terminology and Convention
---
* APS – Alternate Payment Service
* NPS – New Payment System
* APIGW -API Gateway
* Paynow-Paynow process Processing Paynow QR transactions from receiver bank.Also It handle Order/Enquiry/Reversal requests from ChannelSystems.It send Payment notifications to Notification module and Transaction state messages to Settlement System.At the end of the day it recon transaction with Settlement system. 
* Solace - Solace is the message bus to communicate across nets subsystems and between NPS modules.
* Notification - Notification communicates with NETS Biz Merchants through APIGW
* ChannelSystem(IP Terminal) -Order Channel
* ChannelSystem(MBiz App)-StaticQR Merchant APP
* ReceiverBank -Source Of Fund
* SettlementSystem -Tandem Settling with Banks As NETS as a Master Acquirer
* FraudwallSystem -Fraud transaction detection.

### High Level System Architecture (NPS Transaction Flow)
---
Below diagrams gives an internal overview of different components of the system

<!-- High level System Diagram Start-->

```plantuml
@startuml PaynowSystemArchitecture
@startuml Paynow System Diagram
skinparam databaseBackgroundColor Aqua
skinparam QueueBackgroundColor GreenYellow
skinparam CardBackgroundColor Teal

package "NPS" #LightCyan {
    [PAYNOW]
    [Notification]
    [FRAUDWALL]
    database "NPS DB"
    card "HSM"
     }
card "ChannelSystem" #LightYellow{
rectangle "IPTerminal" #Tan
rectangle "MBizApp" #Gray
}

queue "Solace"
cloud "API Gateway" #SteelBlue
cloud "WebProxy" #Green
rectangle "ReceiverBank" #Gold
rectangle "SettlementSystem" #DarkGoldenRod
rectangle "FraudwallSystem" #Maroon

[IPTerminal] <-->[WebProxy] : <<tcp>>
[WebProxy] <-->[PAYNOW] : <<tcp>>
[PAYNOW] <-->[HSM] :"<<Rest API>>"
[Solace] --> [SettlementSystem]
[PAYNOW] ..> [SettlementSystem]
[PAYNOW] ..> [API Gateway]
[PAYNOW] ..> [Notification]
[PAYNOW] ..> [FRAUDWALL]
[PAYNOW] <----> [NPS DB] :"<<Mysql>>"
[PAYNOW] <-->[ReceiverBank]: <<https>> 
[PAYNOW]<-->[Solace]
[API Gateway] <-->[ReceiverBank] :<<https>>
[API Gateway]<-->[Solace]
[API Gateway]..>[PAYNOW]
[API Gateway] -->[MBizApp] :<<https>>
[MBizApp] <-->[API Gateway] :<<https>>
[Notification] ---->[API Gateway]: <<https>>
[Solace] --> [Notification]
[Solace] -->[FRAUDWALL]
[FRAUDWALL] <-->[FraudwallSystem]:<<https>>
@enduml
@enduml

```
<!-- High level System Diagram End-->

### MTI & Process Codes
---
| API                        | Request MTI | Request  Process Code | Response MTI | Response  Process Code |
|----------------------------|-------------|-----------------------|--------------|------------------------|
| Paynow Credit Notification | 8200        | 420000                | 8210         | 420000                 |
| Paynow Order               | 0200        | 995000                | 0210         | 995000                 |
| Paynow Order Query         | 0100        | 335000                | 0110         | 335000                 |
| Paynow Order Reversal      | 0400        | 995000                | 0410         | 995000                 |
| Refund Reversal            | 8400        | 440000                | 8410         | 440000                 |

### Sequence Diagrams
---

#####1.Paynow Order Request/Response
---

<!-- Paynow order Sequence Diagram Start-->

```plantuml
@startuml PaynowOrderSequenceDiagram
@startuml Paynow Order Request
skinparam style strictuml
ChannelSystem  -> PAYNOW : Paynow Order Request
PAYNOW -> PAYNOW : Msg Format Validation,Internal Order request creation
PAYNOW --> ChannelSystem : Paynow Order Response(Error)\n <<Format Error>>
PAYNOW -> PAYNOW : Paynow Order Request
PAYNOW -> PAYNOW: Business validation(Duplicate order RRN Check,MID,TID validation,\n Currency,Channel indicator validation)
PAYNOW --> ChannelSystem : Paynow Order Response(Error)\n <<Business Error>>
PAYNOW -> PAYNOW: Processing /Persistance \n (Merchant Short Name retrieval,Dynamic Code creation,Stan Generation,\n Paynow Order Persistance)
PAYNOW -> PAYNOW: Generate Paynow QR
PAYNOW -> PAYNOW : Paynow Order response \n (External Response Creation) 
PAYNOW  -> ChannelSystem :Paynow Order Response
@enduml
@enduml
```
<!-- Paynow order Sequence Diagram End-->

#####2 .Paynow Credit Notification Request/Response
---
<!-- Paynow Credit notification Sequence Diagram Start-->

```plantuml
@startuml CreditNotificationSequenceDiagram
@startuml Paynow Credit Notification
skinparam style strictuml
ReceiverBank  -> PAYNOW : Paynow Credit Notification Request
PAYNOW -> PAYNOW : Msg Format Validation,internal Credit Notification Req creation
PAYNOW --> ReceiverBank : Paynow Credit Notification Response(Error)\n <<Format Error>>
PAYNOW -> PAYNOW:Identify Static Credit Notification \n Or Dynamic Credit Notification. \n Verify merchant ref No,Transaction type,Currency
PAYNOW --> ReceiverBank : Paynow Credit Notification Response(Error)\n <<Business Error>>

|||
====
note over PAYNOW #Cyan: If Paynow Static QR.
PAYNOW -> PAYNOW: Business validation(Validate Duplicate credit notification,\n Lookup NETS MID TID based on MPAN ,Validate NETS MID TID,\n validate merchant shortname) 
note over PAYNOW #Crimson:If Business validation fail.
PAYNOW -->ReceiverBank :Paynow Credit Notification Response(Error) \n <<Business Validation failed>>

note over PAYNOW #SeaGreen:If Business validation pass.
PAYNOW -> PAYNOW: Processing /Persistance \n (Create Paynow Static QR Request,Generate Stan, \n Calculate Settlement Date,\n Create Transaction,Store Paynow data)
PAYNOW -> PAYNOW :Validate Exceptional Scenarioes(pos active,merchant active,\n Acquirer active,Acquirer merchant Mapping active,.Credit notification expiration check with crediting time)

note over PAYNOW #Crimson:If validation fail.
PAYNOW -> PAYNOW:Mark Transaction as reversed.
PAYNOW -> PAYNOW:Create Refund reversal request.
PAYNOW -> ReceiverBank :Refund_Reversal Request.
ReceiverBank ->PAYNOW :Refund_Reversal Response.

note over PAYNOW #SeaGreen:If validation pass.
PAYNOW -> PAYNOW : Retrieve communication Data For the Terminal
PAYNOW -> SettlementSystem :Send Transaction State message
PAYNOW -> FraudWall :Send fraudwall message
PAYNOW -> Notification :Paynow Static QR notification
Notification -> APIGW :Paynow Static QR Notification Message \n (Notification to NETS Biz)
PAYNOW -> PAYNOW : Generate Paynow Credit Notification Response \n (External Response Creation)
PAYNOW --> ReceiverBank : Paynow Credit Notification Response(Success)

====
note over PAYNOW #Cyan: If Paynow Dynamic QR.
PAYNOW -> PAYNOW: Business validation( Validate Duplicate credit notification \n Lookup NETS TID,Validate NETS MID TID) 
PAYNOW -> PAYNOW: Processing  \n (Match Paynow QR Order Request based on dynamic code).
PAYNOW -> PAYNOW:Retrieve original order

note over PAYNOW #Crimson:If original order not found.
PAYNOW -->ReceiverBank :Paynow Credit Notification Response(Error) \n <<If Paynow Order Not Match>>

note over PAYNOW #SeaGreen:If original order found.
PAYNOW -> PAYNOW: Processing /Persistance (\n Calculate Settlement Date,\n Create Transaction,Store Paynow data,\n update original order with transaction id and payment type)
PAYNOW -> PAYNOW :Validate Exceptional Scenario(pos active,merchant active,\n Acquirer active,\n Acquirer merchant Mapping active,Order State not in pending state)

note over PAYNOW #Crimson:If validation fail.
PAYNOW -> PAYNOW:Mark Transaction as reversed.
PAYNOW -> PAYNOW:Create Refund reversal request.
PAYNOW -> ReceiverBank :Refund_Reversal Request.
ReceiverBank ->PAYNOW :Refund_Reversal Response.

note over PAYNOW #SeaGreen:If validation pass.
PAYNOW -> SettlementSystem :Send Transaction State message
PAYNOW -> FraudWall :Send fraudwall message
PAYNOW -> PAYNOW : Generate Paynow Credit Notification Response \n (External Response Creation)
PAYNOW --> ReceiverBank : Paynow Credit Notification Response(Success)

|||

@enduml

@enduml
```
<!-- Paynow Credit notification Sequence Diagram End-->

##### 3.Paynow Order Query Request/Response
---
<!-- Paynow Order Query Sequence Diagram Start-->

```plantuml
@startuml OrderQuerySequenceDiagram
@startuml Paynow Order Query Request
skinparam style strictuml
ChannelSystem  -> PAYNOW : Paynow Order Query Request
PAYNOW -> PAYNOW : Msg Format Validation,Populate Paynow Enquiry request
PAYNOW --> ChannelSystem : Paynow Order Query Response(Error)\n <<Format Error>>
PAYNOW -> PAYNOW: Business validation(Check Paynow order already exist based on dynamic code in merchant ref)
====
note over PAYNOW #OrangeRed:Original Order not found
PAYNOW -->ChannelSystem :Paynow Order Query Response(Error Code-15 etc)
====
note over PAYNOW #SeaGreen:Original Order found
PAYNOW -> PAYNOW:Retrieve matching transaction based on transaction id.
====
note over PAYNOW #IndianRed:Transaction id not found in original order.
note over PAYNOW #coral: If Requested Order Data(Transaction id) not found. search count <=MAX Search Count and \n Order status pending
PAYNOW -> ChannelSystem: PayNow order query response with Response code 03(Pending)
note over PAYNOW #coral: If Requested Order Data(Transaction id) not found. search count <=MAX Search Count and \n Order status timeout,Reversed
PAYNOW -> ChannelSystem: PayNow order query response with Response code 68(Timeout)
note over PAYNOW #coral: If Requested Order Data(Transaction id) not found. search count >MAX Search Count
PAYNOW -> ChannelSystem: PayNow order query response with Response code 06(SystemError)
====
note over PAYNOW #Lime: Transaction id Found in original order.
PAYNOW -> PAYNOW: Processing /Data Retrieval(Transaction and paynow data)
note over PAYNOW #coral: If Requested Order Data(Transaction id) found.Transaction reversed or reversal failed.
PAYNOW -> ChannelSystem: PayNow order query response with Response code 68(Timeout)

note over PAYNOW #SeaGreen: If Requested Order Data(Transaction id) found.Transaction OK or Settled.
PAYNOW -> ChannelSystem:PayNow order query response with Response code 00 and transaction data
====
@enduml
@enduml
```
<!-- Paynow Order Query Sequence Diagram End-->


##### 4.Paynow Order Reversal Request/Response

<!-- Paynow Order Reversal Sequence Diagram Start-->

```plantuml
@startuml OrderReversalSequenceDiagram
@startuml Paynow Order Reversal Request
skinparam style strictuml
ChannelSystem  -> PAYNOW : Paynow Order Reversal Request.
PAYNOW -> PAYNOW : Msg Format Validation,Populate Paynow reversal request.
PAYNOW --> PAYNOW : Paynow Order Reversal Response(Error)\n <<Format Error>>
PAYNOW -> PAYNOW: Processing/Data Retrieval based on dynamic code in merchant reference.
====
note over PAYNOW #OrangeRed:Original Order not found
PAYNOW -->ChannelSystem :Paynow Order reversal response(Error Code-15 etc)
====
note over PAYNOW #SeaGreen:Original Order found
PAYNOW -> PAYNOW:Retrieve matching transaction based on transaction id inside order.
====
note over PAYNOW #IndianRed:Transaction id not found in original order.
note over PAYNOW #coral: If Requested Order Data(Transaction id) not found. Order status pending Or Order status Timeout
PAYNOW --> PAYNOW :Set Order status to reversed
PAYNOW -> ChannelSystem: PayNow order reversal response with Response code 00(Success)

note over PAYNOW #coral: If Requested Order Data(Transaction) not found.Reversed
PAYNOW -> ChannelSystem: PayNow order reversal response with Response code 68(Timeout)
====
note over PAYNOW #Lime: Transaction id Found in original order.
PAYNOW -> PAYNOW: Processing /Data Retrieval(Transaction and paynow data)

note over PAYNOW #coral: If Requested Order transaction found.Transaction reversed or reversal failed.
PAYNOW -> ChannelSystem: PayNow order query response with Response code 68(Timeout)

note over PAYNOW #SeaGreen: If Requested Order transaction found.Transaction OK or Settled.
PAYNOW -> PAYNOW:Update transaction to reversed
PAYNOW ->ReceivingBank:Create and send refund_reversal request to receiving bank
ReceivingBank ->PAYNOW:refund_reversal response.
PAYNOW ->SettlementSystem:Send Transaction State message.
PAYNOW -> ChannelSystem:PayNow order query response with Response code 00 

@enduml

@enduml

```

### Queue and Topic Name
---
#### API Gateway to Inbound:

##### Paynow Credit Notification
Queue- Q.CPS.00.P101.REQ.JSON.SUB   
Topic- P101/G/A/SUB/REQ/TRX/APIGW01/PAY/PNCRDNOTIFY/NIL/V1/ANY/>



#### Transaction State Message

Solace Topic: P101/G/A/SUB/ANY/TRX/CPS/PAY/TXNSTATE/NIL/V1/JSON/{ transaction_type}/{institution_code}/{status}/{response_code}/{acquirer_institution_code}/{transaction_id}

transaction_type -> PNSTATICCN/PNDYNAMICCN/PNDYNAMICREVERSAL
institution_code -> 30000000033,30000000034,30000000035

#### Fraud wall message
Solace Topic:P101/G/A/LOC/RES/TRX/CPSPAYNOW/CPSFW/PAY/ANY/NIL/V1/JSON/1

### Paynow QR Formatting (QR String)
---
##### Merchant Reference Number:
###### Static PayNow QR: 
MPAN (16) + Type Indicator (1) + ShortName (8)
###### One-time use PayNow QR: 
Dynamic code (16) + Type Indicator (1) + ShortName (8)

##### Type Indicator (Proposed):
###### Static PayNow QR:
 Space (0x20h)
###### One-time use PayNow QR: 
 Hyphen (0x2Dh)
###### Switch model: 
 Period (0x2Eh)

### Paynow QR Formatting (QR Image)
---
250 px * 250 px



### Message Encryption and Signature
---
####OCBC
Digital signature
To consume OCBC transactional APIs,Bank has utilized the OAuth 2.0 authorization framework that allows third party applications to gain limited access in both time and scope to customer account details.
1.Authorization API
The access token returned from the Authorization Server will be used to:
• Distinguish between different our user session
• Validate user consent against requested permission
• Verify that user session has not expired
2.Transaction Refund API
This API allows merchants to refund the payer for double debit/dispute.

####DBS
RSA and AES Encryption and Decryption are to be implemented at Merchant and DBS while transmitting request and response.

At the time of merchant on-boarding at DBS, DBS shares RSA public key .cert format, and merchant to share their RSA public key in base64encodedString. Merchant and DBS to test encryption and decryption manually/using automated tool given by DBS. Upon verification is successful, Merchant can continue to invoke DBS API services.
https://<IP_or_DNS_as_per_environment>/middleware

AES
1. Mode : CBC
2. Key Size : 128 (Java implicit)
3. Block Size : 128
4. Padding : PKCS7(C#.net) / PKCS5(Java)- Seems similar except bit length according to tech blogs.
5. Initialization Vector of 16bytes(128-bits):
a. C#.net : 1234567898765432
b. Java : “1234567898765432” String of 16-Char
6. Passphrase for Key Generation: Alphanumeric (16 chars).

RSA
RSA/ECB/PKCS1Padding

####UOB
JWT signature 
For each API call, customer is required to generate a customized JWT in the HTTP header.The purpose of JWT is to ensure that the request payload is signed by the API requester.
