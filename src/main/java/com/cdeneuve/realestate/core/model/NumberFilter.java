package com.cdeneuve.realestate.core.model;

import lombok.*;

import java.math.BigDecimal;

@Builder
@ToString
@RequiredArgsConstructor
public class NumberFilter implements Filter {
    private final String filterName;
    private final BigDecimal from;
    private final BigDecimal to;

    @Override
    public String getValue() {
        return "" + filterName + "=" + (from != null ? from : "") + "-" + (to != null ? to : "");
    }
}
