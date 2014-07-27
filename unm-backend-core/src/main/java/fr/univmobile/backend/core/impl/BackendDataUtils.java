package fr.univmobile.backend.core.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.capitalize;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import fr.univmobile.backend.core.BackendDataSource;
import fr.univmobile.backend.core.Entry;
import fr.univmobile.backend.core.EntryBuilder;
import fr.univmobile.backend.core.Support;

abstract class BackendDataUtils {

	public static <E extends Entry, EB extends EntryBuilder<E>> Class<E> getDataClass(
			final Class<? extends BackendDataSource<E, EB>> dataSourceClass) {

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

	public static <E extends Entry, EB extends EntryBuilder<E>> Class<? extends EB> getBuilderClass(
			final Class<? extends BackendDataSource<E, EB>> dataSourceClass) {

		final Support support = getInheritedAnnotation(dataSourceClass,
				Support.class);

		@SuppressWarnings("unchecked")
		final Class<? extends EB> type = (Class<? extends EB>) support
				.builder();

		return type;
	}

	public static Object getAttribute(final Entry entry,
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
}
