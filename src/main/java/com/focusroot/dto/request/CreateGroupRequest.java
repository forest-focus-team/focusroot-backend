package com.focusroot.dto.request;

import lombok.Data;

@Data
public class CreateGroupRequest {
    private String name;
    private String ownerId;
}
