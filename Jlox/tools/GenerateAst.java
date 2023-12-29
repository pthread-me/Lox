package tools;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException{

//        if(args.length != 1){
//            System.out.println("Usage: generate_tree <output directory>");
//            System.exit(64);
//        }

        String outputDir = "lox";
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
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


        for(String type: types){
            String input[] = type.split(" : ");
            defType(writer, Expr, input[0], input[1]);
        }

        writer.println("\n}");
        writer.close();
    }

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

        //fields
        for(String field: fieldList){
            String line[] = field.split(" ");

            writer.println("\t\t"+line[0]+" "+line[1]+";");
        }

        writer.println("\t}");
    }
}
