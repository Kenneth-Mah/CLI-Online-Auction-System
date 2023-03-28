# IS2103-Project-CrazyBids
Contains the files used for IS2103 Pair Project in AY22/23 Sem 2.
IS2103

3 client 
Auction client
OAS Admission panel
SOAP Web services

What are the classes in each client
What needs to be in the EJB?


OAS Administration Panel
Employee (ENTITY)
System Administrator
Finance Staff
Sales Staff

OAS Auction Client
Visitor
Customer (ENTITY)

Proxy Bidding cum Sniping Agent
Premium Customer



Questions:
How to auto close expired auction listings and assign winning bids?
Use a Timer! 🙂
A credit package can only be removed if it is not used. (Used status means credit consumed or credit packaged has been bought?) Otherwise, it should be marked as disabled and customers should not be allowed to purchase a disabled credit package
CreditTransactionEntity tracks the change in credit balance (i.e., can be +ve or -ve)
____________________________________________________________________________
Design:
CrazyBids (Enterprise Application)
CrazyBids-ejb (EJB module)

singleton
DataInitializationSessionBean.java

stateful
<none>

stateless
CustomerSessionBean.java
CustomerSessionBeanLocal.java
EmployeeSessionBean.java
EmployeeSessionBeanLocal.java
AuctionListingBidSessionBean.java 

util
none


OASAdminPanelClient
Main.java
MainApp.java
SystemAdministrationModule.java
FinanceAdministrationModule.java
SalesAdministrationModule.java
OASAuctionClient
Main.java
MainApp.java


SOAPWebService
??? to be implemented


CrazyBidsLibrary (Java Class Library)

singleton


stateful
<none>

stateless
CustomerSessionBeanRemote.java
EmployeeSessionBeanRemote.java

Entity 
CustomerEntity.java
EmployeeEntity.java
AuctionListingBidEntity.java
AuctionListingEntity.java
TransactionEntity.java
CreditPackageEntity.java
CreditTransactionEntity.java


util enumeration
EmployeeTypeEnumeration.java

util exception
EmployeeNotFoundException.java
CustomerNotfoundException.java
BiddingAuctionIsOverException.java
BiddingAuctionNotYetStartException.java
EmployeeAlreadyExistException.java
CustomerAlreadyExistException.java
AuctionListingAlreadyExistException.java
CreditPackageInUseCannotBeRemovedException.java
CreditPackageDisabledException.java
ActionListingInUseCannotBeRemovedException.java
ActionListingDisabledException.java


