package no.felleskatalogen.fhir.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Binary;
import org.hl7.fhir.r5.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class BinaryProvider implements IResourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(BinaryProvider.class);

    @Autowired
    private ApplicationContext applicationContext;

    private List<Binary> items; 

    private Map<String, String> mapping;

    @PostConstruct
    public void loadItems() {
        items = new ArrayList<>();
        items.add(loadItem("1", "classpath:/felleskatalogen/foto/skilarence-30.jpg"));
        items.add(loadItem("2", "classpath:/felleskatalogen/foto/skilarence-120.jpg"));

        mapping = new HashMap<>();
        mapping.put("500781", "1");
        mapping.put("112869", "2");
        mapping.put("454978", "2");
    }

    private Binary loadItem(String id, String resource) {
        Binary binary = null;
        try (InputStream inputStream = applicationContext.getResource(resource).getInputStream()) {
            binary = new Binary();
            binary.setId(id);
            byte[] bytes = new byte[inputStream.available()];
            bytes = inputStream.readAllBytes();
            binary.setData(bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return binary;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Binary.class;
    }

    @Read
    public Binary read(@IdParam IdType theId) {
        return items.stream()
                .filter(item -> item.getIdElement().getIdPart().equals(theId.getIdPart()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Not Found"));
    }

    @Search
    public Binary search(@RequiredParam(name = "varenr") String varenr) {
        if (mapping.containsKey(varenr)) {
            return items.stream()
                    .filter(item -> item.getIdElement().getIdPart().equals(mapping.get(varenr)))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        } else {
            throw new ResourceNotFoundException("Not Found");
        }
    }

}
