package com.techcoach.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transferring user account details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Hides any fields from the JSON response if their value is null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String email;
    private String role;
}
