package io.github.adiitgg.mapstruct.spi;

import org.mapstruct.ap.spi.AccessorNamingStrategy;
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

/**
 * A custom {@link AccessorNamingStrategy} for fluent getter & setter naming conventions.
 * This strategy extends the default behavior to support methods without 'get' or 'is' prefixes
 * as getters and methods without 'set' prefix as setters, following a fluent API design.
 */
public class DefaultWithFluentAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    /**
     * Determines if the given method is a getter method. This implementation considers methods
     * without 'is' or 'get' prefixes as potential getters if they have no parameters and
     * a non-void return type.
     *
     * @param method the method to check
     * @return true if the method is a getter, false otherwise
     */
    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        if (super.isGetterMethod(method)) {
            return true;
        }
        String methodName = method.getSimpleName().toString();
        return !methodName.startsWith("is") &&
          !methodName.startsWith("get") &&
          method.getParameters().isEmpty() &&
          method.getReturnType().getKind() != TypeKind.VOID &&
          !typeUtils.isAssignable( method.getReturnType(), method.getEnclosingElement().asType() );
    }

    /**
     * Retrieves the property name from a given getter or setter method. For fluent setters,
     * the name is derived by stripping the 'set' prefix if present. For other methods, the
     * standard JavaBeans convention is followed.
     *
     * @param getterOrSetterMethod the method from which to extract the property name
     * @return the property name
     */
    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {
      String methodName = getterOrSetterMethod.getSimpleName().toString();
      if ( isFluentSetter( getterOrSetterMethod ) ) {
        // If this is a fluent setter that starts with set and the 4th character is an uppercase one
        // then we treat it as a Java Bean style method (we get the property starting from the 4th character).
        // Otherwise we treat it as a fluent setter
        // For example, for the following methods:
        // * public Builder setSettlementDate(String settlementDate)
        // * public Builder settlementDate(String settlementDate)
        // We are going to extract the same property name settlementDate
        if ( methodName.startsWith( "set" )
          && methodName.length() > 3
          && Character.isUpperCase( methodName.charAt( 3 ) ) ) {
          return IntrospectorUtils.decapitalize( methodName.substring( 3 ) );
        }
        else {
          return methodName;
        }
      }
      return IntrospectorUtils.decapitalize( methodName.substring( methodName.startsWith( "is" ) ? 2 : (!methodName.startsWith("get") ? 0 : 3) ) );
    }
}
