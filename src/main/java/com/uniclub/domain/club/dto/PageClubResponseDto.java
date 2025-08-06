package com.uniclub.domain.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PageClubResponseDto<T> {
    private final List<T> content;
    private final boolean hasNext;
}
