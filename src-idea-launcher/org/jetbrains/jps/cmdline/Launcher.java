/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.jps.cmdline;

import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.LauncherImpl;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * make sure groovy-eclipse-batch-2.4.13-01.jar contains latest  jrr lib
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        LauncherImpl.main(args);
    }
}
