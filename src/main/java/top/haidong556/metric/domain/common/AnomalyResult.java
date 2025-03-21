package top.haidong556.metric.domain.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class AnomalyResult {
    private String machineId;
    private boolean hasAnomaly;
    private String message;
    private List<String> anomalyDetails;
    public AnomalyResult(String machineId) {
        this.machineId = machineId;
    }
    public void addAnomalyDetail(String detail) {
        if(anomalyDetails == null) {
            anomalyDetails = new ArrayList<>();
        }
        anomalyDetails.add(detail);
    }
}
