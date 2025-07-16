package org.ufpi.pasid.projects.monitor;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class ProducerWithReplyTo {

    private static final String HOST = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
    private static final String USER = System.getenv().getOrDefault("RABBITMQ_USER", "guest");
    private static final String PASS = System.getenv().getOrDefault("RABBITMQ_PASS", "guest");

    private static final Gson gson = new Gson();
    public final Map<String, BatchResult> batchResults = new ConcurrentHashMap<>();

    public void executeBatchAsync(List<ExecutionRequest> batch, String batchId, String queueName) {
        long start = System.currentTimeMillis();
        new Thread(() -> {
            Map<ExecutionRequest, ExecutionResponse> responses = sendRequestsAndAwait(batch, queueName);
            long duration = System.currentTimeMillis() - start;
            BatchResult result = new BatchResult(batch, responses, duration);
            batchResults.put(batchId, result);
            System.out.println("[✓] Batch " + batchId + " concluído e armazenado.");
        }).start();
    }

    public Map<ExecutionRequest, ExecutionResponse> sendRequestAndAwait(ExecutionRequest request, String queueName) {
        return sendRequestsAndAwait(Collections.singletonList(request), queueName);
    }

    public Map<ExecutionRequest, ExecutionResponse> sendRequestsAndAwait(List<ExecutionRequest> requests, String queueName) {
        Map<ExecutionRequest, ExecutionResponse> requestToResponse = new ConcurrentHashMap<>();

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setUsername(USER);
            factory.setPassword(PASS);

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(queueName, true, false, false, null);
                String replyQueue = channel.queueDeclare().getQueue();

                Map<String, ExecutionRequest> correlationToRequest = new ConcurrentHashMap<>();
                CountDownLatch latch = new CountDownLatch(requests.size());

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String correlationId = delivery.getProperties().getCorrelationId();
                    if (correlationToRequest.containsKey(correlationId)) {
                        String response = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        Gson gson = new Gson();
                        ExecutionResponse executionResponse = gson.fromJson(response, ExecutionResponse.class);
                        ExecutionRequest req = correlationToRequest.get(correlationId);
                        requestToResponse.put(req, executionResponse);
                        latch.countDown();
                    }
                };

                channel.basicConsume(replyQueue, true, deliverCallback, consumerTag -> {
                });

                for (ExecutionRequest request : requests) {
                    String correlationId = UUID.randomUUID().toString();

                    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                            .replyTo(replyQueue)
                            .correlationId(correlationId)
                            .build();

                    String message = gson.toJson(request);
                    channel.basicPublish("", queueName, props, message.getBytes(StandardCharsets.UTF_8));
                    correlationToRequest.put(correlationId, request);
                    System.out.println("[✓] Mensagem publicada para modelo " + request.getModel());
                }

                latch.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestToResponse;
    }

    public static class BatchResult {
        public final List<ExecutionRequest> requests;
        public final Map<ExecutionRequest, ExecutionResponse> responses;
        public final long durationMs;

        public BatchResult(List<ExecutionRequest> requests, Map<ExecutionRequest, ExecutionResponse> responses, long durationMs) {
            this.requests = requests;
            this.responses = responses;
            this.durationMs = durationMs;
        }
    }
}
