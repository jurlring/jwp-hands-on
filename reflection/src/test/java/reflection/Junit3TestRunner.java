package reflection;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class Junit3TestRunner {

    @Test
    void run() throws Exception {
        Junit3Test junit3Test = new Junit3Test();
        Class<Junit3Test> clazz = Junit3Test.class;
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("test")) {
                methods[i].invoke(junit3Test);
            }
        }
    }
}
