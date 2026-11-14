package nl.testinsight.backend.processor;

import nl.testinsight.backend.dto.TestDataDto;
import nl.testinsight.backend.enums.SourceType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TestReportProcessor<T> {
    /**
     * Processes the test result report into the generic {@link TestDataDto}
     * @param report test result report
     * @return the collection of {@link TestDataDto}
     */
    List<TestDataDto> process(T report);
}
