package software.amazon.awssdk.enhanced.dynamodb.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TypeTokenTest {
    @Test
    public void anonymousCreationCapturesComplexTypeArguments() {
        TypeToken<Map<String, List<List<String>>>> typeToken = new TypeToken<Map<String, List<List<String>>>>(){};
        assertThat(typeToken.rawClass()).isEqualTo(Map.class);
        assertThat(typeToken.rawClassParameters().get(0).rawClass()).isEqualTo(String.class);
        assertThat(typeToken.rawClassParameters().get(1).rawClass()).isEqualTo(List.class);
        assertThat(typeToken.rawClassParameters().get(1).rawClassParameters().get(0).rawClass()).isEqualTo(List.class);
        assertThat(typeToken.rawClassParameters().get(1).rawClassParameters().get(0).rawClassParameters().get(0).rawClass())
                .isEqualTo(String.class);
    }

    @Test
    public void customTypesWork() {
        TypeToken<TypeTokenTest> typeToken = new TypeToken<TypeTokenTest>(){};
        assertThat(typeToken.rawClass()).isEqualTo(TypeTokenTest.class);
    }

    @Test
    public void nonStaticInnerTypesWork() {
        TypeToken<InnerType> typeToken = new TypeToken<InnerType>(){};
        assertThat(typeToken.rawClass()).isEqualTo(InnerType.class);
    }

    @Test
    public void staticInnerTypesWork() {
        TypeToken<InnerStaticType> typeToken = new TypeToken<InnerStaticType>(){};
        assertThat(typeToken.rawClass()).isEqualTo(InnerStaticType.class);
    }

    @Test
    public void wildcardTypesDontWork() {
        assertThatThrownBy(() -> new TypeToken<List<?>>(){}).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public <T> void genericParameterTypesDontWork() {
        assertThatThrownBy(() -> new TypeToken<List<T>>(){}).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void helperCreationMethodsWork() {
        assertThat(TypeToken.from(String.class).rawClass()).isEqualTo(String.class);

        assertThat(TypeToken.listOf(String.class)).satisfies(v -> {
            assertThat(v.rawClass()).isEqualTo(List.class);
            assertThat(v.rawClassParameters()).hasSize(1);
            assertThat(v.rawClassParameters().get(0).rawClass()).isEqualTo(String.class);
        });

        assertThat(TypeToken.mapOf(String.class, Integer.class)).satisfies(v -> {
            assertThat(v.rawClass()).isEqualTo(Map.class);
            assertThat(v.rawClassParameters()).hasSize(2);
            assertThat(v.rawClassParameters().get(0).rawClass()).isEqualTo(String.class);
            assertThat(v.rawClassParameters().get(1).rawClass()).isEqualTo(Integer.class);
        });
    }

    @Test
    public void equalityIsBasedOnInnerEquality() {
        assertThat(TypeToken.from(String.class)).isEqualTo(TypeToken.from(String.class));
        assertThat(TypeToken.from(String.class)).isNotEqualTo(TypeToken.from(Integer.class));

        assertThat(new TypeToken<Map<String, List<String>>>(){}).isEqualTo(new TypeToken<Map<String, List<String>>>(){});
        assertThat(new TypeToken<Map<String, List<String>>>(){}).isNotEqualTo(new TypeToken<Map<String, List<Integer>>>(){});
    }

    public class InnerType {
    }

    public static class InnerStaticType {
    }
}