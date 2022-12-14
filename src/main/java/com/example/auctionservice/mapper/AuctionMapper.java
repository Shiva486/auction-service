package com.example.auctionservice.mapper;

import com.example.auctionservice.entity.Auction;
import com.example.auctionservice.pojo.view.AuctionView;
import com.example.auctionservice.utils.NullAwareBeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper implements AbstractMapper<Auction, AuctionView> {

    @Override
    public void entityToView(Auction auction, AuctionView auctionView) {
        if (auction != null)
            NullAwareBeanUtils.copyPropertiesWithoutNull(auction, auctionView);
    }
}
