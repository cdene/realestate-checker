package com.cdeneuve.realestate.core.model;

import lombok.*;

@ToString
@Builder
@RequiredArgsConstructor
public class TextFilter implements Filter{
    private final String filterName;
    private final String filterValue;


    @Override
    public String getValue() {
        return "" + filterName + "=" + filterValue;
    }
}
