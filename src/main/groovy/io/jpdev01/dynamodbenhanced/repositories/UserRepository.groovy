package io.jpdev01.dynamodbenhanced.repositories

import groovy.transform.CompileStatic
import io.jpdev01.dynamodbenhanced.exceptions.UserAbsentException
import io.jpdev01.dynamodbenhanced.models.AuthorityType
import io.jpdev01.dynamodbenhanced.models.User
import org.springframework.stereotype.Service
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException

import java.time.Instant

@CompileStatic
@Service
class UserRepository {

    final DynamoDbTable<User> table

    UserRepository(DynamoDbEnhancedClient client) {
        table = client.table(User.simpleName, TableSchema.fromBean(User))
    }

    Optional<User> getById(Key key) {
        Optional.ofNullable(table.getItem(key))
    }

    Optional<List<User>> getAll() {
        table.scan()
            .items()
            .asList()
            .with {
                Optional.ofNullable(it ?: null)
            }
    }

    User create(User user) {
        table.updateItem() {
            user.regDate = Instant.now()
            it.item(user)
        }
    }

    User update(User user) {
        table.updateItem {
            user.updatedDate = Instant.now()
            it.item(user).ignoreNulls(true)
        }
    }

    Optional<User> delete(Key key) {
        Optional.ofNullable(table.deleteItem(key))
    }

}
