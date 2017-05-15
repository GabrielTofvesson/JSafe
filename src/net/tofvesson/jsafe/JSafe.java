package net.tofvesson.jsafe;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;

@SuppressWarnings("ALL")
public class JSafe {

    // Special values in class
    public static final String CLASSNAME = "&LOADEDCLASS;";
    public static final String AMPERSAND = "&amp;";
    Process p;

    public JSafe(String code, String... args){
        Process sand = null;
        try {
            File f = File.createTempFile("exec", ".java");
            code = code.replace(CLASSNAME, f.getName().substring(0, f.getName().length()-5)).replace(AMPERSAND, "&");
            OutputStream o = new FileOutputStream(f);
            o.write(code.getBytes());
            o.close();
            JavaCompiler j = ToolProvider.getSystemJavaCompiler();
            StringBuilder sb = new StringBuilder();
            for(String s : args) sb.append(s).append(' ');
            if(sb.length()>0) sb.setLength(sb.length()-1);
            if(j.run(null, null, null, f.getAbsolutePath())==0){
                f.delete();
                File f2 = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().length()-4)+"class");
                File f1 = new File(f2.getAbsolutePath().substring(0, f2.getAbsolutePath().length()-f2.getName().length()));
                sand = Runtime.getRuntime().exec("java "+f2.getName().substring(0, f2.getName().length()-6), new String[]{}, f1.getAbsoluteFile());
                p = sand;
                if(p!=null){
                    Thread t = new Thread(() -> {
                        try{
                            InputStream i = p.getInputStream();
                            Thread.sleep(200);
                            while(p.isAlive() || i.available()>0){
                                if(i.available()>0) System.out.write(i.read());
                                else Thread.sleep(1);
                            }
                            f2.delete();
                        }catch(Exception e){ e.printStackTrace(); }
                    });
                    t.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        //} catch (InterruptedException e) {
            e.printStackTrace();
        } finally{ if(sand==null) p = null; }
    }


    /*
                f.delete();
                f = new File(f.getAbsolutePath());
                SandboxClassLoader loader = new SandboxClassLoader(null, "net.tofvesson.jsafe.JSafe");
                ArrayList<Byte> a = new ArrayList<>();
                byte[] b = new byte[4096];
                int i;
                while()
    */
    class SandboxClassLoader extends ClassLoader{

        protected final String[] blacklist;
        protected final String[] packageBlacklist;

        public SandboxClassLoader(String[] packageBlacklist, String... blacklist){
            this.packageBlacklist = packageBlacklist==null?new String[]{}:packageBlacklist;
            this.blacklist = blacklist;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }

        @Override
        protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
            return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
        }
    }
}
