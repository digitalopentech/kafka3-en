package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var myService = new ReadingReportService();
        try (var service = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "ECOMMERCE_USER_GENERATE_READING_REPORT",
                myService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws CommonKafkaException {
        var message = record.value();
        var user = message.getPayload();

        System.out.println("-----------------------------------");
        System.out.println("Processing report for + " + user);

        var target = new File(user.getReportPath());
        try {
            IO.copy(SOURCE, target);
            IO.append(target, "Created for " + user.getUuid());
            System.out.println("File created: "+ target.getAbsolutePath());
        } catch (IOException e) {
            throw new CommonKafkaException(e);
        }

    }

}







