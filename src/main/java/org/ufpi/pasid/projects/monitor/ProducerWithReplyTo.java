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
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_PORT", "5672"));

    // Configs extras (com defaults seguros)
    private static final int HEARTBEAT_SEC = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_HEARTBEAT", "30"));
    private static final int NETWORK_RECOVERY_MS = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_NET_RECOVERY_MS", "5000"));
    private static final int CONNECTION_TIMEOUT_MS = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_CONN_TIMEOUT_MS", "30000"));
    private static final int HANDSHAKE_TIMEOUT_MS = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_HANDSHAKE_TIMEOUT_MS", "30000"));
    private static final int PREFETCH = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_PREFETCH", "20"));
    private static final int AWAIT_TIMEOUT_SEC = Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_AWAIT_TIMEOUT_SEC", "120"));

    private static final Gson gson = new Gson();
    public final Map<String, BatchResult> batchResults = new ConcurrentHashMap<>();

    /* --------- Fábrica com auto-recovery e heartbeats --------- */
    private ConnectionFactory buildFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USER);
        factory.setPassword(PASS);

        // Detecta falhas mais rápido e reconecta automaticamente
        factory.setRequestedHeartbeat(HEARTBEAT_SEC);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(NETWORK_RECOVERY_MS);

        // Timeouts mais folgados para redes instáveis
        factory.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
        factory.setHandshakeTimeout(HANDSHAKE_TIMEOUT_MS);
        factory.setShutdownTimeout(0); // não bloquear no shutdown

        return factory;
    }

    public void executeBatchAsync(List<ExecutionRequest> batch, String batchId, String queueName) {
        long start = System.currentTimeMillis();
        new Thread(() -> {
            Map<ExecutionRequest, ExecutionResponse> responses = sendRequestsAndAwait(batch, queueName);
            long duration = System.currentTimeMillis() - start;
            BatchResult result = new BatchResult(batch, responses, duration);
            batchResults.put(batchId, result);
            System.out.println("[✓] Batch " + batchId + " concluído e armazenado.");
        }, "producer-batch-" + batchId).start();
    }

    public Map<ExecutionRequest, ExecutionResponse> sendRequestAndAwait(ExecutionRequest request, String queueName) {
        return sendRequestsAndAwait(Collections.singletonList(request), queueName);
    }

    public Map<ExecutionRequest, ExecutionResponse> sendRequestsAndAwait(List<ExecutionRequest> requests, String queueName) {
        Map<ExecutionRequest, ExecutionResponse> requestToResponse = new ConcurrentHashMap<>();

        try {
            ConnectionFactory factory = buildFactory();

            try (Connection connection = factory.newConnection("producer-with-replyto");
                 Channel channel = connection.createChannel()) {

                // Logs úteis de ciclo de vida
                connection.addShutdownListener(cause ->
                        System.out.println("[RabbitMQ][CONN][SHUTDOWN] " + cause)
                );
                channel.addShutdownListener(cause ->
                        System.out.println("[RabbitMQ][CHAN][SHUTDOWN] " + cause)
                );

                // Prefetch (opcional, ajuda a evitar explosão de entregas)
                channel.basicQos(PREFETCH);

                // Declara fila de requisição (durable)
                channel.queueDeclare(queueName, true, false, false, null);

                // Cria fila de resposta exclusiva e auto-delete (RPC pattern)
                // O nome é gerado pelo broker; o auto-recovery irá re-declarar após reconexão.
                String replyQueue = channel.queueDeclare("", false, true, true, null).getQueue();

                Map<String, ExecutionRequest> correlationToRequest = new ConcurrentHashMap<>();
                CountDownLatch latch = new CountDownLatch(requests.size());

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    try {
                        String correlationId = delivery.getProperties().getCorrelationId();
                        if (correlationId != null) {
                            ExecutionRequest req = correlationToRequest.remove(correlationId);
                            if (req != null) {
                                String response = new String(delivery.getBody(), StandardCharsets.UTF_8);
                                ExecutionResponse executionResponse = gson.fromJson(response, ExecutionResponse.class);
                                requestToResponse.put(req, executionResponse);
                                latch.countDown();
                            }
                        }
                    } catch (Throwable t) {
                        System.out.println("[RabbitMQ][DELIVER][ERROR] " + t);
                        t.printStackTrace();
                    }
                };

                String consumerTag = channel.basicConsume(replyQueue, true, deliverCallback, ct -> {});

                // Publica todas as mensagens
                for (ExecutionRequest req : requests) {
                    String correlationId = UUID.randomUUID().toString();

                    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                            .replyTo(replyQueue)
                            .correlationId(correlationId)
                            .build();

                    String message = gson.toJson(req);
                    channel.basicPublish("", queueName, props, message.getBytes(StandardCharsets.UTF_8));
                    correlationToRequest.put(correlationId, req);
                    System.out.println("[✓] Mensagem publicada para modelo " + req.getModel());
                }

                // Espera respostas com timeout para nunca travar a thread
                boolean allArrived = latch.await(AWAIT_TIMEOUT_SEC, TimeUnit.SECONDS);
                if (!allArrived) {
                    System.out.println("[!] Timeout aguardando respostas (" + AWAIT_TIMEOUT_SEC + "s). " +
                            "Prosseguindo com respostas parciais: " + requestToResponse.size() + "/" + requests.size());
                }

                // Cancela o consumer desta chamada (boa prática)
                try { channel.basicCancel(consumerTag); } catch (Exception ignore) {}

            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.out.println("[RabbitMQ][INTERRUPTED] " + ie);
        } catch (Exception e) {
            // Captura qualquer falha de conexão/IO para não derrubar chamador
            System.out.println("[RabbitMQ][ERROR] " + e);
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
