package com.nimfid.commons.response;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class PageResponse {

    private Object  pageContent;
    private int     currentPage;
    private int     totalPages;
    private Long    totalItems;
}
