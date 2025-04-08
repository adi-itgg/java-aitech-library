package io.aitech.vertx.db.orm.util;

import io.aitech.vertx.db.orm.annotation.Entity;
import io.aitech.vertx.db.orm.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UtilTest {

  @Data
  @Accessors(fluent = true)
  @Entity(name = "m_user")
  @AllArgsConstructor
  public static class EntityTest {

    private @Id Long id;
    private String foo;

  }

  @Test
  void invokeExactMethodHandleStatic() throws Throwable {
    OffsetDateTime now = (OffsetDateTime) Utils.findTimeStaticMHByType(OffsetDateTime.class).invokeExact();
    assertNotNull(now);
  }

  @Test
  void invokeMethodHandleStatic() throws Throwable {
    val now = Utils.findTimeStaticMHByType(OffsetDateTime.class).invoke();
    assertNotNull(now);
  }


  @Test
  void lambdaMetaFactorySetterField() throws Throwable {
    val field = EntityTest.class.getDeclaredField("foo");
    field.trySetAccessible();
    val entity = new EntityTest(1L, "test");
    val setter = Utils.createSetter(field);
    setter.accept(entity, "newTest");
    assertEquals("newTest", entity.foo);
  }

  @Test
  void lambdaMetaFactoryGetterField() throws Throwable {
    val field = EntityTest.class.getDeclaredField("foo");
    field.trySetAccessible();
    val entity = new EntityTest(1L, "test");
    val getter = Utils.createGetter(field);
    assertEquals("test", getter.apply(entity));
  }

}
