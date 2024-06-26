/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
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
package com.sylvanaar.idea.lua.lang.psi.controlFlow;

import com.sylvanaar.idea.lua.lang.psi.controlFlow.impl.InstructionImpl;
import com.sylvanaar.idea.lua.lang.psi.expressions.LuaExpression;


public class AssertionInstruction extends InstructionImpl {
  private final boolean myNegate;

  public AssertionInstruction(int num, LuaExpression assertion, boolean negate) {
    super(assertion, num);
    myNegate = negate;
  }

  public boolean isNegate() {
    return myNegate;
  }

  protected String getElementPresentation() {
    return "assertion: " + (myNegate ? "! " : "") + getElement().getText();
  }

}
