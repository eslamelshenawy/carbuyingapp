package com.carbuyingapp.inspection;

import com.carbuyingapp.model.entity.SupplierOffer;
import com.carbuyingapp.model.enums.InspectionCompany;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * VehiVerify Inc. inspection service.
 * Applies a different scoring model with higher base confidence for local vehicles
 * and a price-based bonus for competitively priced offers.
 */
@Service
public class VehiVerifyInspectionService implements InspectionService {

    private static final int BASE_SCORE = 75;
    private static final int IMPORT_PENALTY = 10;
    private static final BigDecimal LOW_PRICE_THRESHOLD = new BigDecimal("20000");

    @Override
    public InspectionResult inspect(SupplierOffer offer) {
        int score = BASE_SCORE;

        if (offer.isImported()) {
            score -= IMPORT_PENALTY;
        }

        if (offer.getPrice().compareTo(LOW_PRICE_THRESHOLD) <= 0) {
            score += 5;
        }

        score = Math.max(0, Math.min(100, score));

        String summary = String.format(
                "VehiVerify inspection complete. Score: %d. Recommendation: %s.",
                score,
                score >= 65 ? "Approved" : "Further review needed"
        );

        return new InspectionResult(score, summary);
    }

    @Override
    public InspectionCompany getCompany() {
        return InspectionCompany.VEHI_VERIFY_INC;
    }
}
