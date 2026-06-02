package org.oathforge.toolkit.resilience;

@FunctionalInterface
interface ThrowingSupplier<T> {

	T get() throws Throwable;
}
