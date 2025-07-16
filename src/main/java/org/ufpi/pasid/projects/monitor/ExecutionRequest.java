package org.ufpi.pasid.projects.monitor;


import java.util.List;
import java.util.Map;

public class ExecutionRequest {



    public enum ExecutionType {
        STATIONARY_ANALYSIS,
        STATIONARY_SIMULATION,
        TRANSIENT_SIMULATION
    }

    public enum ModelType {
        RBD, SPN, CTMC, SCRIPT
    }
    private String configId;
    private String model; // nome do modelo
    private Map<String, Double> variables; // conjunto nome/valor
    private double error; // nível de erro (ex: 0.001)
    private ExecutionType executionType; // tipo de execução
    private ModelType modelType; // tipo de modelo
    private List<String> metrics; // métricas a calcular
    private long maxExecutionTimeInMinutes;

    public ExecutionRequest() {
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
}
