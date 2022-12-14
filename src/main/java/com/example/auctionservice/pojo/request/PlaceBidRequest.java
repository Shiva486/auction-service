package com.example.auctionservice.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceBidRequest {

    @NotNull
    @Positive
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", exclusiveMinimum = true)
    private Long bidderId;

    @NotNull
    @Positive
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", exclusiveMinimum = true)
    private Integer bidValue;
}
