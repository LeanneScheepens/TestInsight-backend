package nl.testinsight.backend.common.dto;

import nl.testinsight.backend.common.TestStatus;

public record TestStatusCountDto(
        String status,
        long count)
{ }
