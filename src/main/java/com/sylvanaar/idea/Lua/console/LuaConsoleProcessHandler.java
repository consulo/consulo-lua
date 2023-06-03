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

package com.sylvanaar.idea.lua.console;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 2/20/11
 * Time: 4:55 PM
 */
//public class LuaConsoleProcessHandler extends ColoredProcessHandler
//{
//	private final LanguageConsoleImpl myLanguageConsole;
//
//	public LuaConsoleProcessHandler(Process process, LanguageConsoleImpl languageConsole, String commandLine, Charset charset)
//	{
//		super(process, commandLine, charset);
//		myLanguageConsole = languageConsole;
//	}
//
//	@Override
//	public void coloredTextAvailable(String text, Key attributes)
//	{
//		ConsoleViewContentType outputType;
//		if(attributes == ProcessOutputTypes.STDERR)
//		{
//			outputType = ConsoleViewContentType.ERROR_OUTPUT;
//		}
//		else if(attributes == ProcessOutputTypes.SYSTEM)
//		{
//			outputType = ConsoleViewContentType.SYSTEM_OUTPUT;
//		}
//		else
//		{
//			outputType = ConsoleViewContentType.NORMAL_OUTPUT;
//		}
//
//		if(text.startsWith(">>"))
//		{
//			text = text.substring(3);
//			myLanguageConsole.setPrompt(">>");
//		}
//		else if(text.startsWith(">"))
//		{
//			text = text.substring(2);
//			myLanguageConsole.setPrompt(">");
//		}
//
//		if(outputType != ConsoleViewContentType.SYSTEM_OUTPUT)
//		{
//			myLanguageConsole.printToHistory(text, outputType.getAttributes());
//		}
//	}
//}

