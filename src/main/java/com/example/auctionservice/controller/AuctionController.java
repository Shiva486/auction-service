package com.example.auctionservice.controller;

import com.example.auctionservice.pojo.request.CreateAuctionRequest;
import com.example.auctionservice.pojo.request.PatchAuctionRequest;
import com.example.auctionservice.pojo.request.PlaceBidRequest;
import com.example.auctionservice.pojo.response.Response;
import com.example.auctionservice.pojo.view.AuctionView;
import com.example.auctionservice.service.AuctionService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/auction", consumes = "application/json", produces = "application/json")
public class AuctionController extends AbstractController {

    private final AuctionService auctionService;

    @Autowired
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    @Operation(description = "Create an auction by a seller",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json")),
            responses = @ApiResponse(content = @Content(schema = @Schema(implementation = AuctionView.class))))
    public Response<?> createAuction(@RequestBody @Valid CreateAuctionRequest createAuctionRequest) {
        AuctionView auctionView = auctionService.createAuction(createAuctionRequest);
        return getSuccessResponse(auctionView);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @Timed
    @Operation(description = "Patch update an auction",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json")),
            responses = @ApiResponse(content = @Content(schema = @Schema(implementation = AuctionView.class))))
    public Response<?> patchUpdate(@RequestBody @Valid PatchAuctionRequest patchAuctionRequest) {
        AuctionView auctionView = auctionService.patchUpdateAuction(patchAuctionRequest);
        return getSuccessResponse(auctionView);
    }

    @RequestMapping(value = "/{id}/disable", method = RequestMethod.PUT)
    @Timed
    @Operation(description = "Disable an auction",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json")))
    public Response<?> disable(@PathVariable("id") Long id) {
        AuctionView auctionView = auctionService.revokeAuction(id);
        return getSuccessResponse(auctionView);
    }

    @RequestMapping(method = RequestMethod.GET)
    @Timed
    @Operation(description = "Get auctions by combination of filters",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json")),
            responses = @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuctionView.class)))))
    public Response<?> getByFilter(@RequestParam(value = "ids", required = false) List<Long> ids,
                                   @RequestParam(value = "names", required = false) List<String> names,
                                   @RequestParam(value = "sellerIds", required = false) List<Long> sellerIds,
                                   @RequestParam(value = "bidderIds", required = false) List<Long> bidderIds,
                                   @RequestParam(value = "startDateFrom", required = false) @DateTimeFormat(pattern="dd-MM-yyyy HH:mm:ss") Date startDateFrom,
                                   @RequestParam(value = "startDateTill", required = false) @DateTimeFormat(pattern="dd-MM-yyyy HH:mm:ss") Date startDateTill,
                                   @RequestParam(value = "endDateFrom", required = false) @DateTimeFormat(pattern="dd-MM-yyyy HH:mm:ss") Date endDateFrom,
                                   @RequestParam(value = "endDateTill", required = false) @DateTimeFormat(pattern="dd-MM-yyyy HH:mm:ss") Date endDateTill,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                   @RequestParam(value = "sortColumn", required = false) String sortColumn,
                                   @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        List<AuctionView> auctionViewList = auctionService.getAuctionsByFilter(ids, names, sellerIds, bidderIds,
                                                                               startDateFrom, startDateTill,
                                                                               endDateFrom, endDateTill,
                                                                               pageSize, pageNumber,
                                                                               sortColumn, sortOrder);
        return getSuccessResponse(auctionViewList);
    }

    @RequestMapping(value = "/{id}/placeBid", method = RequestMethod.POST)
    @Timed
    @Operation(description = "Place bid in an auction",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json")),
            responses = @ApiResponse(content = @Content(schema = @Schema(implementation = AuctionView.class))))
    public Response<?> placeBid(@PathVariable("id") Long id, @RequestBody @Valid PlaceBidRequest placeBidRequest) {
        AuctionView auctionView = auctionService.placeBid(id, placeBidRequest);
        return getSuccessResponse(auctionView);
    }

}
