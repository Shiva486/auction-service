package com.example.auctionservice.service;

import com.example.auctionservice.entity.Auction;
import com.example.auctionservice.entity.QAuction;
import com.example.auctionservice.exception.CustomException;
import com.example.auctionservice.exception.ErrorCodes;
import com.example.auctionservice.mapper.AuctionMapper;
import com.example.auctionservice.pojo.request.PatchAuctionRequest;
import com.example.auctionservice.pojo.request.PlaceBidRequest;
import com.example.auctionservice.pojo.view.AuctionView;
import com.example.auctionservice.pojo.request.CreateAuctionRequest;
import com.example.auctionservice.repository.AuctionRepository;
import com.example.auctionservice.utils.CommonFunctions;
import com.example.auctionservice.utils.NullAwareBeanUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository,
                          AuctionMapper auctionMapper) {
        this.auctionRepository = auctionRepository;
        this.auctionMapper = auctionMapper;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AuctionView createAuction(CreateAuctionRequest createAuctionRequest) {
        Auction auction = new Auction();
        NullAwareBeanUtils.copyProperties(createAuctionRequest, auction);
        auction.setEndDate(DateUtils.addHours(auction.getStartDate(), 1));

        auction = auctionRepository.save(auction);
        AuctionView auctionView = new AuctionView();
        auctionMapper.entityToView(auction, auctionView);

        return auctionView;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AuctionView patchUpdateAuction(PatchAuctionRequest patchAuctionRequest) {
        Optional<Auction> auctionOptional = auctionRepository.findById(patchAuctionRequest.getId());

        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            NullAwareBeanUtils.copyPropertiesWithoutNull(patchAuctionRequest, auction);
            auction.setEndDate(DateUtils.addHours(auction.getStartDate(), 1));

            auction = auctionRepository.save(auction);
            AuctionView auctionView = new AuctionView();
            auctionMapper.entityToView(auction, auctionView);

            return auctionView;
        }
        else
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AuctionView revokeAuction(Long id) {
        Optional<Auction> auctionOptional = auctionRepository.findById(id);

        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            auction.setIsActive(false);

            auction = auctionRepository.save(auction);
            AuctionView auctionView = new AuctionView();
            auctionMapper.entityToView(auction, auctionView);

            return auctionView;
        }
        else
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
    }

    public List<AuctionView> getAuctionsByFilter(List<Long> ids, List<String> names, List<Long> sellerIds, List<Long> bidderIds,
                                                 Date startDateFrom, Date startDateTill, Date endDateFrom, Date endDateTill,
                                                 Integer pageSize, Integer pageNumber, String sortColumn, String sortOrder) {
        BooleanExpression booleanExpression = this.getBooleanExpression(ids, names, sellerIds, bidderIds, startDateFrom, startDateTill, endDateFrom, endDateTill);
        Pageable pageable = CommonFunctions.getPageable(pageSize, pageNumber, sortColumn, sortOrder);

        List<Auction> auctionList = pageable == null ?
                auctionRepository.findAll(booleanExpression)
                : auctionRepository.findAll(booleanExpression, pageable).getContent();

        List<AuctionView> auctionViewList = new ArrayList<>();
        for (Auction auction: auctionList) {
            AuctionView auctionView = new AuctionView();
            auctionMapper.entityToView(auction, auctionView);
            auctionViewList.add(auctionView);
        }

        return auctionViewList;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AuctionView placeBid(Long id, PlaceBidRequest placeBidRequest) {
        Optional<Auction> auctionOptional = auctionRepository.findById(id);

        if (auctionOptional.isPresent()) {
            Date currentDate = new Date();
            Auction auction = auctionOptional.get();
            this.validateBid(placeBidRequest, auction, currentDate);

            auction.setCurrentBidderId(placeBidRequest.getBidderId());
            auction.setCurrentBidValue(placeBidRequest.getBidValue());

            this.validateAndExtendAuctionDuration(auction, currentDate);

            auction = auctionRepository.save(auction);
            AuctionView auctionView = new AuctionView();
            auctionMapper.entityToView(auction, auctionView);

            return auctionView;
        }
        else
            throw new CustomException(ErrorCodes.NO_ENTITY_FOUND);
    }

    private void validateBid(PlaceBidRequest placeBidRequest, Auction auction, Date currentDate) {
        if (!auction.getIsActive())
            throw new CustomException(ErrorCodes.AUCTION_NOT_ACTIVE);
        if (currentDate.before(auction.getStartDate()))
            throw new CustomException(ErrorCodes.AUCTION_NOT_YET_STARTED);
        if (currentDate.after(DateUtils.addHours(auction.getStartDate(), 1)))
            throw new CustomException(ErrorCodes.AUCTION_ENDED);

        if (placeBidRequest.getBidderId().equals(auction.getCurrentBidderId()))
            throw new CustomException(ErrorCodes.CURRENT_BIDDER_CANNOT_PLACE_ANOTHER_BID);
        if (placeBidRequest.getBidValue() <= auction.getMinimumBidValue())
            throw new CustomException(ErrorCodes.BID_LOWER_THAN_MIN_VALUE);
        if (auction.getCurrentBidValue() != null
                && placeBidRequest.getBidValue() <= this.getNextMinimumBidValue(auction.getCurrentBidValue()))
            throw new CustomException(ErrorCodes.BID_LOWER_THAN_NEXT_MIN_ALLOWED_BID);
    }

    private Integer getNextMinimumBidValue(Integer currentBidValue) {
        return (int) Math.ceil(currentBidValue * 1.02);
    }

    private void validateAndExtendAuctionDuration(Auction auction, Date currentDate) {
        long duration  = DateUtils.addHours(auction.getStartDate(), 1).getTime() - currentDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

        if (diffInMinutes <= 5L && auction.getNumberOfTimesExtended() < 100) {
            auction.setNumberOfTimesExtended(auction.getNumberOfTimesExtended()+1);
            auction.setEndDate(DateUtils.addMinutes(auction.getEndDate(), 10));
        }
    }

    private BooleanExpression getBooleanExpression(List<Long> ids, List<String> names, List<Long> sellerIds,
                                                   List<Long> bidderIds, Date startDateFrom, Date startDateTill,
                                                   Date endDateFrom, Date endDateTill) {
        QAuction qAuction = QAuction.auction;

        BooleanExpression booleanExpression = Expressions.asBoolean(true).isTrue();
        booleanExpression.and(qAuction.isActive.isTrue());

        if (!CollectionUtils.isEmpty(ids)) {
            booleanExpression = booleanExpression.and(qAuction.id.in(ids));
        }

        if (!CollectionUtils.isEmpty(names)) {
            List<BooleanExpression> nameBooleanExpressions = new ArrayList<>();
            for (String name: names) {
                nameBooleanExpressions.add(Expressions.asBoolean(true).isTrue()
                                                      .and(qAuction.name.equalsIgnoreCase(name)));
            }

            booleanExpression.andAnyOf(nameBooleanExpressions.toArray(new BooleanExpression[0]));
        }

        if (!CollectionUtils.isEmpty(sellerIds)) {
            booleanExpression = booleanExpression.and(qAuction.sellerId.in(sellerIds));
        }

        if (!CollectionUtils.isEmpty(bidderIds)) {
            booleanExpression = booleanExpression.and(qAuction.currentBidderId.in(bidderIds));
        }

        if (startDateFrom != null) {
            booleanExpression = booleanExpression.and(qAuction.startDate.goe(startDateFrom));
        }

        if (startDateTill != null) {
            booleanExpression = booleanExpression.and(qAuction.startDate.loe(startDateTill));
        }

        if (endDateFrom != null) {
            booleanExpression = booleanExpression.and(qAuction.endDate.goe(endDateFrom));
        }

        if (endDateTill != null) {
            booleanExpression = booleanExpression.and(qAuction.endDate.loe(endDateTill));
        }

        return booleanExpression;
    }
}
