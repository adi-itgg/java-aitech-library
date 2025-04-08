package io.aitech.arch.platform.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

@Factory
public final class DIValidation {


  @Bean(autoCloseable = true)
  ValidatorFactory javaxValidatorFactory() {
    return Validation.byDefaultProvider()
      .configure()
      .messageInterpolator(new ParameterMessageInterpolator())
      .buildValidatorFactory();
  }

  @Bean
  Validator javaxValidator(ValidatorFactory factory) {
    return factory.getValidator();
  }

}
