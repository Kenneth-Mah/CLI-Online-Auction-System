
# IS2103 – Enterprise Systems Server-side Design and Development AY 2022/23 Semester 2
## Pair Project

![image](https://user-images.githubusercontent.com/36888332/232277531-f381d42e-0eb4-4eb4-a216-12fbdecf8b9a.png)

[Link](https://doc-0c-0s-prod-01-apps-viewer.googleusercontent.com/viewer2/prod-01/pdf/1ao04m6oehojg24ofpf5ib54qbmpq36n/0t9h4tlhha86k4ddorfk445lq9gofvgt/1681627425000/3/109246059685749923494/APznzaaW28BpCddVkNQw21kPnulTqowc7qJHptFZKT5kpllHf16sE2XUur8mwLKo9ma4NXz-EFmqfvW62FZqMUYBf9Ae0Egw42qsmmmT8W4mQBmTTXEAbT4DP2CxMeT4yp2v6v2sB_sidOpgB6KHM1umbV-aYzdiDv1pKCPi-T2n2NsK4q0fRAersZfSKXWvEtMriDI34Cj4LpF35tHr3a3uqq2UyxvGt0VL3r6p_ZtILKtbMMLj2lGj0svJ4FmC0bIQXt3I6qfu4XkJrKgsT699UOp7KPeM2egdbizlTkqbotOZyNwGwW73dY_Mv5mjF2K6lvWXToFs1SGKio-4_9C8qiYCN5aW3eY_kITo8Qm2MDOioN25I96wvSBmr8oHYWJUBEU5mu60YR1JIq7ozUx3v19i3ztrqw==?authuser=0&nonce=tlckkgfr4o6qm&user=109246059685749923494&hash=d0h98hr3mtmeddo48vha34phnr78jkar) to our project description. 

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
