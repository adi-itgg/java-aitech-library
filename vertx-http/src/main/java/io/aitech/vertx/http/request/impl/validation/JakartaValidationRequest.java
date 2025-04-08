package io.aitech.vertx.http.request.impl.validation;

import io.aitech.vertx.http.exception.ViolationException;
import io.aitech.vertx.http.request.RequestValidation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Cleanup;
import lombok.val;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

public class JakartaValidationRequest implements RequestValidation {

  private final Validator validator;

  public JakartaValidationRequest() {
    val validationConfiguration = Validation.byDefaultProvider().configure();

    if (isParameterMessagInterpolatorExists()) {
      validationConfiguration.messageInterpolator(new ParameterMessageInterpolator());
    }

    @Cleanup val validatorFactory = validationConfiguration.buildValidatorFactory();

    this.validator = validatorFactory.getValidator();
  }

  private boolean isParameterMessagInterpolatorExists() {
    try {
      Class.forName("org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public void validate(Object request) {
    if (request == null) {
      throw new ViolationException("Request body cannot be null");
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
      throw new ViolationException(message);
    }
  }

}
