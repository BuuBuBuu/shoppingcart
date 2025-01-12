/**
 * @author ZENGXING
 * Generated by script
 */

//package com.shoppingcart.springboot.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//  @Bean
//  public CorsFilter corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    CorsConfiguration config = new CorsConfiguration();
//
//    // Allow credentials such as cookies, authorization headers, or TLS client certificates
//    config.setAllowCredentials(true);
//
//    // Allow requests from the React frontend running on localhost:3000
//    config.addAllowedOrigin("http://localhost:3000"); // React's default port
//
//    // Allow any headers (e.g., Authorization, Content-Type, etc.)
//    config.addAllowedHeader("*");
//
//    // Allow any HTTP methods (GET, POST, PUT, DELETE, etc.)
//    config.addAllowedMethod("*");
//
//    // Register this configuration for all paths
//    source.registerCorsConfiguration("/**", config);
//
//    // Return the CorsFilter with the configuration set
//    return new CorsFilter(source);
//  }
//}
//
