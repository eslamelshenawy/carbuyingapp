package com.carbuyingapp.inspection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InspectionResult {

    private final int score;
    private final String summary;
}
