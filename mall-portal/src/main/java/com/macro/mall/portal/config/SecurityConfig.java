package com.macro.mall.portal.config;

import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.component.GoAccessDeniedHandler;
import com.macro.mall.portal.component.GoAuthenticationEntryPoint;
import com.macro.mall.portal.component.GoAuthenticationFailureHandler;
import com.macro.mall.portal.component.GoAuthenticationSuccessHandler;
import com.macro.mall.portal.component.GoLogoutSuccessHandler;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UmsMemberService memberService;
    public SecurityConfig(UmsMemberService memberService) { this.memberService = memberService; }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/favicon.ico",
                                "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/sso/*", "/home/**", "/member/**", "/returnApply/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(new GoAccessDeniedHandler())
                        .authenticationEntryPoint(new GoAuthenticationEntryPoint()))
                .formLogin(form -> form.loginPage("/sso/login").successHandler(new GoAuthenticationSuccessHandler())
                        .failureHandler(new GoAuthenticationFailureHandler()).permitAll())
                .logout(logout -> logout.logoutUrl("/sso/logout").logoutSuccessHandler(new GoLogoutSuccessHandler())
                        .invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService userDetailsService() {
        return username -> {
            UmsMember member = memberService.getByUsername(username);
            if (member != null) return new MemberDetails(member);
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }
    @Bean DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
