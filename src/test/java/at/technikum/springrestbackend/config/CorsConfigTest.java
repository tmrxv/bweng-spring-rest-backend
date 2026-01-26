package at.technikum.springrestbackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CorsConfigTest {

    @Test
    void corsConfigurerBeanCreated() {
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer configurer = config.corsConfigurer();

        assertThat(configurer).isNotNull();
    }

    @Test
    void corsConfigurerAddsMappings() {
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer configurer = config.corsConfigurer();

        CorsRegistry registry = mock(CorsRegistry.class);
        org.springframework.web.servlet.config.annotation.CorsRegistration registration =
                mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(registry.addMapping(any())).thenReturn(registration);
        when(registration.allowedOrigins(any(String[].class))).thenReturn(registration);
        when(registration.allowedMethods(any(String[].class))).thenReturn(registration);
        when(registration.allowedHeaders(any(String[].class))).thenReturn(registration);
        when(registration.allowCredentials(anyBoolean())).thenReturn(registration);

        configurer.addCorsMappings(registry);

        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("http://localhost:5173");
        verify(registration).allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }

    @Test
    void addsResourceHandlerForUploads() {
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer configurer = config.corsConfigurer();

        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration registration =
                mock(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration.class);

        when(registry.addResourceHandler(any())).thenReturn(registration);
        when(registration.addResourceLocations(any(String[].class))).thenReturn(registration);

        configurer.addResourceHandlers(registry);

        verify(registry).addResourceHandler("/uploads/**");
        verify(registration).addResourceLocations("file:uploads/");
    }
}
