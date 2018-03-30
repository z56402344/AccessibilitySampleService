package z.util;

/**
 *MD5
 */
public class MD5 {

    public static String getMD5(String instr) {
        return md5Upper(instr);
    }

    public static String md5Upper(String instr) {
    	return md5(instr,hexUpper);
    }
    public static String md5Lower(String instr) {
    	return md5(instr,hexLower);
    }
    public static final char hexLower[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'};
    public static final char hexUpper[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'};
    public static String md5(String instr,char[] hexDigits) {
        String s = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(instr.getBytes());
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {
        }
        return s;
    }
}
