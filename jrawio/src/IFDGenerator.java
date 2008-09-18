/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 *******************************************************************************
 *
 * $Id: IFDGenerator.java 162 2008-09-18 19:33:44Z fabriziogiudici $
 *
 ******************************************************************************/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: IFDGenerator.java 162 2008-09-18 19:33:44Z fabriziogiudici $
 *
 ******************************************************************************/
public class IFDGenerator
  {
    public static void main (String[] args)
      {
        String baseDir = args[0];
        String targetDir = args[1];
        build(baseDir, targetDir, "TIFF.properties", "it.tidalwave.imageio.tiff", "IFDGenerated", "IFDSupport", "TIFF", -2517805638960118907L);
        build(baseDir, targetDir, "CanonCRWMakerNote.properties", "it.tidalwave.imageio.crw", "CanonCRWMakerNoteSupport", "IFDSupport",
            "CanonCRW", 3251780563890118907L);
      }

    /*******************************************************************************
     * 
     * @param baseDir
     * 
     *******************************************************************************/

    private static void build (String baseDir,
                               String targetDir,
                               String fileName,
                               String packageName,
                               String className,
                               String baseClassName,
                               String registryName,
                               long serialVersionUID)
      {
        String s = null;

        try
          {
            File file = new File(baseDir + "/" + fileName);
            
            if (!file.exists())
              {
                System.err.println("FILE NOT FOUND: " + file.getAbsolutePath());
                return;
              }
            
            String targetName = "/" + packageName.replace('.', '/') + "/" + className + ".java";
            File targetFile = new File(targetDir + targetName);
            targetFile.getParentFile().mkdirs();
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(targetFile));

            StringWriter sw2 = new StringWriter();
            PrintWriter pw2 = new PrintWriter(sw2);
            StringWriter sw3 = new StringWriter();
            PrintWriter pw3 = new PrintWriter(sw3);

            pw.println("package " + packageName + ";");
            pw.println();
            pw.println("import it.tidalwave.imageio.raw.Directory;");
            pw.println("import it.tidalwave.imageio.raw.TagRational;");
            pw.println("import it.tidalwave.imageio.raw.TagRegistry;");
            pw.println("import it.tidalwave.imageio.tiff.IFDSupport;");

            pw.println("/** This class is auto-generated from " + fileName + ". */");
            pw.println();
            
            if (className.equals("IFDSupport"))
              {
                pw.print("public ");
              }
            
            pw.println("class " + className + " extends " + baseClassName);
            pw.println("  {");
            pw.println("    private final static long serialVersionUID = " + serialVersionUID + "L;");

            pw.println("    public " + className + "()");
            pw.println("      {");
            pw.println("        super(TagRegistry.getRegistry(\"" + registryName + "\"));");
            pw.println("      }");
            pw.println();

            for (;;)
              {
                s = br.readLine();

                if (s == null)
                  {
                    break;
                  }

                s = s.trim();

                int i = s.indexOf('#');

                if (i >= 0)
                  {
                    s = s.substring(0, i);
                  }

                if ("".equals(s))
                  {
                    continue;
                  }

                StringTokenizer st = new StringTokenizer(s, "\t");
                String xxx = st.nextToken().trim();
                int code = xxx.startsWith("0x") ? Integer.parseInt(xxx.substring(2), 16) : Integer.parseInt(xxx);
                String tagName = strip(st.nextToken().trim());
                String type = st.nextToken().trim().toLowerCase();
                String javaType = type.toLowerCase();
                String uncapitalizedTagName = uncapitalized(tagName);
                String capitalizedTagName = capitalized(tagName);

                String tag = x(code, tagName, pw3, registryName);

                if (type.equals("ascii"))
                  {
                    javaType = "String";
                  }

                else if (type.equals("rational"))
                  {
                    javaType = "TagRational";
                  }

                else if (type.equals("srational"))
                  {
                    javaType = "TagRational";
                  }

                else if (type.equals("rational[]"))
                  {
                    javaType = "TagRational[]";
                  }

                else if (type.equals("srational[]"))
                  {
                    javaType = "TagRational[]";
                  }

                else if (type.equals("byte"))
                  {
                    javaType = "int";
                  }

                else if (type.equals("long"))
                  {
                    javaType = "int";
                  }

                else if (type.equals("short"))
                  {
                    javaType = "int";
                  }

                else if (type.equals("sshort"))
                  {
                    javaType = "int";
                  }

                else if (type.equals("byte[]"))
                  {
                    javaType = "byte[]";
                  }

                else if (type.equals("long[]"))
                  {
                    javaType = "int[]";
                  }

                else if (type.equals("short[]"))
                  {
                    javaType = "int[]";
                  }

                else if (type.equals("sshort[]"))
                  {
                    javaType = "int[]";
                  }

                else if (type.equals("undefined"))
                  {
                    javaType = "int";
                  }

                else if (type.equals("undefined[]"))
                  {
                    javaType = "byte[]";
                  }

                String javaTypeForGetter = javaType;

                if (javaTypeForGetter.equals("double"))
                  {
                    javaTypeForGetter = "Double";
                  }

                if (javaTypeForGetter.equals("float"))
                  {
                    javaTypeForGetter = "Float";
                  }

                if (javaTypeForGetter.equals("byte"))
                  {
                    javaTypeForGetter = "Byte";
                  }

                if (javaTypeForGetter.equals("byte[]"))
                  {
                    javaTypeForGetter = "Bytes";
                  }

                if (javaTypeForGetter.equals("double[]"))
                  {
                    javaTypeForGetter = "Doubles";
                  }

                if (javaTypeForGetter.equals("float[]"))
                  {
                    javaTypeForGetter = "Floats";
                  }

                if (javaTypeForGetter.equals("int"))
                  {
                    javaTypeForGetter = "Integer";
                  }

                if (javaTypeForGetter.equals("int[]"))
                  {
                    javaTypeForGetter = "Integers";
                  }

                if (javaTypeForGetter.equals("short"))
                  {
                    javaTypeForGetter = "Short";
                  }

                if (javaTypeForGetter.equals("short[]"))
                  {
                    javaTypeForGetter = "Shorts";
                  }

                if (javaTypeForGetter.equals("TagRational"))
                  {
                    javaTypeForGetter = "Rational";
                  }

                if (javaTypeForGetter.equals("TagRational[]"))
                  {
                    javaTypeForGetter = "Rationals";
                  }

                boolean hasEnum = false;

                if (st.hasMoreElements())
                  {
                    String next = st.nextToken().trim();

                    if (next.startsWith("enum:"))
                      {
                        st = new StringTokenizer(next, ":=,");
                        st.nextToken(); // skip enum:
                        javaType = capitalizedTagName;
                        hasEnum = true;

                        StringBuffer buffer4 = new StringBuffer();
                        pw.println("/** This class represents an enumeration for values of " + tagName + ". */");
                        pw.println("    public static class " + capitalizedTagName + " extends Directory.Enumeration");
                        pw.println("       {");
                        pw.println("         private " + capitalizedTagName + " (int value, String name)");
                        pw.println("           {");
                        pw.println("             super(value, name);");
                        pw.println("           }");

                        for (;;)
                          {
                            String value = st.nextToken().trim();
                            //String equal = st.nextToken();

                            //if (!equal.equals("="))
                            //  {
                            //    throw new RuntimeException("= expected");
                            //  }

                            String name = st.nextToken().trim();
                            String name2 = name.replace(' ', '_').replace('-', '_').replace('.', '_').toUpperCase();

                            pw.println("         public final static " + capitalizedTagName + " " + name2 + " = new "
                                + capitalizedTagName + "(" + value + ", \"" + name + "\");");

                            if (type.equals("undefined"))
                              type = "byte";
                            buffer4.append("             if (value == (" + type + ")" + value + ") return " + name2
                                + ";\n");

                            if (!st.hasMoreTokens())
                              {
                                break;
                              }
                          }

                        pw.println("         public static " + capitalizedTagName + " getInstance(int value)");
                        pw.println("           {");
                        pw.println(buffer4);
                        pw.println("             return new " + capitalizedTagName + "(value, \"unknown\");");
                        pw.println("           }");

                        pw.println("       }");
                      }
                  }

                //            pw.println("    private boolean " + uncapitalizedTagName + "present;");
                //            pw.println("    private " + javaType + " " + uncapitalizedTagName + ";");
                //            pw.println();
                pw.println("/** Returns true if the tag " + tagName + " is contained in this IFD. */");
                pw.println("    public boolean is" + capitalizedTagName + "Available()");
                pw.println("      {");
                pw.println("        return containsTag(" + tag + ");");
                pw.println("      }");
                pw.println();

                /*
                 pw.println("    public void set" + capitalizedTagName + " (" + javaType + " " + uncapitalizedTagName + ")");
                 pw.println("      {");
                 pw.println("        this." + uncapitalizedTagName + " = " + uncapitalizedTagName + ";");
                 pw.println("      }");
                 pw.println();
                 */
                pw.println("/** Returns the value of the tag " + tagName + ". */");
                pw.println("    public " + javaType + " get" + capitalizedTagName + "() ");
                pw.println("      {");

                if (!hasEnum)
                  {
                    pw.println("        return get" + javaTypeForGetter + "(" + tag + ");");
                  }
                else
                  {
                    pw.println("        return " + capitalizedTagName + ".getInstance(get" + javaTypeForGetter + "("
                        + tag + "));");
                  }

                pw.println("      }");
                pw.println();

                pw2.println("        if (is" + capitalizedTagName + "Available())");
                pw2.println("          {");

                String toString = "get" + capitalizedTagName + "()";

                if (javaType.endsWith("[]"))
                  {
                    toString = "toString(" + toString + ")";
                  }

                pw2.println("            buffer.append(\"\\t" + uncapitalizedTagName + ": \" + " + toString
                    + " + \"\\n\");");
                pw2.println("          }");
                pw2.println();
              }

            pw2.close();

            pw.println(sw3.toString());
            pw.println("  }");

            br.close();
            pw.close();
          }
        catch (Exception e)
          {
            System.err.println("While processing " + s);
            e.printStackTrace();
          }
      }

    /**
     * @param tagName
     * @return
     */
    private static String capitalized (String tagName)
      {
        return tagName.substring(0, 1).toUpperCase() + tagName.substring(1);
      }

    private static String uncapitalized (String tagName)
      {
        return tagName.substring(0, 1).toLowerCase() + tagName.substring(1);
      }

    private static String strip (String s)
      {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < s.length(); i++)
          {
            char c = s.charAt(i);

            if (Character.isJavaIdentifierPart(c))
              {
                result.append(c);
              }
          }

        return result.toString();
      }

    private static String x (int code,
                             String name,
                             PrintWriter out,
                             String registryName)
      {
        StringBuffer uName = new StringBuffer();
        char[] chars = name.toCharArray();

        for (int j = 0; j < chars.length; j++)
          {
            char c = chars[j];

            if (j > 0 && Character.isUpperCase(c))
              {
                if (Character.isLowerCase(chars[j - 1])
                    || (j < chars.length - 1 && Character.isLowerCase(chars[j + 1])))
                  uName.append("_");
              }

            if (!Character.isJavaIdentifierPart(c) || c == '.')
              c = '_';

            uName.append(Character.toUpperCase(c));
          }

        while (uName.length() < 40)
          uName.append(' ');

        out.println("  /** Field " + name + " (#" + code + ") */");
        out.println("    public final static Object " + uName + " = TagRegistry.getRegistry(\"" + registryName
            + "\").register(" + code + ", \"" + name + "\");");

        return uName.toString().trim();
      }
  }
