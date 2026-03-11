package com.carbuyingapp.inspection;

import com.carbuyingapp.model.entity.CustomerRequest;
import com.carbuyingapp.model.entity.SupplierOffer;
import com.carbuyingapp.model.enums.InspectionCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VehiVerifyInspectionServiceTest {

    private VehiVerifyInspectionService service;

    @BeforeEach
    void setUp() {
        service = new VehiVerifyInspectionService();
    }

    @Test
    void getCompany_shouldReturnVehiVerifyInc() {
        assertThat(service.getCompany()).isEqualTo(InspectionCompany.VEHI_VERIFY_INC);
    }

    @Test
    void inspect_localVehicleAboveLowPrice_shouldReturnBaseScore() {
        SupplierOffer offer = buildOffer(new BigDecimal("30000"), false);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(75);
        assertThat(result.getSummary()).contains("Approved");
    }

    @Test
    void inspect_importedVehicle_shouldApplyPenalty() {
        SupplierOffer offer = buildOffer(new BigDecimal("30000"), true);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(65);
        assertThat(result.getSummary()).contains("Approved");
    }

    @Test
    void inspect_lowPricedLocalVehicle_shouldAddBonus() {
        SupplierOffer offer = buildOffer(new BigDecimal("15000"), false);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(80);
    }

    @Test
    void inspect_lowPricedImportedVehicle_shouldApplyBothModifiers() {
        SupplierOffer offer = buildOffer(new BigDecimal("15000"), true);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(70);
    }

    private SupplierOffer buildOffer(BigDecimal price, boolean imported) {
        return SupplierOffer.builder()
                .id(1L)
                .supplierId(10L)
                .request(CustomerRequest.builder().id(1L).build())
                .carDetails("Test vehicle")
                .price(price)
                .imported(imported)
                .build();
    }
}
