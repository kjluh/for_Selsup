package org.example;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CrptApi {

    private final RateLimiter rateLimiter;
    private final int interval;

    public CrptApi(TimeUnit interval, int requestsLimit) {
        this.interval = (int) SECONDS.convert(1, interval);
        if (requestsLimit > 0) {
            rateLimiter = RateLimiter.create(requestsLimit);
        } else {
            throw new IllegalArgumentException(" передано неправильное значение лимита операций");
        }
    }

    @Data
    public class Document {
        private Description description;
        private String docId;
        private String docStatus;
        private String docType;
        private boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private String productionDate;
        private String productionType;
        private Products products;
        private String regDate;
        private String regNumber;

        public Document() {
        }

        public Document(Description description, String docId, String docStatus, String docType, boolean importRequest,
                        String ownerInn, String participantInn, String producerInn, String productionDate,
                        String productionType, Products products, String regDate, String regNumber) {
            this.description = description;
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.products = products;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }
    }

    @Data
    public class Products {
        private final String certificateDocument;
        private final String certificateDocumentDate;
        private final String certificateDocumentNumber;
        private final String ownerInn;
        private final String producerInn;
        private final String productionDate;
        private final String tnvedCode;
        private final String uitCode;
        private final String uituCode;

        public Products(String certificateDocument, String certificateDocumentDate, String certificateDocumentNumber,
                        String ownerInn, String producerInn, String productionDate, String tnvedCode, String uitCode,
                        String uituCode) {
            this.certificateDocument = certificateDocument;
            this.certificateDocumentDate = certificateDocumentDate;
            this.certificateDocumentNumber = certificateDocumentNumber;
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
            this.uitCode = uitCode;
            this.uituCode = uituCode;
        }
    }

    @Data
    public class Description {
        private final String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }
    }

    public HttpResponse createDocument(Document document, String signature) throws InterruptedException {
        try {
            rateLimiter.acquire(interval);
            Thread.sleep(3000);
            String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
            String json = document.toString() + signature;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
