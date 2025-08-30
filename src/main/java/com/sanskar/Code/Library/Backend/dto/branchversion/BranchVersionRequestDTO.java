package com.sanskar.Code.Library.Backend.dto.branchversion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchVersionRequestDTO {
    private String snippetId;
    private String branchId;
    private int version;
    private String message;

    private String description;
    private String language;
    private String code;
}
