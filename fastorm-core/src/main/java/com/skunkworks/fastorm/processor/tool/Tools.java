package com.skunkworks.fastorm.processor.tool;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.math.BigDecimal;

/**
 * stole on 19.02.17.
 */
public enum Tools {
    ;

    public static String getRecordsetType(Element element, Messager messager) {
        if (Long.class.getCanonicalName().equals(element.asType().toString())) {
            return "Long";
        } else if (Integer.class.getCanonicalName().equals(element.asType().toString())) {
            return "Integer";
        } else if (String.class.getCanonicalName().equals(element.asType().toString())) {
            return "String";
        } else if (Boolean.class.getCanonicalName().equals(element.asType().toString())) {
            return "Boolean";
        } else if (Byte.class.getCanonicalName().equals(element.asType().toString())) {
            return "Byte";
        } else if (Float.class.getCanonicalName().equals(element.asType().toString())) {
            return "Float";
        } else if (Double.class.getCanonicalName().equals(element.asType().toString())) {
            return "Double";
        } else if (BigDecimal.class.getCanonicalName().equals(element.asType().toString())) {
            return "BigDecimal";
        }

        error(messager, "Unrecognized type:" + element.asType());
        return null;
    }

    public static void warn(Messager messager, String message) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
    }

    public static void error(Messager messager, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
