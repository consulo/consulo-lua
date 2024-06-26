/*
 * Copyright 2012 Jon S Akhtar (Sylvanaar)
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

package com.sylvanaar.idea.lua.luaj;

import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.application.ApplicationManager;
import consulo.colorScheme.EditorColorsManager;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.color.ColorValue;
import jsyntaxpane.lexers.LuaLexer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import se.krka.kahlua.j2se.interpreter.History;
import se.krka.kahlua.j2se.interpreter.InputTerminal;
import se.krka.kahlua.j2se.interpreter.OutputTerminal;
import se.krka.kahlua.j2se.interpreter.jsyntax.JSyntaxUtil;
import se.krka.kahlua.j2se.interpreter.jsyntax.KahluaKit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: Sep 19, 2010
 * Time: 4:29:54 PM
 */
@SuppressWarnings("serial")
public class LuaJInterpreter extends JPanel
{
	private final OutputTerminal terminal;
	private final JLabel status = new JLabel("");

	private History history = new History();
	private Future<?> future;
	private Globals _G;

	public LuaJInterpreter()
	{
		super(new BorderLayout());

		JSyntaxUtil.setup();

		// create a Lua engine
		_G = JsePlatform.debugGlobals();

		ColorValue consoleColor = EditorColorsManager.getInstance().getGlobalScheme().getColor(ConsoleViewContentType.CONSOLE_BACKGROUND_KEY);

		final InputTerminal input = new InputTerminal(TargetAWT.to(consoleColor));

		final KahluaKit kit = new KahluaKit(new LuaLexer());
		JSyntaxUtil.installSyntax(input, true, kit);
		//new AutoComplete(input, platform, env);

		terminal = new OutputTerminal(TargetAWT.to(consoleColor), input);
		terminal.setPreferredSize(new Dimension(800, 400));
		terminal.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyChar() != 0)
				{
					input.requestFocus();
					Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});

		add(status, BorderLayout.SOUTH);
		add(terminal, BorderLayout.CENTER);

		history = new History();
		input.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent keyEvent)
			{
			}

			@Override
			public void keyPressed(KeyEvent keyEvent)
			{
			}

			@Override
			public void keyReleased(KeyEvent keyEvent)
			{
				if(isControl(keyEvent))
				{
					if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
					{
						if(isDone())
						{
							String text = input.getText();
							history.add(text);
							terminal.appendLua(withNewline(text));
							input.setText("");
							execute(text);
						}
						keyEvent.consume();
					}
					if(keyEvent.getKeyCode() == KeyEvent.VK_UP)
					{
						history.moveBack(input);
						keyEvent.consume();
					}
					if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
					{
						history.moveForward(input);
						keyEvent.consume();
					}
					if(keyEvent.getKeyCode() == KeyEvent.VK_C)
					{
						if(future != null)
						{
							future.cancel(true);
						}
						keyEvent.consume();
					}
				}
			}
		});

		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent componentEvent)
			{
				input.requestFocus();
			}
		});
	}

	private static String withNewline(String text)
	{
		if(text.endsWith("\n"))
		{
			return text;
		}
		return text + "\n";
	}

	private static boolean isControl(KeyEvent keyEvent)
	{
		return (keyEvent.getModifiers() & InputEvent.CTRL_MASK) != 0;
	}

	private void setStatus(final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				status.setText(text);
			}
		});
	}

	// A runnable that handles the entire execution
	public Runnable getRunnableExecution(final String text)
	{
		return new FutureTask<Object>(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					setStatus("[running...]");
					final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

					// evaluate Lua code from String
					final PrintStream stdout = _G.STDOUT;
					_G.STDOUT = new PrintStream(outputStream);
					_G.get("load").call(LuaValue.valueOf(text)).call();

					print(new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
					_G.STDOUT = stdout;
				}
				catch(LuaError e)
				{
					printError(e);
				}
				finally
				{
					setStatus("");
				}
			}
		}, null);
	}

	private void print(final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				terminal.appendOutput(text + '\n');
			}
		});
	}

	private void printError(final Exception e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				terminal.appendError(e.getMessage() + '\n');
			}
		});
	}

	public void execute(final String text)
	{
		future = ApplicationManager.getApplication().executeOnPooledThread(getRunnableExecution(text));
	}

	public boolean isDone()
	{
		return future == null || future.isDone();
	}

	public OutputTerminal getTerminal()
	{
		return terminal;
	}
}
