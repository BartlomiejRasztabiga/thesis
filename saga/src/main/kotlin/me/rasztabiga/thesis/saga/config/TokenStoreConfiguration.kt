package me.rasztabiga.thesis.saga.config

import com.mongodb.client.MongoClient
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.extensions.mongo.DefaultMongoTemplate
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore
import org.axonframework.modelling.saga.repository.SagaStore
import org.axonframework.serialization.Serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenStoreConfiguration {

    @Bean
    fun tokenStore(client: MongoClient, serializer: Serializer): TokenStore {
        return MongoTokenStore
            .builder()
            .mongoTemplate(
                DefaultMongoTemplate
                    .builder()
                    .mongoDatabase(client)
                    .build()
            )
            .serializer(serializer)
            .build()
    }


    @Bean
    fun sagaStore(client: MongoClient, serializer: Serializer): SagaStore<Any> {
        return MongoSagaStore.builder()
            .mongoTemplate(
                DefaultMongoTemplate
                    .builder()
                    .mongoDatabase(client)
                    .build()
            )
            .serializer(serializer)
            .build()
    }
}
