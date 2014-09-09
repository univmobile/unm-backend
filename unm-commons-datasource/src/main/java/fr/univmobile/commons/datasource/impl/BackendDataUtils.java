package fr.univmobile.commons.datasource.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.capitalize;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import fr.univmobile.commons.datasource.RevDataSource;
import fr.univmobile.commons.datasource.Entry;
import fr.univmobile.commons.datasource.EntryBuilder;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.Support;

abstract class BackendDataUtils {

	public static <E extends Entry<E>, EB extends EntryBuilder<E>> Class<E> getDataClass(
			final Class<? extends RevDataSource<E, EB>> dataSourceClass) {

		final Support support = getInheritedAnnotation(dataSourceClass,
				Support.class);

		@SuppressWarnings("unchecked")
		final Class<E> type = (Class<E>) support.data();

		return type;
	}

	public static <A extends Annotation> A getInheritedAnnotation(
			final Class<?> clazz, final Class<A> annotationClass) {

		final A annotation = recursiveGetAnnotation(clazz, annotationClass);

		if (annotation == null) {
			throw new IllegalStateException("Class should be @"
					+ annotationClass.getSimpleName() + "-annotated: "
					+ clazz.getName());
		}

		return annotation;
	}

	@Nullable
	private static <A extends Annotation> A recursiveGetAnnotation(
			@Nullable final Class<?> clazz, final Class<A> annotationClass) {

		if (clazz == null) {
			return null;
		}

		final A annotation = clazz.getAnnotation(annotationClass);

		if (annotation != null) {
			return annotation;
		}

		final A superclassAnnotation = recursiveGetAnnotation(
				clazz.getSuperclass(), annotationClass);

		if (superclassAnnotation != null) {
			return superclassAnnotation;
		}

		for (final Class<?> i : clazz.getInterfaces()) {

			final A interfaceAnnotation = recursiveGetAnnotation(i,
					annotationClass);

			if (interfaceAnnotation != null) {
				return interfaceAnnotation;
			}
		}

		return null;
	}

	public static <E extends Entry<E>, EB extends EntryBuilder<E>> Class<? extends EB> getBuilderClass(
			final Class<? extends RevDataSource<E, EB>> dataSourceClass) {

		final Support support = getInheritedAnnotation(dataSourceClass,
				Support.class);

		@SuppressWarnings("unchecked")
		final Class<? extends EB> type = (Class<? extends EB>) support
				.builder();

		return type;
	}

	private static Object getAttribute(final Entry<?> entry,
			final String attributeName) {

		checkNotNull(entry, "entry");

		final String methodName = "get" + capitalize(attributeName);

		try {

			return entry.getClass().getMethod(methodName).invoke(entry);

		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <E extends Entry<E>, EB extends EntryBuilder<E>> String getPrimaryKeyName(
			final Class<? extends RevDataSource<E, EB>> dataSourceClass) {

		final String[] primaryKey = getInheritedAnnotation(dataSourceClass,
				PrimaryKey.class).value();

		if (primaryKey.length == 0) {
			throw new IllegalArgumentException(
					"DataSource class declares an empty primary key: "
							+ dataSourceClass.getName());
		}

		if (primaryKey.length != 1) {
			throw new NotImplementedException(
					"primaryKey is not unique for DataSource class: "
							+ dataSourceClass.getName());
		}

		return primaryKey[0];
	}

	public static <E extends Entry<E>, EB extends EntryBuilder<E>> String getPrimaryKeyValue(
			final E data,
			final Class<? extends RevDataSource<E, EB>> dataSourceClass) {

		final String[] primaryKey = getInheritedAnnotation(dataSourceClass,
				PrimaryKey.class).value();

		if (primaryKey.length == 0) {
			throw new IllegalArgumentException(
					"DataSource class declares an empty primary key: "
							+ dataSourceClass.getName());
		}

		final StringBuilder sb = new StringBuilder();

		for (final String attributeName : primaryKey) {

			final Object value = BackendDataUtils.getAttribute(data,
					attributeName);

			sb.append('_').append(value);
		}

		return sb.toString().substring(1);
	}
}
