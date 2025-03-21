package top.haidong556.metric.domain.model.machineMonitorAggregate;

import lombok.Getter;

@Getter
public class MachineIdentification {
    private final String machineIdentification;
    public MachineIdentification(String machineIdentification) {
        this.machineIdentification = machineIdentification;
    }
}
