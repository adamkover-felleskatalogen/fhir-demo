package no.felleskatalogen.fhir;

import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;

@Configuration
public class FhirConfiguration {

    @Autowired
    private ApplicationContext context;

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR5();
    }

    @Bean
    public ServletRegistrationBean<HttpServlet> ServletRegistrationBean() {
        ServletRegistrationBean<HttpServlet> registration = new ServletRegistrationBean<>(new FhirServlet(context),"/*");
        registration.setName("FhirServlet");
        return registration;
    }

}
