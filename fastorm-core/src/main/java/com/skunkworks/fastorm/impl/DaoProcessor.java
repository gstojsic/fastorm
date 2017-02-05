package com.skunkworks.fastorm.impl;

import com.skunkworks.fastorm.annotations.Dao;
import com.skunkworks.fastorm.parser.query.Query;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.skunkworks.fastorm.parser.QueryLexer;
import org.skunkworks.fastorm.parser.QueryParser;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Column;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * stole on 29.01.17.
 */
public class DaoProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Dao.class)) {
            // Check if a class has been annotated with @Dao
            if (annotatedElement.getKind() != ElementKind.INTERFACE) {
                error(annotatedElement, "Only interfaces can be annotated with @%s", Dao.class.getSimpleName());
                return true; // Exit processing
            }

            try {
                generateDao(annotatedElement);
            } catch (Exception e) {
//                warn("banana" + e.getMessage());
//                error(e.getMessage());
//                //error(null, e.getMessage());
//                return true;
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void generateDao(Element annotatedElement) throws Exception {
        final String daoName = Dao.class.getName();
        AnnotationValue daoValue = null;
        for (AnnotationMirror am : annotatedElement.getAnnotationMirrors()) {
            if (daoName.equals(am.getAnnotationType().toString())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    if ("value".equals(entry.getKey().getSimpleName().toString())) {
                        daoValue = entry.getValue();
                        break;
                    }
                }
            }
        }

        TypeMirror daoMirror = (TypeMirror) daoValue.getValue();
        TypeElement daoValueElement = processingEnv.getElementUtils().getTypeElement(daoMirror.toString());

        Set<String> additionalImports = new HashSet<>();

        //Interface Methods
        List<MethodData> queryMethods = new ArrayList<>();
        List<MethodData> queryListMethods = new ArrayList<>();
        List<MethodData> storedProcedureMethods = new ArrayList<>();
        List<MethodData> unrecognizedMethods = new ArrayList<>();

        for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
            if (ElementKind.METHOD.equals(enclosedElement.getKind())) {
                MethodData methodData = processMethod(enclosedElement);
                if (MethodType.QUERY_SINGLE.equals(methodData.getType())) {
                    queryMethods.add(methodData);
                } else if (MethodType.QUERY_LIST.equals(methodData.getType())) {
                    queryListMethods.add(methodData);
                } else if (MethodType.STORED_PROCEDURE.equals(methodData.getType())) {
                    storedProcedureMethods.add(methodData);
                } else {
                    unrecognizedMethods.add(methodData);
                }
            }
        }

        String interfaceName = annotatedElement.getSimpleName().toString();
        String className = interfaceName + "Impl";
        JavaFileObject jfo = filer.createSourceFile(className);

        Writer writer = jfo.openWriter();

        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());

        VelocityEngine ve = new VelocityEngine(props);
        ve.init();

        VelocityContext context = new VelocityContext();

        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        //warn("package element:" + packageElement.toString());
        //warn("daoValueElement element:" + daoValueElement.toString());

        context.put("packageName", packageElement.getQualifiedName().toString());
        context.put("interfaceName", interfaceName);
        context.put("className", className);
        String entityName = daoValueElement.getSimpleName().toString();
        context.put("entityName", entityName);

        List<FieldData> fields = new ArrayList<>();
        int fieldIndex = 1;
        for (Element enclosedElement : daoValueElement.getEnclosedElements()) {
            String name = enclosedElement.getSimpleName().toString();
            if (enclosedElement.getKind().isField() && !"DEFAULT_VALUE".equals(name)) {
                fields.add(processField(enclosedElement, name, fieldIndex));
                fieldIndex++;
            }
        }
        context.put("fields", fields);
        context.put("queryMethods", queryMethods);
        context.put("queryListMethods", queryListMethods);
        context.put("storedProcedureMethods", storedProcedureMethods);
        context.put("unrecognizedMethods", unrecognizedMethods);
        context.put("additionalImports", additionalImports);

        String selectedColumns = fields.stream().
                map(FieldData::getColumnName).
                collect(Collectors.joining(", "));
        context.put("selectColumns", selectedColumns);

        Template vt = ve.getTemplate("velocity/dao.vm");

        vt.merge(context, writer);

        writer.close();
    }

    private MethodData processMethod(Element methodElement) {
        ExecutableElement method = (ExecutableElement) methodElement;
        MethodData methodData = new MethodData();
        methodData.setName(method.getSimpleName().toString());

        //Return type
        warn(method.getReturnType().toString());
        warn(method.getReturnType().getKind().toString());

        TypeElement returnTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
        DeclaredType declaredReturnType = (DeclaredType) method.getReturnType();
        //methodData.setReturnType(returnTypeElement.getSimpleName().toString());
        methodData.setReturnType(getTypeString(returnTypeElement, declaredReturnType));

        //method parameters
        ArrayList<String> parameters = new ArrayList<>();
        for (VariableElement param : method.getParameters()) {
            TypeElement paramElement = processingEnv.getElementUtils().getTypeElement(param.asType().toString());
            parameters.add(paramElement.getSimpleName().toString() + " " + param.getSimpleName());
        }
        //warn(parameters.toString());
        methodData.setParameters(String.join(", ", parameters));

        //query method
        processQueryMethod(methodData, declaredReturnType);
        return methodData;
    }

    private void processQueryMethod(MethodData methodData, DeclaredType declaredReturnType) {
        final InputStream is = new ByteArrayInputStream(methodData.getName().getBytes(Charset.forName("UTF-8")));
        try {
            final ANTLRInputStream inputStream = new ANTLRInputStream(is);
            // Create an ExprLexer that feeds from that stream
            final QueryLexer lexer = new QueryLexer(inputStream);
            // Create a stream of tokens fed by the lexer
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            // Create a parser that feeds off the token stream
            final QueryParser parser = new QueryParser(tokens);
            // Begin parsing at rule query
            final QueryParser.QueryContext queryContext = parser.query();
            if (parser.getNumberOfSyntaxErrors() == 0) {
                Query ctx = queryContext.ctx;
                ArrayList<String> whereSegmentList = new ArrayList<>();
                List<String> params = ctx.getQueryParams();
                List<String> operators = ctx.getQueryOperators();
                whereSegmentList.add(params.get(0) + " = ?");
                for (int i = 1; i < params.size(); i++) {
                    whereSegmentList.add(operators.get(i - 1));
                    whereSegmentList.add(params.get(i) + " = ?");
                }
                methodData.setQuery(String.join(" ", whereSegmentList));
                if (declaredReturnType.getTypeArguments().size() > 0) {
                    methodData.setType(MethodType.QUERY_LIST);
                } else {
                    methodData.setType(MethodType.QUERY_SINGLE);
                }

                //TODO - fix prepared statement parameters and orderBy
            } else {
                warn("Method " + methodData.getName() + " has not been succesfully parsed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTypeString(TypeElement returnTypeElement, DeclaredType declaredReturnType) {
//        for (TypeMirror tpe : declaredReturnType.getTypeArguments()) {
//            warn("Type Parameter el:" + tpe.toString());
//        }
        //warn("Type Parameter:" + returnTypeElement.getTypeParameters().toString());
        //warn(returnTypeElement.getSimpleName().toString());
        //warn(returnTypeElement.toString());

        if (declaredReturnType.getTypeArguments().size() > 0) {
            String typeParameters = declaredReturnType.getTypeArguments().stream().
                    map(typeMirror -> {
                        TypeElement paramElement = processingEnv.getElementUtils().getTypeElement(typeMirror.toString());
                        return paramElement.getSimpleName().toString();
                        //return typeMirror.toString();
                    }).
                    collect(Collectors.joining(", "));

            return returnTypeElement.getSimpleName().toString() +
                    '<' + String.join(", ", typeParameters) + '>';
        } else {
            return returnTypeElement.getSimpleName().toString();
        }
    }

    private FieldData processField(Element field, String name, int fieldIndex) {
        //warn("element type:" + field.asType());
        //warn("element kind:" + field.asType().getKind());

        String columnName = getColumnName(field, name);
        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String recordsetType = getRecordsetType(field);
        return new FieldData(fieldIndex, name, columnName, getter, setter, recordsetType);
    }

    private String getColumnName(Element field, String name) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnAnnotationName = (columnAnnotation != null && !"".equals(columnAnnotation.name())) ? columnAnnotation.name() : name;
        //warn("columnAnnotationName:" + columnAnnotationName);
        return columnAnnotationName;
    }

    private String getRecordsetType(Element field) {
        if ("java.lang.Long".equals(field.asType().toString())) {
            return "Long";
        } else if ("java.lang.String".equals(field.asType().toString())) {
            return "String";
        }

        error("Unrecognized type:" + field.asType());
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Dao.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void warn(String message) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, message);
    }

    private void error(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
