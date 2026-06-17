package com.focusroot.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateGroupRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    private Integer penaltyCoins;
}
