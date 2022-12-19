package com.skunkworks.fastorm.processor.dao;

import com.skunkworks.fastorm.annotations.Dao;
import com.skunkworks.fastorm.parser.query.Query;
import com.skunkworks.fastorm.processor.AbstractGenerator;
import com.skunkworks.fastorm.processor.dao.template.FieldData;
import com.skunkworks.fastorm.processor.dao.template.MethodData;
import com.skunkworks.fastorm.processor.dao.template.MethodType;
import com.skunkworks.fastorm.processor.dao.template.QueryParameter;
import com.skunkworks.fastorm.processor.tool.Tools;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.skunkworks.fastorm.parser.QueryLexer;
import org.skunkworks.fastorm.parser.QueryParser;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Column;
import javax.persistence.Id;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * stole on 19.02.17.
 */
public class DaoGenerator extends AbstractGenerator {
    private static final String CLASS_SUFIX = "Generated";

    private final Set<String> additionalImports = new HashSet<>();

    public DaoGenerator(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    public void generateDao(Element annotatedElement) throws Exception {
        AnnotationMirror daoAnnotation =
                Tools.findAnnotation(annotatedElement.getAnnotationMirrors(), Dao.class);

        AnnotationValue daoValue = Tools.getAnnotationData(daoAnnotation, "value");

        if (daoValue == null) {
            //no value, nothing to do.
            return;
        }

        TypeMirror daoMirror = (TypeMirror) daoValue.getValue();
        TypeElement daoValueElement = processingEnv.getElementUtils().getTypeElement(daoMirror.toString());

        if (Tools.isFromSamePackage(annotatedElement, daoValueElement)) {
            additionalImports.add(daoValueElement.getQualifiedName().toString());
        }

        List<FieldData> fields = new ArrayList<>();
        int fieldIndex = 1;
        for (Element enclosedElement : daoValueElement.getEnclosedElements()) {
            String name = enclosedElement.getSimpleName().toString();
            if (isValidField(enclosedElement, name)) {

                fields.add(processField(enclosedElement, name, fieldIndex));
                fieldIndex++;
            }
        }

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
        String className = getGeneratedClassName(interfaceName);

        Map<String, Object> context = new HashMap<>();

        PackageElement packageElement = (PackageElement) annotatedElement.getEnclosingElement();

        context.put("packageName", packageElement.getQualifiedName().toString());
        context.put("interfaceName", interfaceName);
        context.put("className", className);
        String entityName = daoValueElement.getSimpleName().toString();
        context.put("entityName", entityName);

        context.put("fields", fields);

        FieldData idField = fields.stream().
                filter(FieldData::isId).
                findFirst().orElseGet(() -> fields.stream().
                filter(field -> "id".equalsIgnoreCase(field.getName())).
                findFirst().
                orElseThrow(() -> new RuntimeException("Id field not found")));
        context.put("idField", idField);

        context.put("queryMethods", queryMethods);
        context.put("queryListMethods", queryListMethods);
        context.put("storedProcedureMethods", storedProcedureMethods);
        context.put("unrecognizedMethods", unrecognizedMethods);
        context.put("additionalImports", additionalImports);

        String selectedColumns = fields.stream().
                map(FieldData::getColumnName).
                collect(Collectors.joining(", "));
        context.put("selectColumns", selectedColumns);

        write(className, "dao/dao.ftl", context);
    }

    public static String getGeneratedClassName(String interfaceName) {
        return interfaceName + CLASS_SUFIX;
    }

    private boolean isValidField(Element enclosedElement, String name) {
        if ("DEFAULT_VALUE".equals(name))
            return false;

        if (!enclosedElement.getKind().isField())
            return false;

        //Skip transient fields
        return !enclosedElement.getModifiers().contains(Modifier.TRANSIENT);
    }

    private MethodData processMethod(Element methodElement) {
        ExecutableElement method = (ExecutableElement) methodElement;
        MethodData methodData = new MethodData();
        methodData.setName(method.getSimpleName().toString());

        //Return type
        //warn(method.getReturnType().toString());
        //warn(method.getReturnType().getKind().toString());

        TypeElement returnTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(method.getReturnType());
        DeclaredType declaredReturnType = (DeclaredType) method.getReturnType();
        methodData.setReturnType(getTypeString(returnTypeElement, declaredReturnType));

        //method parameters
        ArrayList<String> parameters = new ArrayList<>();
        List<QueryParameter> queryParameters = new ArrayList<>();
        int index = 0;
        for (VariableElement param : method.getParameters()) {
            TypeElement paramElement = processingEnv.getElementUtils().getTypeElement(param.asType().toString());
            String methodParameterName = param.getSimpleName().toString();
            parameters.add(paramElement.getSimpleName().toString() + " " + methodParameterName);

            //warn("Parameter:" + paramElement.getSimpleName().toString() + ", " + paramElement.asType().toString());
            //Tip
            String queryParameterType = Tools.getRecordsetType(param, messager);
            queryParameters.add(new QueryParameter(++index, methodParameterName, queryParameterType));
        }
        //warn(parameters.toString());
        methodData.setParameters(String.join(", ", parameters));
        methodData.setQueryParameters(queryParameters);

        //query method
        processQueryMethod(methodData, declaredReturnType);
        return methodData;
    }

    private void processQueryMethod(MethodData methodData, DeclaredType declaredReturnType) {
        final InputStream is = new ByteArrayInputStream(methodData.getName().getBytes(StandardCharsets.UTF_8));
        try {
            final CharStream inputStream = CharStreams.fromStream(is);
            // Create an ExprLexer that feeds from that stream
            final QueryLexer lexer = new QueryLexer(inputStream);
            // Create a stream of tokens fed by the lexer
            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            // Create a parser that feeds off the token stream
            final QueryParser parser = new QueryParser(tokens);
            // Begin parsing at rule query
            final QueryParser.QueryContext queryContext = parser.query();

//            ParseTree tree = parser.query();
//            ParseTreeWalker walker = new ParseTreeWalker();
//            walker.walk( new HelloWalker(), tree );
//
//            QueryBaseListener
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
                StringBuilder query = new StringBuilder();
                query.append(String.join(" ", whereSegmentList));
                if (ctx.getOrderByParam() != null) {
                    query.append(" order by ").append(ctx.getOrderByParam());
                }
                methodData.setQuery(query.toString());
                if (declaredReturnType.getTypeArguments().size() > 0) {
                    methodData.setType(MethodType.QUERY_LIST);
                } else {
                    methodData.setType(MethodType.QUERY_SINGLE);
                }

                //TODO - fix prepared statement parameters and orderBy
//                methodData.setQueryParameters(queryParameters);
            } else {
                warn("Method " + methodData.getName() + " has not been succesfully parsed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FieldData processField(Element field, String name, int fieldIndex) {

        String columnName = getColumnName(field, name);
        String getterPrefix = field.asType().getKind().equals(TypeKind.BOOLEAN) ? "is" : "get";
        String capitalizedName = Tools.capitalizeFirstLetter(name);
        String getter = getterPrefix + capitalizedName;
        String setter = "set" + capitalizedName;
        String recordsetType = Tools.getRecordsetType(field, messager);

        final String type = getFieldType(field, additionalImports);

        Id idAnnotation = field.getAnnotation(Id.class);
        boolean isId = idAnnotation != null;

        return new FieldData(fieldIndex, name, type, columnName, getter, setter, recordsetType, isId);
    }

    private String getColumnName(Element field, String name) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        //warn("columnAnnotationName:" + columnAnnotationName);
        return (columnAnnotation != null && !"".equals(columnAnnotation.name())) ? columnAnnotation.name() : name;
    }
}