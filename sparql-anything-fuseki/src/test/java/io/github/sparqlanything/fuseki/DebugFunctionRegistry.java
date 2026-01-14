package io.github.sparqlanything.fuseki;

import org.apache.jena.query.ARQ;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.function.FunctionFactory;
import io.github.sparqlanything.engine.FacadeX;
import io.github.sparqlanything.model.Triplifier;

import java.lang.reflect.Field;

public class DebugFunctionRegistry {
    public static void main(String[] args) throws Exception {
        QC.setFactory(ARQ.getContext(), FacadeX.ExecutorFactory);
        FunctionRegistry registry = FunctionRegistry.get();
        
        String uri = Triplifier.FACADE_X_CONST_NAMESPACE_IRI + "forward";
        FunctionFactory factory = registry.get(uri);
        
        System.out.println("URI: " + uri);
        System.out.println("Factory object: " + factory);
        System.out.println("Factory class: " + factory.getClass().getName());
        
        // Try to extract the function class from FunctionFactoryAuto using reflection
        // FunctionFactoryAuto is package-private in Jena, so we check by class name
        if (factory.getClass().getSimpleName().equals("FunctionFactoryAuto")) {
            // Use reflection to get the function class field
            Field fnClassField = factory.getClass().getDeclaredField("fnClass");
            fnClassField.setAccessible(true);
            Class<?> functionClass = (Class<?>) fnClassField.get(factory);
            
            System.out.println("Extracted function class: " + functionClass.getName());
            System.out.println("Has FXFunctionDoc? " + (functionClass.getAnnotation(io.github.sparqlanything.model.annotations.FXFunctionDoc.class) != null));
            
            io.github.sparqlanything.model.annotations.FXFunctionDoc doc = 
                functionClass.getAnnotation(io.github.sparqlanything.model.annotations.FXFunctionDoc.class);
            if (doc != null) {
                System.out.println("Description: " + doc.description());
                System.out.println("Example: " + doc.example());
            }
        }
    }
}
