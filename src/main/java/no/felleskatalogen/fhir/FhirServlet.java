package no.felleskatalogen.fhir;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.ApplicationContext;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import no.felleskatalogen.fhir.provider.BinaryProvider;
import no.felleskatalogen.fhir.provider.ManufacturedItemDefinitionProvider;

@WebServlet("/*")
public class FhirServlet extends RestfulServer {

    private static final long serialVersionUID = 7307348571081254800L;

    private ApplicationContext applicationContext;

    FhirServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        setFhirContext(FhirContext.forR5());
        setResourceProviders(Arrays.asList(
                applicationContext.getBean(BinaryProvider.class),
                applicationContext.getBean(ManufacturedItemDefinitionProvider.class)));
        }

}
