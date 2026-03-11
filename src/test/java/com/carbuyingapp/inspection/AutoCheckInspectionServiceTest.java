package com.carbuyingapp.inspection;

import com.carbuyingapp.model.entity.CustomerRequest;
import com.carbuyingapp.model.entity.SupplierOffer;
import com.carbuyingapp.model.enums.InspectionCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AutoCheckInspectionServiceTest {

    private AutoCheckInspectionService service;

    @BeforeEach
    void setUp() {
        service = new AutoCheckInspectionService();
    }

    @Test
    void getCompany_shouldReturnAutoCheckCo() {
        assertThat(service.getCompany()).isEqualTo(InspectionCompany.AUTO_CHECK_CO);
    }

    @Test
    void inspect_localVehicleBelowPremium_shouldReturnBaseScore() {
        SupplierOffer offer = buildOffer(new BigDecimal("30000"), false);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(70);
        assertThat(result.getSummary()).contains("passes minimum threshold");
    }

    @Test
    void inspect_importedVehicle_shouldApplyPenalty() {
        SupplierOffer offer = buildOffer(new BigDecimal("30000"), true);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(55);
        assertThat(result.getSummary()).contains("below minimum threshold");
    }

    @Test
    void inspect_premiumPricedVehicle_shouldAddBonus() {
        SupplierOffer offer = buildOffer(new BigDecimal("60000"), false);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(80);
    }

    @Test
    void inspect_importedPremiumVehicle_shouldApplyBothModifiers() {
        SupplierOffer offer = buildOffer(new BigDecimal("60000"), true);

        InspectionResult result = service.inspect(offer);

        assertThat(result.getScore()).isEqualTo(65);
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
