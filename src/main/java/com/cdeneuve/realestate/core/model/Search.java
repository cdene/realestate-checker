package com.cdeneuve.realestate.core.model;

import lombok.*;

import java.util.Collection;

@Builder
@ToString
@Getter
public class Search {
    @Singular
    private final Collection<Filter> filters;
}
