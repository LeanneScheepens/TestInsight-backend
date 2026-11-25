package nl.testinsight.backend.common.dto;

import java.time.LocalDate;

public record TestStatusTrendDto (
        String status,
        long count,
        LocalDate executedAt

){

}
