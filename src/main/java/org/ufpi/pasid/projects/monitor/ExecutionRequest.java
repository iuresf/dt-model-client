package org.ufpi.pasid.projects.monitor;


import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

public class ExecutionRequest {



    public enum ExecutionType {
        STATIONARY_ANALYSIS,
        STATIONARY_SIMULATION,
        TRANSIENT_SIMULATION
    }

    public enum Engine {
        MERCURY,
        NEW_ENGINE
    }

    public enum ModelType {
        RBD, SPN, CTMC, SCRIPT, QUEUEING_NETWORK
    }
    private String configId;
    private Engine engine = Engine.MERCURY;
    private String model; // nome do modelo
    private JsonElement modelJson; // modelo puro embutido para a nova engine
    private JsonElement engineRequest; // request nativo da nova engine
    private Map<String, Double> variables; // conjunto nome/valor
    private double error; // nível de erro (ex: 0.001)
    private ExecutionType executionType; // tipo de execução
    private ModelType modelType; // tipo de modelo
    private List<String> metrics; // métricas a calcular
    private long maxExecutionTimeInMinutes;
    private Double confidenceLevel;
    private Double maxAbsoluteError;
    private Double simulationTime;
    private Integer samplingPoints;
    private Integer repetitions;
    private Integer minSamples;
    private Integer maxSamples;
    private Double warmupTime;
    private Integer maxEvents;
    private Long seed;
    private Integer parallelism;
    private Long timeoutMs;
    private Boolean debug;

    public ExecutionRequest() {
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public JsonElement getModelJson() {
        return modelJson;
    }

    public void setModelJson(JsonElement modelJson) {
        this.modelJson = modelJson;
    }

    public JsonElement getEngineRequest() {
        return engineRequest;
    }

    public void setEngineRequest(JsonElement engineRequest) {
        this.engineRequest = engineRequest;
    }

    public Map<String, Double> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Double> variables) {
        this.variables = variables;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public long getMaxExecutionTimeInMinutes() {
        return maxExecutionTimeInMinutes;
    }

    public void setMaxExecutionTimeInMinutes(long maxExecutionTimeInMinutes) {
        this.maxExecutionTimeInMinutes = maxExecutionTimeInMinutes;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public Double getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public Double getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    public void setMaxAbsoluteError(Double maxAbsoluteError) {
        this.maxAbsoluteError = maxAbsoluteError;
    }

    public Double getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(Double simulationTime) {
        this.simulationTime = simulationTime;
    }

    public Integer getSamplingPoints() {
        return samplingPoints;
    }

    public void setSamplingPoints(Integer samplingPoints) {
        this.samplingPoints = samplingPoints;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public Integer getMinSamples() {
        return minSamples;
    }

    public void setMinSamples(Integer minSamples) {
        this.minSamples = minSamples;
    }

    public Integer getMaxSamples() {
        return maxSamples;
    }

    public void setMaxSamples(Integer maxSamples) {
        this.maxSamples = maxSamples;
    }

    public Double getWarmupTime() {
        return warmupTime;
    }

    public void setWarmupTime(Double warmupTime) {
        this.warmupTime = warmupTime;
    }

    public Integer getMaxEvents() {
        return maxEvents;
    }

    public void setMaxEvents(Integer maxEvents) {
        this.maxEvents = maxEvents;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }
}
