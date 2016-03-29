/*
	Std.java
	Copyright (c) 2016 Masato Kokubo
*/

package org.mkokubo.lightsleep.logger;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.function.Supplier;

/**
	Outputs logs to the standard output or error output.

	@since 1.0.0
	@author Masato Kokubo
*/
public abstract class Std implements Logger {
	public static abstract class Out extends Std {
		public Out(Level level) {
			super(System.out, level);
		}

		public static class Trace extends Out {
			public Trace(String name) {super(Level.TRACE);}
		}

		public static class Debug extends Out {
			public Debug(String name) {super(Level.DEBUG);}
		}

		public static class Info extends Out {
			public Info (String name) {super(Level.INFO );}
		}

		public static class Warn extends Out {
			public Warn (String name) {super(Level.WARN );}
		}

		public static class Error extends Out {
			public Error(String name) {super(Level.ERROR);}
		}

		public static class Fatal extends Out {
			public Fatal(String name) {super(Level.FATAL);}
		}
	}

	public static abstract class Err extends Std {
		public Err(Level level) {
			super(System.err, level);
		}

		public static class Trace extends Err {
			public Trace(String name) {super(Level.TRACE);}
		}

		public static class Debug extends Err {
			public Debug(String name) {super(Level.DEBUG);}
		}

		public static class Info extends Err {
			public Info (String name) {super(Level.INFO );}
		}

		public static class Warn extends Err {
			public Warn (String name) {super(Level.WARN );}
		}

		public static class Error extends Err {
			public Error(String name) {super(Level.ERROR);}
		}

		public static class Fatal extends Err {
			public Fatal(String name) {super(Level.FATAL);}
		}
	}

	// Level enum
	protected enum Level {TRACE, DEBUG, INFO, WARN, ERROR, FATAL}

	// The print stream
	private PrintStream stream;

	// The level
	private Level level;

	// The message format
	private static String messageFormat = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s ";

	/**
		Constructs a new <b>Std</b> with the specified name.

		@param stream the print stream
		@param level the level

		@throws NullPointerException <b>name</b> is <b>null</b>
	*/
	protected Std(PrintStream stream, Level level) {
		if (stream == null) throw new NullPointerException("Std.<init>: stream == null");
		if (level == null) throw new NullPointerException("Std.<init>: level == null");
		this.stream = stream;
		this.level = level;
	}

	/**
		Outputs a message to the log at <b>level</b>.

		@param level the level
		@param message a message
	*/
	private void println(Level level, String message) {
		if (level.compareTo(this.level) >= 0)
			stream.println(
				String.format(messageFormat, new Timestamp(System.currentTimeMillis()), level)
				+ message);
	}

	/**
		Outputs a message with a <b>Throwable</b> to the log at <b>level</b>.

		@param level the level
		@param message a message
		@param t a Throwable
	*/
	private void println(Level level, String message, Throwable t) {
		println(level, message + " " + t.toString());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message) {
		println(Level.TRACE, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message) {
		println(Level.DEBUG, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message) {
		println(Level.INFO, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message) {
		println(Level.WARN, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message) {
		println(Level.ERROR, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message) {
		println(Level.FATAL, message);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(String message, Throwable t) {
		println(Level.TRACE, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(String message, Throwable t) {
		println(Level.DEBUG, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(String message, Throwable t) {
		println(Level.INFO,message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(String message, Throwable t) {
		println(Level.WARN, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(String message, Throwable t) {
		println(Level.ERROR, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(String message, Throwable t) {
		println(Level.FATAL, message, t);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void trace(Supplier<String> messageSupplier) {
		trace(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void debug(Supplier<String> messageSupplier) {
		debug(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void info(Supplier<String> messageSupplier) {
		info(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void warn(Supplier<String> messageSupplier) {
		warn(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void error(Supplier<String> messageSupplier) {
		error(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public void fatal(Supplier<String> messageSupplier) {
		fatal(messageSupplier.get());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isFatalEnabled() {
		return true;
	}
}
