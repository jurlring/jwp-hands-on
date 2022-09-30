package nextstep.study.di.stage4.annotations;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private static final Set<Object> beans = new HashSet<>();

    public static DIContainer createContainerForPackage(final String rootPackageName)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(rootPackageName);
        Set<Class<?>> serviceClass = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> service : serviceClass) {
            beans.add(service.getDeclaredConstructor().newInstance());
        }
        Set<Class<?>> repositoryClass = reflections.getTypesAnnotatedWith(Repository.class);
        for (Class<?> repository : repositoryClass) {
            beans.add(repository.getDeclaredConstructor().newInstance());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(bean -> bean.getClass().isInstance(aClass))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
