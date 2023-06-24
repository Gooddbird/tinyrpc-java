package com.iker.tinyrpc.annotation;

import lombok.Getter;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class AnnotationContextHandler {

    @Getter
    private final String packageName;

    private final Reflections reflections;

    public AnnotationContextHandler(String packageName) {
        this.packageName = packageName;
        reflections = new Reflections(packageName);
    }

    public Set<Class<?>> scanAnnotation(Class<? extends Annotation> clazz) {
        return reflections.getTypesAnnotatedWith(clazz);

//        for (Class<?> item : classSet) {
//            TinyPBService annotation = item.getAnnotation(TinyPBService.class);
//            String name = Optional.ofNullable(annotation).<RuntimeException>orElseThrow(
//                    () -> { throw new RuntimeException("get TinyPBService annotation null"); }
//            ).name();
//
//            if (name.isEmpty()) {
//                name = item.getSuperclass().getSimpleName();
//            }
//
//            try {
//                SpringContextUtil.getBean("rpcServiceFactory", ProtobufRpcServiceFactory.class).registerService(name, item.newInstance());
//            } catch (InstantiationException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
    }



}
