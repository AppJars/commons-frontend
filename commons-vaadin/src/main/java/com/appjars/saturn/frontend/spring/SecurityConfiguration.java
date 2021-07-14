package com.appjars.saturn.frontend.spring;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {

  private static final String LOGOUT_URL = "/sso/logout";
  private static final String LOGOUT_SUCCESS_URL = "/";

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider =
        keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
		// Not using Spring CSRF here to be able to use plain HTML for the login page
    http.csrf().disable()
      .headers().frameOptions().sameOrigin().and()
      // Register our CustomRequestCache that saves unauthorized access attempts, so
      // the user is redirected after login.
      .requestCache().requestCache(new CustomRequestCache())
      // Restrict access to our application.
      .and().authorizeRequests()
      // Allow all flow internal requests.
      .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
      // Allow all requests by logged in users.
      .anyRequest().fullyAuthenticated()
      // Configure logout
      .and().logout().addLogoutHandler(keycloakLogoutHandler())
      .logoutUrl(LOGOUT_URL).permitAll().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
  }

  /**
   * Allows access to static resources, bypassing Spring security.
   */
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
        // Start page //
        //"/",
        // About page //
        "/about",

        // Vaadin Flow static resources //
        "/VAADIN/**",

        // the standard favicon URI
        "/favicon.ico",

        "/icons/**", "/images/**",

        "/connect/**",

        // the robots exclusion standard
        "/robots.txt",

        // web application manifest //
        "/manifest.webmanifest", "/sw.js", "/offline-page.html", "/sw-runtime-resources-precache.js",

        // (development mode) static resources //
        "/frontend/**",

        // (production mode) static resources //
        "/frontend-es5/**", "/frontend-es6/**");
  }

}
