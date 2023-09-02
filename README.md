# SpringCashCardSecurity
# 1: Understand our Security Requirements
Who should be allowed to manage any given Cash Card?

In our simple domain, let's state that the user who created the Cash Card "owns" the Cash Card. Thus, they are the "card owner". Only the card owner can view or update a Cash Card.

The logic will be something like this:

IF the user is authenticated

... AND they are authorized as a "card owner"

... ... AND they own the requested Cash Card

THEN complete the users's request

BUT do not allow users to access Cash Cards they do not own.

#2: Review update from Previous Lab
In this lab we'll secure our Family Cash Card API and restrict access to any given Cash Card to the card's "owner".

To prepare for this, we introduced the concept of an owner in the application.

The owner is the unique identity of the person who created and can manage a given Cash Card.

Let's review the following changes we made on your behalf:

owner added as a field to the CashCard Java record.
owner added to all .sql files in src/test/resources/
owner added to all .json files in src/test/resources/example/cashcard
All application code and tests are updated to support the new owner field. No functionality has changed as a result of these updates.

Let's take some time now to familiarize yourself with these updates.
