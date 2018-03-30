package z.frame;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Util {
    public Util() {
    }

    public static File createParentDir(String path) {
        File f = new File(path);
        if(!f.exists()) {
            File parentDir = f.getParentFile();
            if(!parentDir.exists()) {
                parentDir.mkdirs();
            }
        }

        return f;
    }

    public static boolean writeText(String ctx, String path) {
        boolean ret = false;
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(createParentDir(path));
            if(ctx != null) {
                out.write(ctx.getBytes());
            }

            ret = true;
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        if(out != null) {
            try {
                out.close();
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return ret;
    }

    public static String readText(String path, int maxLen) {
        FileInputStream in = null;
        String r = null;
        if(maxLen <= 0) {
            maxLen = 2048;
        }

        try {
            File e = new File(path);
            if(e.exists()) {
                in = new FileInputStream(e);
                byte[] bin = new byte[maxLen];
                int ret = in.read(bin);
                r = new String(bin, 0, ret);
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        if(in != null) {
            try {
                in.close();
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

        return r;
    }

    public static boolean closeSafe(Closeable c) {
        if(c == null) {
            return true;
        } else {
            try {
                c.close();
                return true;
            } catch (Exception var2) {
                return false;
            }
        }
    }
}
