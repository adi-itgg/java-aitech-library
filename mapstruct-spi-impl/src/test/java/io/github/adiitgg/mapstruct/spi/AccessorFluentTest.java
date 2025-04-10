package io.github.adiitgg.mapstruct.spi;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class AccessorFluentTest {

  /*@Test
  public void fluentClassToRecord() {
    FluentData fluentData = new FluentData().name("test");
    DataRecord dataRecord = FluentMapper.INSTANCE.toDataRecord(fluentData);

    assert dataRecord.name().equals("test");
  }

  @Test
  public void recordToFluentClass() {
    DataRecord dataRecord = new DataRecord("test");
    FluentData fluentData = FluentMapper.INSTANCE.toFluentData(dataRecord);

    assert fluentData.name().equals("test");
  }*/

  @Test
  void getter() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();
    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("getName");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.TYPEVAR);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    when(executableElement.getParameters()).thenReturn(Collections.emptyList());
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(typeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertTrue(strategy.isGetterMethod(executableElement));
    assertEquals("name", strategy.getPropertyName(executableElement));
  }

  @Test
  void getterFluent() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();

    val types = mock(Types.class);
    when(types.isAssignable(any(), any())).thenReturn(false);

    val mapStructProcessingEnv = mock(MapStructProcessingEnvironment.class);
    when(mapStructProcessingEnv.getTypeUtils()).thenReturn(types);

    strategy.init(mapStructProcessingEnv);

    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("name");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.TYPEVAR);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    when(executableElement.getParameters()).thenReturn(Collections.emptyList());
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(typeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertTrue(strategy.isGetterMethod(executableElement));
    assertEquals("name", strategy.getPropertyName(executableElement));
  }

  @Test
  void getterGetFluent() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();

    val types = mock(Types.class);
    when(types.isAssignable(any(), any())).thenReturn(false);

    val mapStructProcessingEnv = mock(MapStructProcessingEnvironment.class);
    when(mapStructProcessingEnv.getTypeUtils()).thenReturn(types);

    strategy.init(mapStructProcessingEnv);

    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("getName");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.TYPEVAR);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    when(executableElement.getParameters()).thenReturn(Collections.emptyList());
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(typeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertTrue(strategy.isGetterMethod(executableElement));
    assertEquals("name", strategy.getPropertyName(executableElement));
  }


  @Test
  void propertySetterSet() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();

    val types = mock(Types.class);
    when(types.isAssignable(any(), any())).thenReturn(false);

    val mapStructProcessingEnv = mock(MapStructProcessingEnvironment.class);
    when(mapStructProcessingEnv.getTypeUtils()).thenReturn(types);

    strategy.init(mapStructProcessingEnv);

    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("setName");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.VOID);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    when(executableElement.getParameters()).thenReturn(Collections.emptyList());
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(typeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertFalse(strategy.isGetterMethod(executableElement));
    assertEquals("setName", strategy.getPropertyName(executableElement));
  }

  @SuppressWarnings("unchecked")
  @Test
  void propertySetterFluent() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();

    val types = mock(Types.class);
    when(types.isAssignable(any(), any())).thenReturn(true);

    val mapStructProcessingEnv = mock(MapStructProcessingEnvironment.class);
    when(mapStructProcessingEnv.getTypeUtils()).thenReturn(types);

    strategy.init(mapStructProcessingEnv);

    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("name");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.PACKAGE);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    val params = mock(List.class);
    when(params.size()).thenReturn(1);
    when((params.isEmpty())).thenReturn(false);

    when(executableElement.getParameters()).thenReturn(params);
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(typeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertFalse(strategy.isGetterMethod(executableElement));
    assertEquals("name", strategy.getPropertyName(executableElement));
  }


  @SuppressWarnings("unchecked")
  @Test
  void propertySetterFluentDiffReturn() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();

    val types = mock(Types.class);
    when(types.isAssignable(any(), any())).thenReturn(true);

    val mapStructProcessingEnv = mock(MapStructProcessingEnvironment.class);
    when(mapStructProcessingEnv.getTypeUtils()).thenReturn(types);

    strategy.init(mapStructProcessingEnv);

    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("name");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.PACKAGE);

    val returnTypeMirror = mock(TypeMirror.class);
    when(returnTypeMirror.getKind()).thenReturn(TypeKind.PACKAGE);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    val params = mock(List.class);
    when(params.size()).thenReturn(1);
    when((params.isEmpty())).thenReturn(false);

    when(executableElement.getParameters()).thenReturn(params);
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(returnTypeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertFalse(strategy.isGetterMethod(executableElement));
    assertEquals("name", strategy.getPropertyName(executableElement));
  }

  @SuppressWarnings("unchecked")
  @Test
  void propertySetterSetFluentDiffReturn() {
    val strategy = new DefaultWithFluentAccessorNamingStrategy();

    val types = mock(Types.class);
    when(types.isAssignable(any(), any())).thenReturn(true);

    val mapStructProcessingEnv = mock(MapStructProcessingEnvironment.class);
    when(mapStructProcessingEnv.getTypeUtils()).thenReturn(types);

    strategy.init(mapStructProcessingEnv);

    val executableElement = mock(ExecutableElement.class);

    val name = mock(Name.class);
    when(name.toString()).thenReturn("setName");

    val typeMirror = mock(TypeMirror.class);
    when(typeMirror.getKind()).thenReturn(TypeKind.PACKAGE);

    val returnTypeMirror = mock(TypeMirror.class);
    when(returnTypeMirror.getKind()).thenReturn(TypeKind.PACKAGE);

    val element = mock(Element.class);
    when(element.asType()).thenReturn(typeMirror);

    val params = mock(List.class);
    when(params.size()).thenReturn(1);
    when((params.isEmpty())).thenReturn(false);

    when(executableElement.getParameters()).thenReturn(params);
    when(executableElement.getSimpleName()).thenReturn(name);
    when(executableElement.getReturnType()).thenReturn(returnTypeMirror);
    when(executableElement.getEnclosingElement()).thenReturn(element);


    assertFalse(strategy.isGetterMethod(executableElement));
    assertEquals("name", strategy.getPropertyName(executableElement));
  }

}
