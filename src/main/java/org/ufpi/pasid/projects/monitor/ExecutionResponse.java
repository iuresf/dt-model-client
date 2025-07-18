package org.ufpi.pasid.projects.monitor;

import java.util.HashMap;

public class ExecutionResponse {
    private String configId;
    private HashMap <String,Double> metricValues;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public HashMap<String, Double> getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(HashMap<String, Double> metricValues) {
        this.metricValues = metricValues;
    }
}
