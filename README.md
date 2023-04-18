
# IS2103 – Enterprise Systems Server-side Design and Development AY 2022/23 Semester 2

## Pair Project
![image](https://user-images.githubusercontent.com/36888332/232277531-f381d42e-0eb4-4eb4-a216-12fbdecf8b9a.png)

##Project class diagram
![IS2103project drawio](https://user-images.githubusercontent.com/36888332/232276724-66b29c3a-7f15-4e01-b610-5f71c30fd071.png)


### IS2103 Pair Project Architecture and Design

When designing the logical data model, we chose to incorporate unidirectional relationships whenever possible. This is because we realise that a unidirectional relationship would be easier to maintain than a bidirectional relationship when data is modified over the course of business processes.

In the case of time-sensitive attributes as in AuctionListingEntity’s “active” attribute, we chose to keep it as an attribute rather than requiring to obtain the state through a method call that checks whether the current time is between the startDateTime and endDateTime. This is so that looking up an ActionListingEntity’s state is more convenient. We felt that it was not too difficult to have methods that update the “active” attribute whenever the current time passes either the startDateTime or endDateTime.

Regarding TransactionEntity, we decided to view the transactionAmount attribute from the point-of-view of the customer. Logically, if a customer places a bid, his availableBalance would decrease. As such, it would make more sense if the resultant TransactionEntity created would store the transactionAmount as a negative value. On the other hand, if the customer were to be out-bidded and have his credits returned to him, the resultant TransactionEntity created would then store the transactionAmount as a positive value, since the transaction results in a positive change to the customer’s availableBalance.

We created a session bean for each entity class so that we can simply encapsulate the operations associated with a particular entity within that entity’s own session bean. We believe that this leads to a more logical architectural design and cleaner code.

## Contributors
| Full Name | Email |
| --- | --- |
| Mah Hoy Ping, Kenneth | e0943059@u.nus.edu |
| Yeo Wei Han | e0726774@u.nus.edu |
