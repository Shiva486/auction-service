package com.example.auctionservice.mapper;

public interface AbstractMapper<E, V> {
    void entityToView(E e, V v);
}
