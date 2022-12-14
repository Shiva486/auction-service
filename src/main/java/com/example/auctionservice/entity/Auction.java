package com.example.auctionservice.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity(name = "auction")
@Table(name = "auction", indexes = {
        @Index(name = "name_idx", columnList = "name"),
        @Index(name = "minimum_bid_value_idx", columnList = "minimum_bid_value"),
        @Index(name = "seller_id_idx", columnList = "seller_id"),
        @Index(name = "start_date_idx", columnList = "start_date"),
        @Index(name = "is_active_idx", columnList = "is_active")
})
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "minimum_bid_value", nullable = false)
    private Integer minimumBidValue;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "current_bid_value")
    private Integer currentBidValue;

    @Column(name = "current_bidder_id")
    private Long currentBidderId;

    @Column(name = "number_of_times_extended", nullable = false)
    private Integer numberOfTimesExtended = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
