package io.swagger.jaxrs2;

import io.swagger.jaxrs2.resources.ApiExampleResource;
import io.swagger.jaxrs2.resources.SimpleMethods;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;


public class ReaderTest {

    private final String OPERATION_SUMMARY = "Operation Summary";
    private final String OPERATION_DESCRIPTION = "Operation Description";

    @Test(description = "scan methods")
    public void scanMethods() {
        Method[] methods = SimpleMethods.class.getMethods();
        Reader reader = new Reader(new OpenAPI());
        for (final Method method : methods) {
            if (isValidRestPath(method)) {
                Operation operation = reader.parseMethod(method);
                Assert.assertNotNull(operation);
            }
        }
    }

    @Test(description = "Get a Summary and Description")
    public void getSummaryAndDescription() {
        Method[] methods = ApiExampleResource.class.getMethods();
        Reader reader = new Reader(new OpenAPI());
        Operation operation = reader.parseMethod(methods[0]);
        Assert.assertNotNull(operation);
        Assert.assertEquals(OPERATION_SUMMARY, operation.getSummary());
        Assert.assertEquals(OPERATION_DESCRIPTION, operation.getDescription());
    }

    @Test(description = "Deprecated Method")
    public void deprecatedMethod() {
        Method[] methods = ApiExampleResource.class.getMethods();
        Reader reader = new Reader(new OpenAPI());

        Operation defaultNonDeprecatedOperation = reader.parseMethod(methods[0]);
        Assert.assertNotNull(defaultNonDeprecatedOperation);
        Assert.assertFalse(defaultNonDeprecatedOperation.getDeprecated());

        Operation deprecatedOperation = reader.parseMethod(methods[1]);
        Assert.assertNotNull(deprecatedOperation);
        Assert.assertTrue(deprecatedOperation.getDeprecated());
    }

    private Boolean isValidRestPath(Method method) {
        for (Class<? extends Annotation> item : Arrays.asList(GET.class, PUT.class, POST.class, DELETE.class,
                OPTIONS.class, HEAD.class)) {
            if (method.getAnnotation(item) != null) {
                return true;
            }
        }
        return false;
    }
}
