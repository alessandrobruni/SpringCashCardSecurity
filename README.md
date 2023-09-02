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






<!DOCTYPE html><html><head><link rel="stylesheet" href="/workshop/static/bootstrap/css/bootstrap.css"><link rel="stylesheet" href="/workshop/static/fontawesome/css/all.min.css"><link rel="stylesheet" href="/workshop/static/highlight.js/styles/default.css"><link rel="stylesheet" href="/workshop/static/styles/educates.css"><link rel="stylesheet" href="/workshop/static/styles/educates-markdown.css"><link rel="stylesheet" href="/workshop/static/theme/workshop-instructions.css"><link rel="shortcut icon" href="/workshop/static/images/favicon.ico"></head><body data-google-tracking-id="" data-clarity-tracking-id="" data-amplitude-tracking-id="" data-workshop-name="course-spring-brasb-6yqkph" data-session-namespace="spring-academy-w07-s869" data-workshop-namespace="spring-academy-w07" data-training-portal="spring-academy" data-ingress-domain="acad-spr-prd3.labs.spring.academy" data-ingress-protocol="https" data-ingress-port-suffix="" data-prev-page="03-add-dependency" data-current-page="04-spring-security-start" data-next-page="05-configure-basic-auth" data-page-format="markdown" data-page-step="4" data-pages-total="12"><div class="header page-navbar sticky-top bg-primary"><div class="row row-no-gutters"><div class="col-sm-12"><div class="btn-group btn-group-sm" role="group"><button class="btn btn-transparent" type="button" data-goto-page="/" aria-label="Home"><span class="fas fa-home fa-inverse" aria-hidden="true"></span></button></div><div class="btn-toolbar float-right" role="toolbar"><div class="btn-group btn-group-sm" role="group"><button class="btn btn-transparent" id="header-prev-page" type="button" data-goto-page="03-add-dependency" disabled="" aria-label="Prev"><span class="fas fa-arrow-left fa-inverse" aria-hidden="true"></span></button><button class="btn btn-transparent" id="header-goto-toc" type="button" aria-label="TOC" data-toggle="modal" data-target="#table-of-contents"><span class="fas fa-list fa-inverse" aria-hidden="true"></span></button><button class="btn btn-transparent" id="header-next-page" type="button" data-goto-page="05-configure-basic-auth" disabled="" aria-label="Next"><span class="fas fa-arrow-right fa-inverse" aria-hidden="true"></span></button></div></div></div></div></div><div class="container-fluid main-content"><div class="row"><div class="col-sm-12"><section class="page-content"><h1 class="title">4: Satisfy Spring Security's Dependencies</h1><div class="rendered-content"><p>Next, we&#39;ll focus on getting our tests passing again by providing the minimum configuration needed by Spring Security.</p>
<p>We&#39;ve provided another file on our behalf: <code>example/cashcard/SecurityConfig.java</code>. This will be the Java Bean where we&#39;ll configure Spring Security for our application.</p>
<ol>
<li><p>Uncomment <code>SecurityConfig.java</code> and review.</p>
<p>Open <code>SecurityConfig</code>.</p>
<p>Notice that most of the file is commented.</p>
<p>Uncomment all commented lines within <code>SecurityConfig</code>.</p>
<pre><code class="hljs language-java"><span class="hljs-keyword">package</span> example.cashcard;
...
public class SecurityConfig {

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.build();

...
</code></pre>
<p><code>filterChain</code> returns <code>http.build()</code>, which is the minimum needed for now.</p>
<p><em>Note:</em> Please ignore the method <code>passwordEncoder()</code> for now.</p>
</li>
<li><p>Enable Spring Security.</p>
<p>At the moment <code>SecurityConfig</code> is just an un-referenced Java class as nothing is using it.</p>
<p>Let&#39;s turn <code>SecurityConfig</code> into our configuration Bean for Spring Security.</p>
<pre><code class="hljs language-java"><span class="hljs-comment">// Add this Annotation</span>
<span class="hljs-meta">@Configuration</span>
<span class="hljs-keyword">public</span> <span class="hljs-keyword">class</span> <span class="hljs-title class_">SecurityConfig</span> {

@Configuration
public class SecurityConfig {

    // Add this Annotation
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.build();
    }
...
</code></pre>
</li>
<li><p>Understand the Annotations.</p>
<ul>
<li><pre><code class="hljs language-java"><span class="hljs-meta">@Configuration</span>
<span class="hljs-keyword">public</span> <span class="hljs-keyword">class</span> <span class="hljs-title class_">SecurityConfig</span> {...}
</code></pre>
<p>The <code>@Configuration</code> annotation tells Spring to use this class to configure Spring and Spring Boot itself. Any Beans specified in this class will now be available to Spring&#39;s Auto Configuration engine. </p>
</li>
<li><pre><code class="hljs language-java"><span class="hljs-meta">@Bean</span>
<span class="hljs-keyword">public</span> SecurityFilterChain filterChain
</code></pre>
<p>Spring Security expects a Bean to configure its <strong>Filter Chain</strong>, which you learned about in the Simple Spring Security lesson. Annotating a method returning a <code>SecurityFilterChain</code> with the <code>@Bean</code> satisfies this expectation.</p>
</li>
</ul>
</li>
<li><p>Run the tests.</p>
<p>When you run the tests you&#39;ll see that once again all tests pass <em>except for the test for creating a new <code>CashCard</code> via a <code>POST</code></em>.</p>
<pre><code class="hljs language-shell">CashCardApplicationTests &gt; shouldCreateANewCashCard() FAILED
    org.opentest4j.AssertionFailedError:
    expected: 201 CREATED
     but was: 403 FORBIDDEN
...
11 tests completed, 1 failed
</code></pre>
<p>This is expected. We&#39;ll cover this in depth a bit later on.</p>
</li>
</ol>
</div>
