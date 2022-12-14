package com.example.auctionservice.repository;

import com.example.auctionservice.entity.Auction;
import com.querydsl.core.types.Predicate;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends CrudRepository<Auction, Long>, QuerydslPredicateExecutor<Auction> {

    @Override
    List<Auction> findAll(Predicate predicate);
}
