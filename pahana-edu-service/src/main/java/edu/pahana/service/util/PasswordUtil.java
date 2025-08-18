package edu.pahana.service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    public static String hash(String password){
        try{
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[salt.length + hashed.length];
            System.arraycopy(salt,0,combined,0,salt.length);
            System.arraycopy(hashed,0,combined,salt.length,hashed.length);
            return Base64.getEncoder().encodeToString(combined);
        }catch(Exception e){ throw new RuntimeException(e); }
    }

    public static boolean verify(String password, String stored){
        try{
            byte[] decoded = Base64.getDecoder().decode(stored);
            byte[] salt = new byte[16];
            System.arraycopy(decoded,0,salt,0,16);
            byte[] hash = new byte[decoded.length-16];
            System.arraycopy(decoded,16,hash,0,hash.length);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] test = md.digest(password.getBytes(StandardCharsets.UTF_8));
            if(test.length != hash.length) return false;
            int r = 0;
            for(int i=0;i<hash.length;i++){ r |= (test[i] ^ hash[i]); }
            return r==0;
        }catch(Exception e){ return false; }
    }
}
