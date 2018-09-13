package com.developol.polchatex.configuration;

import com.developol.polchatex.persistence.UserDto;
import com.developol.polchatex.persistence.UserDtoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import java.util.Iterator;
import java.util.LinkedList;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDtoRepository userDtoRepository;

    public WebSecurityConfig(UserDtoRepository userDtoRepository) {
        this.userDtoRepository = userDtoRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                httpBasic().and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/rest/**").permitAll()
                .antMatchers(HttpMethod.GET, "/rest/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/rest/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/rest/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/rest/**").hasRole("USER")
                //.antMatchers("/socket/**").hasRole("USER")
                .and()
                .csrf()
                .disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inMemoryUserDetailsManager());
    }


    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        Iterable<UserDto> databaseUserList = userDtoRepository.findAll();
        LinkedList<UserDetails> securityUserList = new LinkedList<UserDetails>();
        Iterator i = databaseUserList.iterator();
        UserDto u;
        while (i.hasNext()) {
            u = (UserDto) i.next();
            securityUserList.add(
                    User.builder()
                            .username(u.getUsername())
                            .password(u.getPassword())
                            .roles("USER")
                            .build()
            );
        }
        return new InMemoryUserDetailsManager(securityUserList);
    }
}

