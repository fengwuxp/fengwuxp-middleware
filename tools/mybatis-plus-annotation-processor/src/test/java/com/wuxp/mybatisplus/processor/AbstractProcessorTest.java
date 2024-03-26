/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
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
package com.wuxp.mybatisplus.processor;


import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.junit.jupiter.api.Assertions;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractProcessorTest {

    private final JavaCompiler compiler = new EclipseCompiler();

    protected static List<String> getFiles(String path) {
        List<String> classes = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(path).listFiles())) {
            if (file.getName().endsWith(".java")) {
                classes.add(file.getPath());
            } else if (file.isDirectory() && !file.getName().startsWith(".")) {
                classes.addAll(getFiles(file.getAbsolutePath()));
            }
        }
        return classes;
    }

    protected void process(Class<? extends AbstractProcessor> processorClass, List<String> classes) throws IOException {
        compile(processorClass, classes);
    }

    protected void compile(Class<? extends AbstractProcessor> processorClass, List<String> classes) throws IOException {
        List<String> options = new ArrayList<String>(classes.size() + 3);
        options.add("-s");
        options.add("target/generated-test-sources/test-annotations");
        options.add("-proc:only");
        options.add("-processor");
        options.add(processorClass.getName());
        options.add("-sourcepath");
        options.add("src/test/java");
        options.addAll(getAPTOptions());
        options.addAll(classes);

        ByteArrayOutputStream out = getStdOut();
        ByteArrayOutputStream err = getStdErr();
        int compilationResult = compiler.run(null, out, err, options.toArray(new String[options.size()]));

        if (compilationResult != 0) {
            System.err.println(compiler.getClass().getName());
            Assertions.fail("Compilation Failed:\n " + new String(err.toByteArray(), "UTF-8"));
        }
    }

    protected ByteArrayOutputStream getStdOut() {
        return new ByteArrayOutputStream();
    }

    protected ByteArrayOutputStream getStdErr() {
        return new ByteArrayOutputStream();
    }

    protected Collection<String> getAPTOptions() {
        return Collections.emptyList();
    }

}
