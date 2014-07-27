package fr.univmobile.backend.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Support {

	Class<? extends Entry> data();

	Class<? extends EntryBuilder<?>> builder();
}
