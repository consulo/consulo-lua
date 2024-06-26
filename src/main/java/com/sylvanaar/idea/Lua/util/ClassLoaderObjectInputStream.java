/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sylvanaar.idea.lua.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Proxy;

/**
 * A special ObjectInputStream that loads a class based on a specified
 * <code>ClassLoader</code> rather than the system default.
 * <p>
 * This is useful in dynamic container environments.
 *
 * @author Paul Hammant
 * @version $Id: ClassLoaderObjectInputStream.java 1080933 2011-03-12 13:53:38Z niallp $
 * @since Commons IO 1.1
 */
public class ClassLoaderObjectInputStream extends ObjectInputStream {

    /** The class loader to use. */
    private final ClassLoader classLoader;

    /**
     * Constructs a new ClassLoaderObjectInputStream.
     *
     * @param classLoader  the ClassLoader from which classes should be loaded
     * @param inputStream  the InputStream to work on
     * @throws java.io.IOException in case of an I/O error
     * @throws java.io.StreamCorruptedException if the stream is corrupted
     */
    public ClassLoaderObjectInputStream(
            ClassLoader classLoader, InputStream inputStream)
            throws IOException, StreamCorruptedException {
        super(inputStream);
        this.classLoader = classLoader;
    }

    /**
     * Resolve a class specified by the descriptor using the
     * specified ClassLoader or the super ClassLoader.
     *
     * @param objectStreamClass  descriptor of the class
     * @return the Class object described by the ObjectStreamClass
     * @throws java.io.IOException in case of an I/O error
     * @throws ClassNotFoundException if the Class cannot be found
     */
    @Override
    protected Class<?> resolveClass(ObjectStreamClass objectStreamClass)
            throws IOException, ClassNotFoundException {
        
        Class<?> clazz = Class.forName(objectStreamClass.getName(), false, classLoader);

        if (clazz != null) {
            // the classloader knows of the class
            return clazz;
        } else {
            // classloader knows not of class, let the super classloader do it
            return super.resolveClass(objectStreamClass);
        }
    }

    /**
     * Create a proxy class that implements the specified interfaces using
     * the specified ClassLoader or the super ClassLoader.
     *
     * @param interfaces the interfaces to implement
     * @return a proxy class implementing the interfaces
     * @throws java.io.IOException in case of an I/O error
     * @throws ClassNotFoundException if the Class cannot be found
     * @see java.io.ObjectInputStream#resolveProxyClass(String[])
     * @since Commons IO 2.1
     */
    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException,
            ClassNotFoundException {
        Class<?>[] interfaceClasses = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaceClasses[i] = Class.forName(interfaces[i], false, classLoader);
        }
        try {
            return Proxy.getProxyClass(classLoader, interfaceClasses);
        } catch (IllegalArgumentException e) {
            return super.resolveProxyClass(interfaces);
        }
    }
    
}
