package com.campusnexus.dto;

import com.campusnexus.enums.EventStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventStatusRequest {
    @NotNull(message = "Status cannot be null")
    private EventStatus status;
}
