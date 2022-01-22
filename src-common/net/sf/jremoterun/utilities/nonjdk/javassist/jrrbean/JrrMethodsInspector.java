package net.sf.jremoterun.utilities.nonjdk.javassist.jrrbean;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javassist.BaseMethodHandler;

public class JrrMethodsInspector extends BaseMethodHandler {

	private final static Logger log = Logger.getLogger(JrrMethodsInspector.class.getName());

	private final JrrBeanMethods jrrBeanMethods;

	private final Date createDate = new Date();

	private final WeakReference<Thread> creatorThread = new WeakReference<Thread>(Thread.currentThread());

	public static int maxFieldsToStringLength = 160;

	private final boolean first;

	private Object lock = new Object();

	public JrrMethodsInspector(final JrrBeanMethods jrrBeanMethods) {
		this.jrrBeanMethods = jrrBeanMethods;
		first = jrrBeanMethods.getObject() == null;
		handleHashCode = true;
	}

	public Object invoke2(Object beanObject, Method methodFromBean, Method superMethod, Object[] args)
			throws Throwable {
		final String methodName = methodFromBean.getName();
		log.fine(methodName);
		final ArrayList<String> al = JrrBeanMaker.buildKey(methodFromBean);
		final Method methodToInvoke = jrrBeanMethods.getMethodsMap().get(al);
		if ("equals".equals(methodName) && args.length == 1) {
			if (beanObject == args[0]) {
				return true;
			}
		} else if (args.length == 0) {
			if ("toString".equals(methodName)) {
				if (methodToInvoke.getDeclaringClass() == Object.class) {
					log.finer("invoking specified toString method");
					return fieldsToString();
				}
				log.finer("invoking super toString method");
			}
		}
		if (methodToInvoke == null) {
			throw new NoSuchMethodError(al.toString());
		}
		final Object beanValue = jrrBeanMethods.getObject();
		if (beanValue == beanObject) {
			return superMethod.invoke(beanObject, args);
		}
		try {
			log.finer("invoking method from different classloads");
			final Object result = methodToInvoke.invoke(beanValue, args);
			return result;
		} catch (final InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public final static StringBuffer fieldsToString(final Object object, Class class1, final int maxLength)
			throws IllegalAccessException {
		final StringBuffer result = new StringBuffer();
		while (true) {
			if (class1 == Object.class) {
				if (result.length() > 2) {
					result.setLength(result.length() - 2);
				}
				break;
			}
			fieldsToString(object, class1, result, maxLength);
			class1 = class1.getSuperclass();
			if (result.length() > maxLength) {
				result.append(" ..");
				break;
			}
		}

		return result;
	}

	public static <T> void fieldsToString(final Object object, final Class clazz, final StringBuffer result,
			final int maxLength) throws IllegalAccessException {
		// boolean first=true;
		for (final Field field : JrrClassUtils.getDeclaredFields(clazz)) {
			final int modif = field.getModifiers();
			if (!Modifier.isStatic(modif)) {
				field.setAccessible(true);
				final Object value = field.get(object);
				result.append(field.getName()).append('=').append(value).append(", ");
				if (result.length() > maxLength) {
					return;
				}
			}
		}
	}

	public String fieldsToString() {
		try {
			return fieldsToString(jrrBeanMethods.getObject(), jrrBeanMethods.getObject().getClass().getSuperclass(),
					maxFieldsToStringLength).toString();
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public JrrBeanMethods getJrrBeanMethods() {
		return jrrBeanMethods;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public boolean isFirst() {
		return first;
	}

	public Thread getCreatorThread() {
		return creatorThread.get();
	}

}
