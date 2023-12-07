package ru.gb.JJ_S02_HW;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class TestProcessor {

    static void runTests(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            runTests(constructor.newInstance());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Method> getMethodsByAnnotation(Object obj,Class<?> annotation) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(x -> x.isAnnotationPresent((Class<? extends Annotation>) annotation))
                .collect(Collectors.toList());
    }

    public static void runTests(Object obj) {
        List<Method> methods = getMethodsByAnnotation(obj,Test.class);
        methods.sort(Comparator.comparing(y -> y.getAnnotation(Test.class).value()));

        List<Method> beforeEachMethods = getMethodsByAnnotation(obj,BeforeEach.class);
        beforeEachMethods.sort(Comparator.comparing(y -> y.getAnnotation(BeforeEach.class).value()));

        List<Method> afterEachMethods = getMethodsByAnnotation(obj,AfterEach.class);
        afterEachMethods.sort(Comparator.comparing(y -> y.getAnnotation(AfterEach.class).value()));

        for (Method method:methods) {
            beforeEachMethods.forEach(x -> runTestMethod(x,obj));
            runTestMethod(method,obj);
            afterEachMethods.forEach(x -> runTestMethod(x,obj));
        }
    }

    private static void runTestMethod(Method testMethod, Object testObj) {
        if (!testMethod.isAnnotationPresent(Skip.class)) {
            try {
                checkTestMethod(testMethod);
                testMethod.setAccessible(true);
                testMethod.invoke(testObj);
            } catch (InvocationTargetException | IllegalAccessException | AssertionError e) {
                throw new RuntimeException("Не удалось запустить тестовый метод \"" + testMethod.getName() + "\"");
            }
        }
    }

    private static void checkTestMethod(Method method) {
        if (!method.getReturnType().isAssignableFrom(void.class) || method.getParameterCount() != 0) {
            throw new IllegalArgumentException("Метод \"" + method.getName() + "\" должен быть void и не иметь аргументов");
        }
    }

}