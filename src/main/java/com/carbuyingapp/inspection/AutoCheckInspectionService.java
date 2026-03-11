package com.carbuyingapp.inspection;

import com.carbuyingapp.model.entity.SupplierOffer;
import com.carbuyingapp.model.enums.InspectionCompany;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * AutoCheck Co. inspection service.
 * Uses a scoring algorithm that factors in price range and import status.
 * Imported vehicles receive a stricter evaluation with a penalty applied.
 */
@Service
public class AutoCheckInspectionService implements InspectionService {

    private static final int BASE_SCORE = 70;
    private static final int IMPORT_PENALTY = 15;
    private static final BigDecimal PREMIUM_THRESHOLD = new BigDecimal("50000");

    @Override
    public InspectionResult inspect(SupplierOffer offer) {
        int score = BASE_SCORE;

        if (offer.isImported()) {
            score -= IMPORT_PENALTY;
        }

        if (offer.getPrice().compareTo(PREMIUM_THRESHOLD) > 0) {
            score += 10;
        }

        score = Math.max(0, Math.min(100, score));

        String summary = String.format(
                "AutoCheck inspection complete. Score: %d. Vehicle %s.",
                score,
                score >= 60 ? "passes minimum threshold" : "below minimum threshold"
        );

        return new InspectionResult(score, summary);
    }

    @Override
    public InspectionCompany getCompany() {
        return InspectionCompany.AUTO_CHECK_CO;
    }
}
