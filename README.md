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






<h1 class="title">5: Configure Basic Authentication</h1><div class="rendered-content"><p>Thus far we have bootstrapped Spring Security, but not actually secured our application.</p>
<p>Now let&#39;s secure our application by configuring <strong>basic authentication</strong>.</p>
<ol>
<li><p>Configure basic authentication.</p>
<p>Update <code>SecurityConfig.filterChain</code> with the following to enable basic authentication:</p>
<pre><code class="hljs language-java"><span class="hljs-meta">@Bean</span>
<span class="hljs-keyword">public</span> SecurityFilterChain <span class="hljs-title function_">filterChain</span><span class="hljs-params">(HttpSecurity http)</span> <span class="hljs-keyword">throws</span> Exception {
  http.authorizeHttpRequests()
    .requestMatchers(<span class="hljs-string">&quot;/cashcards/**&quot;</span>)
    .authenticated()
    .and()
    .csrf().disable()
    .httpBasic();
  <span class="hljs-keyword">return</span> http.build();
}
</code></pre>
</li>
<li><p>Understand the Spring Security configuration.</p>
<p>That&#39;s a lot of method calls!</p>
<p>Here if we explain Spring Security&#39;s builder pattern in more understandable language, we see:</p>
<blockquote>
<p>All HTTP requests to <code>cashcards/</code> endpoints are required to be authenticated using HTTP Basic Authentication security (username and password).</p>
<p>Also, do not require CSRF security.</p>
</blockquote>
<p><strong>Note:</strong> We&#39;ll talk about CSRF security later in this lab.</p>
</li>
<li><p>Run the tests.</p>
<p>What will happen when we run our tests?</p>
<p>When you run the tests, you&#39;ll notice that most tests fail with a <code>401 UNAUTHORIZED</code> HTTP status code, such as the following:</p>
<pre><code class="hljs language-shell">expected: 200 OK
  but was: 401 UNAUTHORIZED
</code></pre>
<p>Though it might not look like it, <em>this is progress!</em></p>
<p>We&#39;ve enabled basic authentication requiring that requests must supply a username and password.</p>
</li>
</ol>
<p>Our tests do not provide a username and password with our HTTP requests. So let&#39;s do that next.</p>
</div>






<div class="container-fluid main-content"><div class="row"><div class="col-sm-12"><section class="page-content"><h1 class="title">6: Testing Basic Authentication</h1><div class="rendered-content"><p>As we learned in the accompanying lesson, there are many ways of providing user authentication and authorization information for a Spring Boot application using Spring Security.</p>
<p>For our tests, we&#39;ll configure a test-only service that Spring Security will use for these this purpose: an <code>InMemoryUserDetailsManager</code>.</p>
<p>Similar to how we configured an in-memory database using H2 for testing Spring Data, we&#39;ll configure an in-memory service with test users to test Spring Security.</p>
<ol>
<li><p>Configure a test-only <code>UserDetailsService</code>.</p>
<p>Which username and password should we submit in our test HTTP requests?</p>
<p>When you reviewed changes to <code>src/test/resources/data.sql</code> you should&#39;ve seen that we set an <code>OWNER</code> value for each <code>CashCard</code> in the database to the username <code>sarah1</code>. For example:</p>
<pre><code class="hljs language-sql"><span class="hljs-keyword">INSERT</span> <span class="hljs-keyword">INTO</span> CASH_CARD(ID, AMOUNT, OWNER) <span class="hljs-keyword">VALUES</span> (<span class="hljs-number">100</span>, <span class="hljs-number">1.00</span>, <span class="hljs-string">&#x27;sarah1&#x27;</span>);
</code></pre>
<p>Let&#39;s provide a test-only <code>UserDetailsService</code> with the user <code>sarah1</code>.</p>
<p>Add the following Bean to <code>SecurityConfig</code>.</p>
<pre><code class="hljs language-java"><span class="hljs-meta">@Bean</span>
<span class="hljs-keyword">public</span> UserDetailsService <span class="hljs-title function_">testOnlyUsers</span><span class="hljs-params">(PasswordEncoder passwordEncoder)</span> {
 User.<span class="hljs-type">UserBuilder</span> <span class="hljs-variable">users</span> <span class="hljs-operator">=</span> User.builder();
 <span class="hljs-type">UserDetails</span> <span class="hljs-variable">sarah</span> <span class="hljs-operator">=</span> users
   .username(<span class="hljs-string">&quot;sarah1&quot;</span>)
   .password(passwordEncoder.encode(<span class="hljs-string">&quot;abc123&quot;</span>))
   .roles() <span class="hljs-comment">// No roles for now</span>
   .build();
 <span class="hljs-keyword">return</span> <span class="hljs-keyword">new</span> <span class="hljs-title class_">InMemoryUserDetailsManager</span>(sarah);
}
</code></pre>
<p>This <code>UserDetailsService</code> configuration should be understandable: configure a user named <code>sarah1</code> with password <code>abc123</code>.</p>
<p>Spring&#39;s IoC container will find the <code>UserDetailsService</code> Bean and Spring Data will use it when needed.</p>
</li>
<li><p>Configure Basic Auth in HTTP tests.</p>
<p>Select one test method that uses <code>restTemplate.getForEntity</code> and update it with basic authentication for <code>sarah1</code>.</p>
<pre><code class="hljs language-java"><span class="hljs-keyword">void</span> <span class="hljs-title function_">shouldReturnACashCardWhenDataIsSaved</span><span class="hljs-params">()</span> {
    ResponseEntity&lt;String&gt; response = restTemplate
            .withBasicAuth(<span class="hljs-string">&quot;sarah1&quot;</span>, <span class="hljs-string">&quot;abc123&quot;</span>) <span class="hljs-comment">// Add this</span>
            .getForEntity(<span class="hljs-string">&quot;/cashcards/99&quot;</span>, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ...
</code></pre>
</li>
<li><p>Run the tests.</p>
<p>The updated test that provides the basics should now pass!</p>
<pre><code class="hljs language-shell">...
CashCardApplicationTests &gt; shouldReturnACashCardWhenDataIsSaved() PASSED
...
</code></pre>
</li>
<li><p>Update all remaining <code>CashCardApplicationTests</code> tests and rerun tests.</p>
<p>Now for some tedium: update all remaining <code>restTemplate</code>-based tests to supply <code>.withBasicAuth(&quot;sarah1&quot;, &quot;abc123&quot;)</code> with every HTTP request.</p>
<p>When finished, rerun the test.</p>
<pre><code class="hljs language-shell">BUILD SUCCESSFUL in 9s
</code></pre>
<p>Everything passes!</p>
<p>Congratulations, you&#39;ve implemented and tested Basic Auth!</p>
</li>
<li><p>Verify Basic Auth with additional tests.</p>
<p>Now let&#39;s add tests that expect a <code>401 UNAUTHORIZED</code> response when incorrect credentials are submitted using basic authentication.</p>
<pre><code class="hljs language-java"><span class="hljs-meta">@Test</span>
<span class="hljs-keyword">void</span> <span class="hljs-title function_">shouldNotReturnACashCardWhenUsingBadCredentials</span><span class="hljs-params">()</span> {
    ResponseEntity&lt;String&gt; response = restTemplate
      .withBasicAuth(<span class="hljs-string">&quot;BAD-USER&quot;</span>, <span class="hljs-string">&quot;abc123&quot;</span>)
      .getForEntity(<span class="hljs-string">&quot;/cashcards/99&quot;</span>, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    response = restTemplate
      .withBasicAuth(<span class="hljs-string">&quot;sarah1&quot;</span>, <span class="hljs-string">&quot;BAD-PASSWORD&quot;</span>)
      .getForEntity(<span class="hljs-string">&quot;/cashcards/99&quot;</span>, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
}
</code></pre>
<p>This test should pass...</p>
<pre><code class="hljs language-shell">...
CashCardApplicationTests &gt; shouldNotReturnACashCardWhenUsingBadCredentials() PASSED
</code></pre>
</li>
</ol>
<p>Success! Now that we&#39;ve implemented <em>authentication</em>, let&#39;s move on to implement <em>authorization</em> next.</p>
</div>




<div class="container-fluid main-content"><div class="row"><div class="col-sm-12"><section class="page-content"><h1 class="title">7: Support Authorization</h1><div class="rendered-content"><p>As we learned in the accompanying lesson, Spring Security supports many forms of authorization.</p>
<p>Here we&#39;ll implement Role-Based Access Control (RBAC).</p>
<p>It is likely that a user service will provide access to many authenticated users, but only &quot;card owners&quot; should be allowed to access Family Cash Cards managed by our application. Let&#39;s make those updates now.</p>
<ol>
<li><p>Add a users and roles to the <code>UserDetailsService</code> Bean.</p>
<p>To test authorization, we need multiple test users with a variety of roles.</p>
<p>Update <code>SecurityConfig.testOnlyUsers</code> and add the <code>CARD-OWNER</code> role to <code>sarah1</code>.</p>
<p>Also, let&#39;s add a new user named &quot;hank-owns-no-cards&quot; with a role of <code>NON-OWNER</code>.</p>
<pre><code class="hljs language-java">...
<span class="hljs-meta">@Bean</span>
<span class="hljs-keyword">public</span> UserDetailsService <span class="hljs-title function_">testOnlyUsers</span><span class="hljs-params">(PasswordEncoder passwordEncoder)</span> {
  User.<span class="hljs-type">UserBuilder</span> <span class="hljs-variable">users</span> <span class="hljs-operator">=</span> User.builder();
  <span class="hljs-type">UserDetails</span> <span class="hljs-variable">sarah</span> <span class="hljs-operator">=</span> users
    .username(<span class="hljs-string">&quot;sarah1&quot;</span>)
    .password(passwordEncoder.encode(<span class="hljs-string">&quot;abc123&quot;</span>))
    .roles(<span class="hljs-string">&quot;CARD-OWNER&quot;</span>) <span class="hljs-comment">// new role</span>
    .build();
  <span class="hljs-type">UserDetails</span> <span class="hljs-variable">hankOwnsNoCards</span> <span class="hljs-operator">=</span> users
    .username(<span class="hljs-string">&quot;hank-owns-no-cards&quot;</span>)
    .password(passwordEncoder.encode(<span class="hljs-string">&quot;qrs456&quot;</span>))
    .roles(<span class="hljs-string">&quot;NON-OWNER&quot;</span>) <span class="hljs-comment">// new role</span>
    .build();
  <span class="hljs-keyword">return</span> <span class="hljs-keyword">new</span> <span class="hljs-title class_">InMemoryUserDetailsManager</span>(sarah, hankOwnsNoCards);
}
</code></pre>
</li>
<li><p>Test for Role verification.</p>
<p>Let&#39;s add a test that will fail at first, but will pass when we fully implement authorization.</p>
<p>Here we&#39;ll assert that user &quot;hank-owns-no-cards&quot; should not have access to a <code>CashCard</code> since that user is not a <code>CARD-OWNER</code>.</p>
<pre><code class="hljs language-java"><span class="hljs-meta">@Test</span>
<span class="hljs-keyword">void</span> <span class="hljs-title function_">shouldRejectUsersWhoAreNotCardOwners</span><span class="hljs-params">()</span> {
    ResponseEntity&lt;String&gt; response = restTemplate
      .withBasicAuth(<span class="hljs-string">&quot;hank-owns-no-cards&quot;</span>, <span class="hljs-string">&quot;qrs456&quot;</span>)
      .getForEntity(<span class="hljs-string">&quot;/cashcards/99&quot;</span>, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
}
</code></pre>
<p><strong><em>But wait!</em></strong> <code>CashCard</code> with ID <code>99</code> belongs to <code>sarah1</code>, right? Shouldn&#39;t only <code>sarah1</code> have access to that data regardless of role?</p>
<p>You&#39;re right! Keep that in mind for later in this lab.</p>
</li>
<li><p>Run the tests.</p>
<p>We see that our new test fails when we run it.</p>
<pre><code class="hljs language-shell">CashCardApplicationTests &gt; shouldRejectUsersWhoAreNotCardOwners() FAILED
 org.opentest4j.AssertionFailedError:
 expected: 403 FORBIDDEN
  but was: 200 OK
</code></pre>
<p>Why was <code>hank-owns-no-cards</code> able to access a <code>CashCard</code> as indicated by the <code>200 OK</code> response?</p>
<p>Although we have given the test users roles, we are <em>not enforcing</em> role-based security.</p>
</li>
<li><p>Enable role-based security.</p>
<p>Edit <code>SecurityConfig.filterChain</code> to restrict access to only users with the <code>CARD-OWNER</code> role.</p>
<pre><code class="hljs language-java"><span class="hljs-meta">@Bean</span>
<span class="hljs-keyword">public</span> SecurityFilterChain <span class="hljs-title function_">filterChain</span><span class="hljs-params">(HttpSecurity http)</span> <span class="hljs-keyword">throws</span> Exception {
  http.authorizeHttpRequests()
    .requestMatchers(<span class="hljs-string">&quot;/cashcards/**&quot;</span>)
    .hasRole(<span class="hljs-string">&quot;CARD-OWNER&quot;</span>) <span class="hljs-comment">// enable RBAC: Replace the .authenticated() code with this line.</span>
    .and()
    .csrf().disable()
    .httpBasic();
  <span class="hljs-keyword">return</span> http.build();
}
</code></pre>
</li>
<li><p>Run the tests.</p>
<p>We see that our tests pass!</p>
<pre><code class="hljs language-shell">CashCardApplicationTests &gt; shouldRejectUsersWhoAreNotCardOwners() PASSED
</code></pre>
</li>
</ol>
<p>We&#39;ve now successfully enabled RBAC-based authorization!</p>
</div>
