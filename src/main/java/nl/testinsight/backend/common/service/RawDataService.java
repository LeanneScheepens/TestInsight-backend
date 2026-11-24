package nl.testinsight.backend.common.service;

import nl.testinsight.backend.common.entity.TestRawDataEntity;
import nl.testinsight.backend.common.repository.RawDataRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class RawDataService {
    private final RawDataRepository rawDataRepository;

    public RawDataService(RawDataRepository rawDataRepository) {
        this.rawDataRepository = rawDataRepository;
    }

    public TestRawDataEntity saveRawData(JsonNode report) {
        TestRawDataEntity entity = new TestRawDataEntity();
        entity.setSourcePayload(report);

        return rawDataRepository.save(entity);
    }
}
