package fr.univmobile.backend.sanitycheck;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import net.avcompris.binding.dom.helper.DomBinderUtils;
import fr.univmobile.backend.core.User;
import fr.univmobile.commons.ReflectUtils;
import fr.univmobile.commons.datasource.Entry;

/**
 * Use this class to test against cold data, that is XML data files on the File
 * System.
 * <p>
 * This is useful to sanity check data prior to launch the backend web
 * application, after a backup restore for instance.
 */
public class BackendChecker {

	/**
	 * Check a data directory, whether it is a "<code>users</code>" directory, a
	 * "<code>poi</code>" directory, etc. This method dispatches the calls to
	 * specialized methods such as {@link #checkUsersDirectory(File)}.
	 */
	public CheckReport checkDirectory(final File dir) throws IOException {

		checkNotNull(dir, "dir");

		final String dirName = dir.getName();

		if ("users".equals(dirName)) {

			return checkUsersDirectory(dir);
		}

		throw new IllegalArgumentException("Unknown data directory name: "
				+ dirName);
	}

	public CheckReport checkUsersDirectory(final File dir) throws IOException {

		checkNotNull(dir, "dir");

		final ReportBuilder reportBuilder = new DefaultReportBuilder();

		for (final File file : dir.listFiles()) {

			checkAllNonNullableFields(file, User.class, reportBuilder);
		}

		return reportBuilder.build();
	}

	private static void checkAllNonNullableFields(final File file,
			final Class<? extends Entry<?>> clazz,
			final ReportBuilder reportBuilder) throws IOException {

		reportBuilder.startEntryCheck(file, clazz);

		final Entry<?> instance = DomBinderUtils.xmlContentToJava(file, clazz);

		for (final Method accessorMethod : clazz.getMethods()) {

			if (accessorMethod.isAnnotationPresent(Nullable.class)) {
				continue;
			}

			final String methodName = accessorMethod.getName();

			if (!methodName.startsWith("get")
					|| accessorMethod.getParameterTypes().length != 0) {
				continue;
			}

			final String isNullMethodName = "isNull" + methodName.substring(3);

			if (ReflectUtils.hasMethod(clazz, isNullMethodName)) {

				final Method isNullMethod;

				try {

					isNullMethod = clazz.getMethod(isNullMethodName);

				} catch (final NoSuchMethodException e) {

					throw new RuntimeException(e);
				}

				final boolean isNull;
				
				try {

					isNull = (Boolean ) isNullMethod.invoke(instance);

				} catch (final IllegalAccessException e) {

					throw new RuntimeException(e);

				} catch (final InvocationTargetException e) {

					throw new RuntimeException(e.getTargetException());
				}
				
				if (isNull) {
					continue;
				}
			}

			try {

				accessorMethod.invoke(instance);

			} catch (final IllegalAccessException e) {

				throw new RuntimeException(e);

			} catch (final InvocationTargetException e) {

				reportBuilder.errorWhileInvokingAccessor(accessorMethod, e);
			}
		}

		reportBuilder.endEntryCheck();
	}
}
