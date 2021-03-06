package com.skunkworks.fastorm.processor.tool;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * stole on 19.02.17.
 */
public enum Tools {
    ;

    public static String getRecordsetType(Element element, Messager messager) {
        if (Long.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.LONG) {
            return Long.class.getSimpleName();
        } else if (Integer.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.INT) {
            return "Int";
        } else if (Boolean.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.BOOLEAN) {
            return Boolean.class.getSimpleName();
        } else if (String.class.getCanonicalName().equals(element.asType().toString())) {
            return String.class.getSimpleName();
        } else if (Float.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.FLOAT) {
            return Float.class.getSimpleName();
        } else if (Double.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.DOUBLE) {
            return Double.class.getSimpleName();
        } else if (Short.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.SHORT) {
            return Short.class.getSimpleName();
        } else if (Byte.class.getCanonicalName().equals(element.asType().toString()) || element.asType().getKind() == TypeKind.BYTE) {
            return Byte.class.getSimpleName();
        } else if (BigDecimal.class.getCanonicalName().equals(element.asType().toString())) {
            return BigDecimal.class.getSimpleName();
        }

        error(messager, "Unrecognized type:" + element.asType());
        return null;
    }

    public static boolean isFromSamePackage(Element leftElement, Element rightElement) {
        return rightElement.getEnclosingElement().getKind() == ElementKind.PACKAGE &&
                leftElement.getEnclosingElement().getKind() == ElementKind.PACKAGE &&
                !rightElement.getEnclosingElement().toString().equals(leftElement.getEnclosingElement().toString());
    }

    public static AnnotationValue getAnnotationData(AnnotationMirror annotationMirror, String itemName) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (itemName.equals(entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static AnnotationMirror findAnnotation(List<? extends AnnotationMirror> annotationMirrors, Class<?> annotationClass) {
        return annotationMirrors.stream().
                filter(annotationMirror -> annotationClass.getName().equals(annotationMirror.getAnnotationType().toString())).
                findFirst().orElse(null);
    }

    public static void warn(Messager messager, String message) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
    }

    public static void error(Messager messager, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    public static String capitalizeFirstLetter(String item) {
        return item.substring(0, 1).toUpperCase() + item.substring(1);
    }

    public static String lowercaseFirstLetter(String item) {
        return item.substring(0, 1).toLowerCase() + item.substring(1);
    }
}
