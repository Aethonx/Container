package io.shaded.container.agent;

import com.sun.tools.attach.VirtualMachine;
import io.shaded.container.reload.ClassReloadHook;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sourced from https://github.com/turn/RedefineClassAgent/blob/master/RedefineClassAgent.java
 *
 * @author Adam Lugowski
 */
public final class RedefineClassAgent {

	/**
	 * Use the Java logger to avoid any references to anything not supplied by the JVM. This avoids issues with
	 * classpath when compiling/loading this class as an agent.
	 */
	private static final Logger LOGGER = Logger.getLogger(RedefineClassAgent.class.getSimpleName());

	/**
	 * Populated when this class is loaded into the JVM as an agent (via {@link #ensureAgentLoaded()}.
	 */
	private static volatile Instrumentation instrumentation = null;

	/**
	 * How long to wait for the agent to load before giving up and assuming the load failed.
	 */
	private static final int AGENT_LOAD_WAIT_TIME_SEC = 10;

	/**
	 * Agent entry point. Do not call this directly.
	 * <p>
	 * This method is called by the JVM when this class is loaded as an agent.
	 * <p>
	 * Sets {@link #instrumentation} to {@code inst}, provided {@code inst} supports class redefinition.
	 *
	 * @param agentArgs ignored.
	 * @param inst      This is the reason this class exists. {@link Instrumentation} has the
	 *                  {@link Instrumentation#redefineClasses(ClassDefinition...)} method.
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {
		if (!inst.isRedefineClassesSupported()) {
			LOGGER.severe("Class redefinition not supported. Aborting.");
			return;
		}
		instrumentation = inst;
		instrumentation.addTransformer(new ClassReloadHook(), true);
	}

	/**
	 * Attempts to redefine class bytecode.
	 * <p>
	 * On first call this method will attempt to load an agent into the JVM to obtain an instance of
	 * {@link Instrumentation}. This agent load can introduce a pause (in practice 1 to 2 seconds).
	 *
	 * @param definitions classes to redefine.
	 * @throws UnmodifiableClassException as thrown by {@link Instrumentation#redefineClasses(ClassDefinition...)}
	 * @throws ClassNotFoundException     as thrown by {@link Instrumentation#redefineClasses(ClassDefinition...)}
	 * @throws FailedToLoadAgentException if agent either failed to load or if the agent wasn't able to get an
	 *                                    instance of {@link Instrumentation} that allows class redefinitions.
	 * @see Instrumentation#redefineClasses(ClassDefinition...)
	 */
	public static void redefineClasses(ClassDefinition... definitions)
			throws UnmodifiableClassException, ClassNotFoundException, FailedToLoadAgentException {
		ensureAgentLoaded();
		instrumentation.redefineClasses(definitions);
	}

	/**
	 * Attemps to define out classes
	 *
	 * @param classes to try to redefine classes.
	 */
	public static void redefineClasses(Class<?>... classes) throws FailedToLoadAgentException, UnmodifiableClassException {
		ensureAgentLoaded();
		instrumentation.retransformClasses(classes);
	}

	/**
	 * Lazy loads the agent that populates {@link #instrumentation}. OK to call multiple times.
	 *
	 * @throws FailedToLoadAgentException if agent either failed to load or if the agent wasn't able to get an
	 *                                    instance of {@link Instrumentation} that allows class redefinitions.
	 */
	private static void ensureAgentLoaded() throws FailedToLoadAgentException {
		if (instrumentation != null) {
			// already loaded
			return;
		}

		// load the agent
		try {
			File agentJar = createAgentJarFile();

			// Loading an agent requires the PID of the JVM to load the agent to. Find out our PID.
			String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
			String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));

			// load the agent
			VirtualMachine vm = VirtualMachine.attach(pid);
			vm.loadAgent(agentJar.getAbsolutePath(), "");
			vm.detach();
		} catch (Exception e) {
			throw new FailedToLoadAgentException(e);
		}

		// wait for the agent to load
		for (int sec = 0; sec < AGENT_LOAD_WAIT_TIME_SEC; sec++) {
			if (instrumentation != null) {
				// success!
				return;
			}

			try {
				LOGGER.info("Sleeping for 1 second while waiting for agent to load.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new FailedToLoadAgentException();
			}
		}

		// agent didn't load
		throw new FailedToLoadAgentException();
	}

	/**
	 * An agent must be specified as a .jar where the manifest has an Agent-Class attribute. Additionally, in order
	 * to be able to redefine classes, the Can-Redefine-Classes attribute must be true.
	 * <p>
	 * This method creates such an agent Jar as a temporary file. The Agent-Class is this class. If the returned Jar
	 * is loaded as an agent then {@link #agentmain(String, Instrumentation)} will be called by the JVM.
	 *
	 * @return a temporary {@link File} that points at Jar that packages this class.
	 * @throws IOException if agent Jar creation failed.
	 */
	private static File createAgentJarFile() throws IOException {
		File jarFile = File.createTempFile("agent", ".jar");
		jarFile.deleteOnExit();

		// construct a manifest that allows class redefinition
		Manifest manifest = new Manifest();
		Attributes mainAttributes = manifest.getMainAttributes();
		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		mainAttributes.put(new Attributes.Name("Agent-Class"), RedefineClassAgent.class.getName());
		mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
		mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");

		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
			// add the agent .class into the .jar
			JarEntry agent = new JarEntry(RedefineClassAgent.class.getName().replace('.', '/') + ".class");
			jos.putNextEntry(agent);
			jos.write(getBytesAsClass(RedefineClassAgent.class));
			jos.closeEntry();
			JarEntry classHook = new JarEntry(ClassReloadHook.class.getName().replace('.', '/') + ".class");
			jos.putNextEntry(classHook);
			jos.write(getBytesAsClass(ClassReloadHook.class));
			jos.closeEntry();
			// dump the class bytecode into the entry
		} catch (Exception e) {
			// Realistically this should never happen.
			LOGGER.log(Level.SEVERE, "Exception while creating RedefineClassAgent jar.", e);
			throw new IOException(e);
		}

		return jarFile;
	}


	private static String getClassName(Class cls) {
		return cls.getName().replace(".", "/") + ".class";
	}

	private static InputStream getStreamAsClass(Class cls) {
		return ClassLoader.getSystemResourceAsStream(getClassName(cls));
	}

	private static byte[] getBytesAsStream(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		byte[] data = new byte[65536];
		while ((read = in.read(data, 0, data.length)) != -1) {
			out.write(data, 0, read);
		}
		return out.toByteArray();
	}

	private static byte[] getBytesAsClass(Class cls) throws IOException {
		return getBytesAsStream(getStreamAsClass(cls));
	}

	/**
	 * Marks a failure to load the agent and get an instance of {@link Instrumentation} that is able to redefine
	 * classes.
	 */
	public static class FailedToLoadAgentException extends Exception {
		public FailedToLoadAgentException() {
			super();
		}

		public FailedToLoadAgentException(Throwable cause) {
			super(cause);
		}
	}
}
