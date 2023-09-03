package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    /*
    * Filter the requests
    * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers(CC_Properties.ROOT_URI+"/**")
                //.authenticated()
                .hasRole(CC_Properties.ROLE_CARD_OWNER)
                .and()
                .csrf().disable()
                .httpBasic();

        return http.build();
    }

    /*
    * Test only purpose user beans
    * */
    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder){
        User.UserBuilder users = User.builder();
        UserDetails sara = users
                .username(CC_Properties.USER_SARA)
                .password(passwordEncoder.encode("abc123"))
                .roles(CC_Properties.ROLE_CARD_OWNER)
                .build();

        UserDetails jim = users
                .username(CC_Properties.USER_NOT_ALLOWED)
                .password(passwordEncoder.encode("abc123"))
                .roles(CC_Properties.ROLE_CARD_NON_OWNER)
                .build();

        return new InMemoryUserDetailsManager(sara,jim);

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}