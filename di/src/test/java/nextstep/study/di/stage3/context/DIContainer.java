package nextstep.study.di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.study.ConsumerWrapper;
import nextstep.study.FunctionWrapper;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        beans = Collections.singleton(createBean(classes));
        beans.forEach(ConsumerWrapper.accept(this::setField));
    }

    private Set<Object> createBean(Set<Class<?>> classes) {
        return classes.stream()
                .map(FunctionWrapper.apply(Class::getDeclaredConstructor)) // 리플랙션으로 생성자 가져옴
                .peek(ConsumerWrapper.accept(
                        constructor -> constructor.setAccessible(true))) //private 변수나 메서드에 리플랙션으로 접근 가능하도록 함
                .map(FunctionWrapper.apply(Constructor::newInstance))//새로운 인스턴스 생성
                .collect(Collectors.toUnmodifiableSet());
    }

    private void setField(Object beanClass) {
        Field[] declaredFields = beanClass.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {// 리플랙션으로 찾은 클래스들에 필드를 꺼냄
            setField(beanClass, declaredField);
        }
    }

    private void setField(Object beanClass, Field declaredField) {
        Class<?> fieldType = declaredField.getType(); // 필드의 타입 클래스
        declaredField.setAccessible(true);

        beans.stream()
                .filter(fieldType::isInstance) // field에 맞는 bean을 찾음
                .forEach(ConsumerWrapper.accept(fieldBean -> declaredField.set(beanClass, fieldBean)));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(aClass::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않는 bean입니당"));
    }
}
