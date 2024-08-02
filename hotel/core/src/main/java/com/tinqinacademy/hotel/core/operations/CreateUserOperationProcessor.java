package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.base.BaseOperation;
import com.tinqinacademy.hotel.api.errors.ErrorMapper;
import com.tinqinacademy.hotel.api.errors.Errors;
import com.tinqinacademy.hotel.api.errors.Error;
import com.tinqinacademy.hotel.api.messages.ExceptionMessages;
import com.tinqinacademy.hotel.api.operations.createuser.CreateUserInput;
import com.tinqinacademy.hotel.persistence.entitites.User;
import com.tinqinacademy.hotel.api.operations.createuser.CreateUserOperation;
import com.tinqinacademy.hotel.api.operations.createuser.CreateUserOutput;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CreateUserOperationProcessor extends BaseOperation implements CreateUserOperation {

    private final UserRepository userRepository;

    public CreateUserOperationProcessor ( Validator validator, ConversionService conversionService, ErrorMapper errorMapper, UserRepository userRepository ) {
        super(validator, conversionService, errorMapper);
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Either<Errors, CreateUserOutput> process(CreateUserInput input) {
        // Логваме началото на операцията за проверка на налични стаи, за по-добра видимост при отстраняване на проблеми
        // и за следене на изпълнението на операцията.
        return Try.of(() -> {
                    log.info("Start creating user input: {}", input);

                    validateInput(input);

                    User user = User.builder()
                            .firstName(input.getFirstName())
                            .lastName(input.getLastName())
                            .userPass(input.getUserPass())
                            .email(input.getEmail())
                            .birthday(input.getBirthday())
                            .phoneNumber(input.getPhoneNumber())
                            .build();

                    User savedUser = userRepository.save(user);

                    CreateUserOutput output = CreateUserOutput.builder()
                            .userId(savedUser.getId())
                            .build();

                    log.info("End creating user output: {}", output);
                    return output;
                })
                .toEither()
                .mapLeft(this::createErrors);
    }

    private void validateInput(CreateUserInput input) {
        if (input.getFirstName() == null || input.getLastName() == null ||
                input.getUserPass() == null || input.getEmail() == null ||
                input.getBirthday() == null || input.getPhoneNumber() == null) {
            throw new RuntimeException(ExceptionMessages.INVALID_DATA_INPUT);
        }
    }

    private Errors createErrors(Throwable throwable) {
        String message = throwable.getMessage() != null ? throwable.getMessage() : "An unexpected error occurred.";
        Error error = Error.builder()
                .message(message)
                .build();
        return new Errors(List.of(error).toString());
    }
}
