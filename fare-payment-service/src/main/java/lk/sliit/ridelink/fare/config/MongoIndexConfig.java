package lk.sliit.ridelink.fare.config;

import lk.sliit.ridelink.fare.model.Fare;
import lk.sliit.ridelink.fare.model.Payment;
import lk.sliit.ridelink.fare.model.Receipt;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

/**
 * MongoDB has no schema-enforced unique constraints like a relational DB,
 * so unique indexes are created explicitly at startup.
 * (Spring Boot 4 no longer creates @Indexed annotations' indexes automatically.)
 */
@Configuration
public class MongoIndexConfig {

    @Bean
    ApplicationRunner createMongoIndexes(MongoTemplate mongoTemplate) {
        return args -> {
            mongoTemplate.indexOps(Fare.class)
                    .ensureIndex(new Index().on("rideId", Sort.Direction.ASC).unique());

            mongoTemplate.indexOps(Payment.class)
                    .ensureIndex(new Index().on("transactionRef", Sort.Direction.ASC).unique().sparse());

            mongoTemplate.indexOps(Receipt.class)
                    .ensureIndex(new Index().on("receiptNumber", Sort.Direction.ASC).unique());
        };
    }
}
