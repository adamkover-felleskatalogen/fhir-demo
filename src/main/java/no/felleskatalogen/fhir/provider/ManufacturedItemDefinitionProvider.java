package no.felleskatalogen.fhir.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.ManufacturedItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class ManufacturedItemDefinitionProvider implements IResourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ManufacturedItemDefinitionProvider.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FhirContext fhirContext;

    private List<ManufacturedItemDefinition> items;

    private Map<String, String> mapping;

    @PostConstruct
    public void loadItems() {
        items = new ArrayList<>();
        items.add(loadItem("classpath:/felleskatalogen/manufactured-item-definition/fhir-r5-skilarence-30.xml"));
        items.add(loadItem("classpath:/felleskatalogen/manufactured-item-definition/fhir-r5-skilarence-120.xml"));

        mapping = new HashMap<>();
        mapping.put("500781", "1");
        mapping.put("112869", "2");
        mapping.put("454978", "2");
    }

    private ManufacturedItemDefinition loadItem(String resource) {
        ManufacturedItemDefinition item = null;
        IParser parser = fhirContext.newXmlParser();
        try (InputStream inputStream = applicationContext.getResource(resource).getInputStream()) {
            item = parser.parseResource(ManufacturedItemDefinition.class, inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return item;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return ManufacturedItemDefinition.class;
    }

    @Read
    public ManufacturedItemDefinition read(@IdParam IdType theId) {
        return items.stream()
                .filter(item -> item.getIdElement().equals(theId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Not Found"));
    }

    @Search
    public ManufacturedItemDefinition search(@RequiredParam(name = "varenr") String varenr) {
        if (mapping.containsKey(varenr)) {
            return items.stream()
                    .filter(item -> item.getIdElement().getIdPart().equals(mapping.get(varenr)))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        } else {
            throw new ResourceNotFoundException("Not Found");
        }
    }

}
