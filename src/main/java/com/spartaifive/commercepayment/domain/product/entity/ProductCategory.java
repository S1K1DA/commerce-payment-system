package com.spartaifive.commercepayment.domain.product.entity;

import lombok.Getter;

// TODO: 시간나면 카테고리 테이블을 따로 만들기
@Getter
public enum ProductCategory {
    ELECTORNICS("전자기기") ,
    FOOD("음식") ,
    TOY("장남감"),
    CLOTHES("의류");

    private final String categoryDescription;

    ProductCategory(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
}
