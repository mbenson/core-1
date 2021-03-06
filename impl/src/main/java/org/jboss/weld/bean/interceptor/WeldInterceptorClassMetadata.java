/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.weld.bean.interceptor;

import static org.jboss.weld.logging.messages.BeanMessage.PROXY_REQUIRED;
import static org.jboss.weld.util.collections.WeldCollections.immutableMap;

import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.Container;
import org.jboss.weld.annotated.enhanced.EnhancedAnnotatedMethod;
import org.jboss.weld.annotated.enhanced.EnhancedAnnotatedType;
import org.jboss.weld.annotated.slim.SlimAnnotatedType;
import org.jboss.weld.exceptions.InvalidObjectException;
import org.jboss.weld.interceptor.reader.DefaultMethodMetadata;
import org.jboss.weld.interceptor.spi.metadata.ClassMetadata;
import org.jboss.weld.interceptor.spi.metadata.MethodMetadata;
import org.jboss.weld.resources.ClassTransformer;

/**
 * @author Marius Bogoevici
 */
public class WeldInterceptorClassMetadata<T> implements ClassMetadata<T>, Serializable {
    private static final long serialVersionUID = -5087425231467781559L;

    private final SlimAnnotatedType<T> type;

    private final WeldInterceptorClassMetadata<?> superclass;

    private final Map<Method, MethodMetadata> methodMetadata;

    private WeldInterceptorClassMetadata(EnhancedAnnotatedType<T> weldClass) {
        this.type = weldClass.slim();
        Map<Method, MethodMetadata> methodMetadataMap = new HashMap<Method, MethodMetadata>();
        for (EnhancedAnnotatedMethod<?, ?> method : weldClass.getDeclaredEnhancedMethods()) {
            MethodMetadata methodMetadata = DefaultMethodMetadata.of(method, WeldAnnotatedMethodReader.getInstance());
            if (methodMetadata.getSupportedInterceptionTypes() != null && methodMetadata.getSupportedInterceptionTypes().size() != 0) {
                methodMetadataMap.put(method.getJavaMember(), methodMetadata);
            }
        }
        this.methodMetadata =  immutableMap(methodMetadataMap);
        if (weldClass.getEnhancedSuperclass() == null || weldClass.getEnhancedSuperclass().getJavaClass().equals(Object.class)) {
            this.superclass = null;
        } else {
            this.superclass = WeldInterceptorClassMetadata.of(weldClass.getEnhancedSuperclass());
        }
    }

    public static <T> WeldInterceptorClassMetadata<T> of(EnhancedAnnotatedType<T> weldClass) {
        return new WeldInterceptorClassMetadata<T>(weldClass);
    }

    public String getClassName() {
        return getJavaClass().getName();
    }

    public Iterable<MethodMetadata> getDeclaredMethods() {
        return methodMetadata.values();
    }

    @Override
    public MethodMetadata getDeclaredMethod(Method method) {
        return methodMetadata.get(method);
    }

    public Class<T> getJavaClass() {
        return type.getJavaClass();
    }

    public ClassMetadata<?> getSuperclass() {
        return superclass;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy<T>(type);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(PROXY_REQUIRED);
    }

    private static class SerializationProxy<T> implements Serializable {

        private static final long serialVersionUID = 514950313251775936L;

        private final SlimAnnotatedType<T> type;

        public SerializationProxy(SlimAnnotatedType<T> type) {
            this.type = type;
        }

        private Object readResolve() {
            EnhancedAnnotatedType<T> enhancedType = Container.instance().services().get(ClassTransformer.class).getEnhancedAnnotatedType(type);
            return new WeldInterceptorClassMetadata<T>(enhancedType);
        }
    }
}

