package io.jpdev01.dynamodbenhanced.configs.local

import groovy.util.logging.Slf4j
import io.jpdev01.dynamodbenhanced.models.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException

import javax.annotation.PostConstruct

@ConditionalOnProperty(value = 'application.localstack', havingValue = 'true')
@Slf4j
@Configuration
class LocalStackConfig {

    final DynamoDbClient dynamoDbClient
    final DynamoDbEnhancedClient dynamoEnhancedDbClient
    private final List<String> queuesNames
    private final List<String> tablesNames

    LocalStackConfig(DynamoDbClient dynamoDbClient,
                     DynamoDbEnhancedClient dynamoEnhancedDbClient,
                     @Value('${application.dynamo.tables.names:}') List<String> tablesNames) {
        this.dynamoDbClient = dynamoDbClient
        this.dynamoEnhancedDbClient = dynamoEnhancedDbClient
        this.queuesNames = queuesNames
        this.tablesNames = tablesNames
    }

    @PostConstruct
    void init() {
        log.info('Starting LocalStack resources creation')
        initDynamoEnhanced()
        log.info('Finished LocalStack resources creation')
    }

    void initDynamoEnhanced() {
        [User].each { Class clazz ->
            try {
                dynamoEnhancedDbClient.table(clazz.simpleName, TableSchema.fromBean(clazz)).createTable()
            } catch (ResourceInUseException ignored) {
            }
        }

    }

}
