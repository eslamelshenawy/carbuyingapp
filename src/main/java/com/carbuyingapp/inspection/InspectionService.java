package com.carbuyingapp.inspection;

import com.carbuyingapp.model.entity.SupplierOffer;
import com.carbuyingapp.model.enums.InspectionCompany;

public interface InspectionService {

    InspectionResult inspect(SupplierOffer offer);

    InspectionCompany getCompany();
}
