package io.aitech.arch.platform.misc;

import io.aitech.arch.platform.exception.InfoLevelException;
import io.avaje.inject.Component;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.function.Consumer;

@Component
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public final class JKTValidator implements Consumer<Object> {

  private final Validator validator;

  @Override
  public void accept(Object request) {
    if (request == null) {
      throw new InfoLevelException("Request body cannot be null");
    }
    val violations = validator.validate(request);
    if (!violations.isEmpty()) {
      val violation = violations.iterator().next();
      val isDefaultMessage = violation.getMessageTemplate().startsWith("{jakarta.validation.") && violation.getMessageTemplate().endsWith(".message}");
      var message = violation.getMessage();
      if (isDefaultMessage) {
        message = violation.getPropertyPath() + " " + message;
      } else {
        message = message.replace("$field", violation.getPropertyPath().toString());
      }
      throw new InfoLevelException(message);
    }
  }

}
