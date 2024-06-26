/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.sylvanaar.idea.lua.run.kahlua;

import consulo.process.ProcessHandler;
import consulo.logging.Logger;

import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 19, 2010
 * Time: 3:06:41 PM
 */
public class KahluaProcessHandler extends ProcessHandler {
    private static final Logger log = Logger.getInstance("Lua.KahluaProcessHandler");
    public KahluaProcessHandler() {
    }

    @Override
    protected void destroyProcessImpl() {
       log.info("destroyProcessImpl");
    }

    @Override
    protected void detachProcessImpl() {
        log.info("detachProcessImpl");
    }

    @Override
    public boolean detachIsDefault() {
        log.info("detachIsDefault");
        return false;
    }

    @Override
    public OutputStream getProcessInput() {
        log.info("getProcessInput");
        return null;  
    }
}
