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

<div class="col-sm-12"><section class="page-content"><h1 class="title">2: Review update from Previous Lab</h1><div class="rendered-content"><p>In this lab we&#39;ll secure our Family Cash Card API and restrict access to any given Cash Card to the card&#39;s &quot;owner&quot;.</p>
<p>To prepare for this, we introduced the concept of an <code>owner</code> in the application.</p>
<p>The <code>owner</code> is the unique identity of the person who created and can manage a given Cash Card.</p>
<p>Let&#39;s review the following changes we made on your behalf:</p>
<ul>
<li><code>owner</code> added as a field to the <code>CashCard</code> Java record.</li>
<li><code>owner</code> added to all <code>.sql</code> files in <code>src/test/resources/</code></li>
<li><code>owner</code> added to all <code>.json</code> files in <code>src/test/resources/example/cashcard</code></li>
</ul>
<p>All application code and tests are updated to support the new <code>owner</code> field. No functionality has changed as a result of these updates.</p>
<p>Let&#39;s take some time now to familiarize yourself with these updates.</p>

