package com.fairyi.shop.newretail.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
/*加解密算法
        1摘要算法
        MD5,SHA-1,HMAC
        特点：单向不可逆，无密钥概念
        2对称加密
        加密和解密的密钥是一个
        典型代表：AES
        3.非对称加密
        加密与解密的密钥是一对（公钥和私钥）
        典型代表：RSA*/

/**
 *@Author feri
 *@Date Created in 2019/3/26 10:11
 * 实现主流的加解密算法
 */
public class EncryptionUtil {
    public static final String SHA1="SHA-1";
    public static final String SHA256="SHA-256";
    public static final String SHA512="SHA-512";
    public static final String AES="AES";
    public static final String RSA="RSA";
    public static final String PUBLICKEY="pubkey"; //公钥
    public static final String PRIVATEKEY="prikey";//私钥

    //MD5加密
    public static String md5Enc(String msg)  {
        try {
           /* JAVA的MessageDigest类,可以提供MD5算法或SHA算法用于计算出数据的摘要；它接收任意大小的数据，
           并输出计算后的固定长度的哈希值。这个输出的哈希值就是我们所说的信息摘要。
           MessageDigest类计算MD5摘要的步骤:
           1.创建MessageDigest对象
           public static MessageDigest getInstance(String algorithm)
           算法名不区分大小写，所以下面的写法都是正确的.
           2.向MessageDigest传送要计算的数据.该步骤就是调用下面的某个方法来完成数据的传递。
             public void update(byte input);
             public void update(byte[] input);
             public void update(byte[] input, int offset, int len);
           3.计算摘要  最后调用下面的某个方法来计算摘要。
             public byte[] digest();
             public byte[] digest(byte[] input);
             public int digest(byte[] buf,int offset,int len);
            4.将摘要转为16进制位的字符串(即哈希值)
            为了更友好的表示摘要，一般都将128位的二进制串 转为 32个16进制位 或 16个16进制位 ，并以字符串的形式表示。
            摘要一般以字符串的形式展示，所以在WEB应用中，用于表示密码的MD5摘要的数据库字段一般设置为String类型
            String password（虽然字段名字面意思表示账户密码，但实际上只是账户密码的MD5摘要）。
             */
        //创建MD5摘要解析器
            MessageDigest messageDigest=MessageDigest.getInstance("MD5");
            //进行加密 向MessageDigest传送要计算的数据
            messageDigest.update(msg.getBytes());
            //获取加密结果        计算摘要
            byte[] arr=messageDigest.digest();
            //将摘要转为xx进制位的字符串
            return Base64Util.base64Enc(arr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    //MD5加密，是上一个方法的重载
    //MD5安全加密 1、要加密的内容 2、盐 干扰内容 3、加密次数
    public static String md5Enc(String msg,String slat,int count)  {
        String s=msg+slat;
        for(int i=1;i<=count;i++){
            s= md5Enc(s);
        }
        return s;
    }
    //进行SHA安全散列加密 结果位base64
    public static String SHAEnc(String type,String msg){
        try {
            MessageDigest messageDigest=MessageDigest.getInstance(type);
            messageDigest.update(msg.getBytes());
            return Base64Util.base64Enc(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    //对称加密
    //生成对称加密的秘钥
    public static String createAESKEY(){
        try {
            //创建秘钥生成器对象
            KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
           //SecretKey类从接口 java.security.Key 继承的方法getAlgorithm, getEncoded, getFormat
            //String getAlgorithm()  返回：与此密钥关联的算法名称。
            //String getFormat() 返回：密钥的基本编码格式。
            //byte[] getEncoded() 返回：返回基本编码格式的密钥，如果此密钥不支持编码，则返回 null。
            SecretKey secretKey=keyGenerator.generateKey();
            return Base64Util.base64Enc(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    //加密
    public static String AESEnc(String key,String msg){
        //将字符串的秘钥转换为秘钥对象
        SecretKeySpec keySpec=new SecretKeySpec(Base64Util.base64Dec(key),"AES");
        try {
            //创建加密器
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,keySpec);
            return Base64Util.base64Enc(cipher.doFinal(msg.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //解密
    public static String AESDec(String key,String msg){
        //将字符串的秘钥转换为秘钥对象
        SecretKeySpec keySpec=new SecretKeySpec(Base64Util.base64Dec(key),"AES");
        try {
            //创建加密器
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,keySpec);
            return new String(cipher.doFinal(Base64Util.base64Dec(msg)),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //创建秘钥对儿
    public static Map<String,String> createRSAKey(){
        try {
            //创建生成器
            KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
            //初始化秘钥长度 2的n次幂
            keyPairGenerator.initialize(512);
            KeyPair keyPair=keyPairGenerator.generateKeyPair();
            Map<String,String> map=new HashMap<>();
            map.put(PUBLICKEY,Base64Util.base64Enc(keyPair.getPublic().getEncoded()));
            map.put(PRIVATEKEY,Base64Util.base64Enc(keyPair.getPrivate().getEncoded()));
            return map;


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    //私钥--加密
    public static String RSAEnc(String key,String msg){
        try {
            //转换私钥
            PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(Base64Util.base64Dec(key));
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            PrivateKey privateKey=keyFactory.generatePrivate(keySpec);
            Cipher cipher=Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,privateKey);
            return Base64Util.base64Enc(cipher.doFinal(msg.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //公钥--解密
    public static String RSADec(String key,String msg){
        try {
            //转换公钥
            X509EncodedKeySpec keySpec=new X509EncodedKeySpec(Base64Util.base64Dec(key));
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            PublicKey publicKey=keyFactory.generatePublic(keySpec);
            Cipher cipher=Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE,publicKey);
           return new String(cipher.doFinal(Base64Util.base64Dec(msg)),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
