package me.rasztabiga.thesis.saga.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Indexes
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.extensions.mongo.DefaultMongoTemplate
import org.axonframework.extensions.mongo.eventhandling.saga.repository.MongoSagaStore
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore
import org.axonframework.modelling.saga.repository.SagaStore
import org.axonframework.serialization.Serializer
import org.bson.Document
import org.bson.conversions.Bson
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
                    .mongoDatabase(client, "axon-saga")
                    .build()
            )
            .serializer(serializer)
            .build()
    }


    @Bean
    fun sagaStore(client: MongoClient, serializer: Serializer): SagaStore<Any> {
        val collection = client.getDatabase("axon-saga").getCollection("sagas")
        createIndexIfNotExists(collection, Indexes.ascending("sagaIdentifier"))
        createIndexIfNotExists(collection, Indexes.ascending("sagaType", "associations.key", "associations.value"))

        return MongoSagaStore.builder()
            .mongoTemplate(
                DefaultMongoTemplate
                    .builder()
                    .mongoDatabase(client, "axon-saga")
                    .build()
            )
            .serializer(serializer)
            .build()
    }

    private fun createIndexIfNotExists(collection: MongoCollection<Document>, index: Bson) {
        if (!collection.listIndexes().toList().map { it["name"] }.contains(index)) {
            collection.createIndex(index)
        }
    }
}
