package org.ufpi.pasid.projects.monitor;

import java.util.HashMap;
import java.util.List;

public class ExecutionResponse {
    private String configId;
    private String requestId;
    private String engine;
    private String modelType;
    private String executionType;
    private HashMap<String, Double> metricValues;
    private HashMap<String, MetricStatistics> statistics;
    private List<TimeSeriesPoint> timeSeries;
    private HashMap<String, Object> meta;
    private String error;
    private List<ExecutionErrorPayload> errors;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getExecutionType() {
        return executionType;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public HashMap<String, Double> getMetricValues() {
        return metricValues;
    }

    public void setMetricValues(HashMap<String, Double> metricValues) {
        this.metricValues = metricValues;
    }

    public HashMap<String, MetricStatistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(HashMap<String, MetricStatistics> statistics) {
        this.statistics = statistics;
    }

    public List<TimeSeriesPoint> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<TimeSeriesPoint> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public HashMap<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Object> meta) {
        this.meta = meta;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ExecutionErrorPayload> getErrors() {
        return errors;
    }

    public void setErrors(List<ExecutionErrorPayload> errors) {
        this.errors = errors;
    }

    public static class MetricStatistics {
        private Double mean;
        private Double min;
        private Double max;
        private Double standardDeviation;
        private Integer samples;
        private Double confidenceLower;
        private Double confidenceUpper;
        private Double halfWidth;

        public Double getMean() { return mean; }
        public void setMean(Double mean) { this.mean = mean; }
        public Double getMin() { return min; }
        public void setMin(Double min) { this.min = min; }
        public Double getMax() { return max; }
        public void setMax(Double max) { this.max = max; }
        public Double getStandardDeviation() { return standardDeviation; }
        public void setStandardDeviation(Double standardDeviation) { this.standardDeviation = standardDeviation; }
        public Integer getSamples() { return samples; }
        public void setSamples(Integer samples) { this.samples = samples; }
        public Double getConfidenceLower() { return confidenceLower; }
        public void setConfidenceLower(Double confidenceLower) { this.confidenceLower = confidenceLower; }
        public Double getConfidenceUpper() { return confidenceUpper; }
        public void setConfidenceUpper(Double confidenceUpper) { this.confidenceUpper = confidenceUpper; }
        public Double getHalfWidth() { return halfWidth; }
        public void setHalfWidth(Double halfWidth) { this.halfWidth = halfWidth; }
    }

    public static class TimeSeriesPoint {
        private Double time;
        private HashMap<String, Double> metricValues;

        public Double getTime() { return time; }
        public void setTime(Double time) { this.time = time; }
        public HashMap<String, Double> getMetricValues() { return metricValues; }
        public void setMetricValues(HashMap<String, Double> metricValues) { this.metricValues = metricValues; }
    }

    public static class ExecutionErrorPayload {
        private String code;
        private String message;
        private String componentType;
        private String componentName;
        private String field;
        private String expression;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getComponentType() { return componentType; }
        public void setComponentType(String componentType) { this.componentType = componentType; }
        public String getComponentName() { return componentName; }
        public void setComponentName(String componentName) { this.componentName = componentName; }
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
    }
}
