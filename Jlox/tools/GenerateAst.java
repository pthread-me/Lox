package tools;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GenerateAst {
    public static void main(String[] args) throws IOException{

        if(args.length != 1){
            System.out.println("Usage: generate_tree <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Ternary : Expr condition, Token first, Expr left, Token second, Expr right",
                "Grouping : Expr expression",
                "Unary    : Token operator, Expr right",
                "Literal  : Object value"
        ));
    }

    private static void defineAst(String outputDir, String Expr, List<String> types) throws IOException{
        String path = outputDir+"/"+Expr+".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package lox;\n");
        writer.println("import java.util.List;\n");
        writer.println("abstract class "+Expr+" {\n");

        defineVisitor(writer, Expr, types);

        //The AST classes
        for(String type: types){
            String input[] = type.split(" : ");
            defType(writer, Expr, input[0], input[1]);
        }

        //base accept and RPN fun
        writer.println("\n\tabstract <R> R accept(Visitor<R> visitor);");
        writer.println("\tabstract <R> R RPN(Visitor<R> visitor);");

        writer.println("\n}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String Expr, List<String> types){
        writer.println("\tinterface Visitor<R> {");

        for(String type : types){
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit"+typeName+Expr+"("+typeName+" "+Expr.toLowerCase()+");");
            writer.println("\t\tR "+typeName+"RPN("+typeName+" "+ Expr.toLowerCase()+");\n");
        }

        writer.println("\t}");
    }

    /**
     * creates the subclasses
     * @param writer
     * @param superclass name of the super class to extend
     * @param type the type/name of the subclass
     * @param fields the private fields in the subclass
     */
    private static void defType(PrintWriter writer, String superclass, String type, String fields){
        writer.println("\tstatic class "+type+" extends "+superclass+" {");
        writer.println("\t\t"+type+"( "+fields+"){");

        String fieldList[] = fields.split(", ");

        //constructor
        for(String field : fieldList){
            String line[] = field.split(" ");
            writer.println("\t\tthis."+line[1]+" = "+line[1]+";");
        }
        writer.println("\t\t}");

        //the accept method in each subclass
        writer.println("\t\t@Override");
        writer.println("\t\t<R> R accept(Visitor<R> visitor){");
        writer.println("\t\treturn visitor.visit"+type.trim()+superclass.trim()+"(this);\n}");

        //the accept method in each subclass
        writer.println("\t\t@Override");
        writer.println("\t\t<R> R RPN(Visitor<R> visitor){");
        writer.println("\t\treturn visitor."+type.trim()+"RPN(this);\n}");

        //fields
        for(String field: fieldList){
            String line[] = field.split(" ");

            writer.println("\t\t"+line[0]+" "+line[1]+";");
        }


        writer.println("\t}");
    }
}
