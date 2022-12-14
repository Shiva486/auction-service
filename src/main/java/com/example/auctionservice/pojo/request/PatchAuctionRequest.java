package com.example.auctionservice.pojo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;

import java.util.Date;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatchAuctionRequest {

    @NotNull
    @Positive
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Auction id to patch update")
    private Long id;

    private String name;

    @Positive
    private Integer minimumBidValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "IST")
    @FutureOrPresent
    private Date startDate;
}
