package io.github.adiitgg.arch.platform.qualifier;

import org.mapstruct.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface DbPrimary {}
