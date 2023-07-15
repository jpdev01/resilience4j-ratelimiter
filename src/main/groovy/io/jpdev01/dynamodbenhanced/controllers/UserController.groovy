package io.jpdev01.dynamodbenhanced.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.jpdev01.dynamodbenhanced.UserCreationDto
import io.jpdev01.dynamodbenhanced.UserUpdateDto
import io.jpdev01.dynamodbenhanced.exceptions.UserAbsentException
import io.jpdev01.dynamodbenhanced.exceptions.UserAlreadyExistsException
import io.jpdev01.dynamodbenhanced.models.AuthorityType
import io.jpdev01.dynamodbenhanced.models.User
import io.jpdev01.dynamodbenhanced.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RequestMapping('/user')
@RestController
class UserController {

    private final UserService userService
    private final ObjectMapper mapper

    UserController(UserService service, ObjectMapper mapper) {
        this.userService = service
        this.mapper = mapper
    }

    @GetMapping(path = '{id}', produces = APPLICATION_JSON_VALUE)
    ResponseEntity getById(@PathVariable String id, @RequestParam AuthorityType authority) {
        userService.getById(id, authority).with { ResponseEntity.of(it) }
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<User>> getAll() {
        userService.all.with { ResponseEntity.of(it) }
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @RateLimiter(name = "CreateUser")
    ResponseEntity create(@RequestBody User user) {
        try {
            userService.create(user).with { convertUserResponse(it, UserCreationDto) }
        } catch (UserAlreadyExistsException ignored) {
            ResponseEntity.badRequest().body([
                error_message: 'User already created! Try update it...',
                user_details : [id: user.id, authority: user.authority]])
        }
    }

    @PatchMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity updatePartial(@RequestBody User user) {
        try {
            userService.update(user).with { convertUserResponse(it, UserUpdateDto) }
        } catch (UserAbsentException ignored) {
            ResponseEntity.badRequest().body([
                error_message: 'User absent! Try creating it first...',
                user_details : [id: user.id, authority: user.authority]])
        }
    }

    @DeleteMapping(path = '{id}', produces = APPLICATION_JSON_VALUE)
    ResponseEntity delete(@PathVariable String id, @RequestParam AuthorityType authority) {
        userService.delete(id, authority).with { ResponseEntity.of(it) }
    }

    private ResponseEntity<UserCreationDto> convertUserResponse(User user, Class<UserCreationDto> type) {
        ResponseEntity.ok(mapper.convertValue(user, type))
    }

}
