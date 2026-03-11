package com.carbuyingapp.inspection;

import com.carbuyingapp.model.enums.InspectionCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InspectionServiceFactoryTest {

    private InspectionServiceFactory factory;

    @BeforeEach
    void setUp() {
        List<InspectionService> services = List.of(
                new AutoCheckInspectionService(),
                new VehiVerifyInspectionService()
        );
        factory = new InspectionServiceFactory(services);
    }

    @Test
    void getService_forAutoCheckCo_shouldReturnAutoCheckInspectionService() {
        InspectionService service = factory.getService(InspectionCompany.AUTO_CHECK_CO);

        assertThat(service).isInstanceOf(AutoCheckInspectionService.class);
        assertThat(service.getCompany()).isEqualTo(InspectionCompany.AUTO_CHECK_CO);
    }

    @Test
    void getService_forVehiVerifyInc_shouldReturnVehiVerifyInspectionService() {
        InspectionService service = factory.getService(InspectionCompany.VEHI_VERIFY_INC);

        assertThat(service).isInstanceOf(VehiVerifyInspectionService.class);
        assertThat(service.getCompany()).isEqualTo(InspectionCompany.VEHI_VERIFY_INC);
    }

    @Test
    void getService_withEmptyServiceList_shouldThrowForAnyCompany() {
        InspectionServiceFactory emptyFactory = new InspectionServiceFactory(List.of());

        assertThatThrownBy(() -> emptyFactory.getService(InspectionCompany.AUTO_CHECK_CO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AUTO_CHECK_CO");
    }
}
