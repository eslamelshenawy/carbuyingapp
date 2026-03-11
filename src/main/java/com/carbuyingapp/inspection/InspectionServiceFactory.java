package com.carbuyingapp.inspection;

import com.carbuyingapp.model.enums.InspectionCompany;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class InspectionServiceFactory {

    private final Map<InspectionCompany, InspectionService> serviceMap;

    public InspectionServiceFactory(List<InspectionService> inspectionServices) {
        serviceMap = new EnumMap<>(InspectionCompany.class);
        for (InspectionService service : inspectionServices) {
            serviceMap.put(service.getCompany(), service);
        }
    }

    public InspectionService getService(InspectionCompany company) {
        InspectionService service = serviceMap.get(company);
        if (service == null) {
            throw new IllegalArgumentException(
                    "No inspection service registered for company: " + company);
        }
        return service;
    }
}
