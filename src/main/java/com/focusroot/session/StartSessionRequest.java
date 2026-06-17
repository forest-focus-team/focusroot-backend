package com.focusroot.session;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionRequest {

    @NotNull
    @Min(5)
    @Max(180)
    private Integer plannedDuration;

    private Long treeSpeciesId;
}
