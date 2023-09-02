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

<h1 class="title">3: Add the Spring Security Dependency</h1><div class="rendered-content"><p>We can add support for Spring Security by adding the appropriate dependency.</p>
<ol>
<li><p>Add the dependency.</p>
<p>Add the following to the <code>build.gradle</code> file in the <code>dependencies {}</code> section:</p>
<pre><code class="hljs language-groovy">dependencies {
    implementation &#x27;org.springframework.boot:spring-boot-starter-web&#x27;

    // Add the following dependency
    implementation &#x27;org.springframework.boot:spring-boot-starter-security&#x27;
    ...
</code></pre>
</li>
<li><p>Run the tests.</p>
<p>We&#39;ve added Spring Security capabilities to our application, but changed no code.</p>
<p>So what do we expect to happen when we run the tests?</p>
<p>Note that we will always run <code>./gradlew test</code> to run the tests.</p>
<pre><code>[~/exercises] $ ./gradlew test
...
CashCardApplicationTests &gt; shouldReturnASortedPageOfCashCards() FAILED
...
CashCardApplicationTests &gt; shouldReturnACashCardWhenDataIsSaved() FAILED
...
CashCardApplicationTests &gt; shouldCreateANewCashCard() FAILED
...
CashCardApplicationTests &gt; shouldReturnAPageOfCashCards() FAILED
...
CashCardApplicationTests &gt; shouldReturnAllCashCardsWhenListIsRequested() FAILED
...
CashCardApplicationTests &gt; shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() FAILED
...
CashCardApplicationTests &gt; shouldNotReturnACashCardWithAnUnknownId() FAILED
11 tests completed, 7 failed
&gt; Task :test FAILED
</code></pre>
<p>Things are really broken!</p>
<p>Every test method within <code>CashCardApplicationTests</code> failed.</p>
<p>Many failures are similar the the one below:</p>
<pre><code class="hljs language-bash">expected: &lt;SOME NUMBER&gt;
 but was: 0
</code></pre>
<p>In most cases, our tests expect <code>CashCard</code> data to be returned from our API, but nothing was returned.</p>
<p>Why do you think all tests of our Cash Card API are failing after adding the Spring Security dependency?</p>
</li>
<li><p>Understand why everything is broken.</p>
<p>So what happened?</p>
<p>When we added the Spring Security dependency to our application, <em>security was enabled by default.</em></p>
<p>Since we have not specified how authentication and authorization are performed within our Cash Card API, Spring Security has completely locked down our API.</p>
<p>Better safe than sorry, right?</p>
</li>
</ol>
<p>Next, let&#39;s configure Spring Security for our application.</p>

