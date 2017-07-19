package com.mesaeva.viktorines.security;

import com.mesaeva.viktorines.template.Pages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig {

    private static final String ALL_USERS = "hasRole('ROLE_STUDENT') || hasRole('ROLE_ADMIN') || hasRole('ROLE_TEACHER')";
    private static final String COOL_USERS = "hasRole('ROLE_ADMIN') || hasRole('ROLE_TEACHER')";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        DataSource dataSource = (DataSource) envCtx.lookup("jdbc/myDB");
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT login, hashpass, enabled FROM users WHERE login = ?")
                .authoritiesByUsernameQuery("SELECT login, roles.authority FROM users INNER JOIN roles ON" +
                        " users.role_id = roles.id WHERE login = ?");
    }

    @Configuration
    public static class SecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            CharacterEncodingFilter filter = new CharacterEncodingFilter();
            filter.setEncoding("UTF-8");
            filter.setForceEncoding(true);
            // @formatter:off
            http
                    .addFilterBefore(filter, CsrfFilter.class)
                    .authorizeRequests()
                    .antMatchers(Pages.HOME).access(ALL_USERS)
                    .antMatchers(Pages.VIKTORINES).access(ALL_USERS)
                    .antMatchers(Pages.VIKTORINE).access(ALL_USERS)
                    .antMatchers(Pages.USER_REGISTRATION).access(COOL_USERS)
                    .antMatchers(Pages.CREATE_VIKTORINE).access(COOL_USERS)
                    .antMatchers(Pages.EDIT_VIKTORINE).access(COOL_USERS)
                    .antMatchers(Pages.USERS_MANAGEMENT).access(COOL_USERS)
                    .antMatchers(Pages.EDIT_USER).access(COOL_USERS)
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage(Pages.LOGIN)
                    .defaultSuccessUrl(Pages.HOME)
                    .and()
                    .logout()
                    .logoutUrl(Pages.LOGOUT)
                    .logoutSuccessUrl(Pages.LOGIN)
                    .and()
                    .csrf();
            // @formatter:on
        }
    }
}
