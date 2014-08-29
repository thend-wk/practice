package com.thend.home.sweethome.md5;

/**
 * NeteaseSignUtil
 * Version 1.0
 * Copyright 2006-2007.
 * All Rights Reserved.
 * <p>本程序是个签名的工具类。
 * 用于各个产品和网银系统传递数据时使用。
 * 包含的功能包括产生需要的公私钥；产生签名和验证签名的过程。
 * @author Shaoqing Fu
 * 
 */

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EncryptionUtil
{
    private static final Log logger = LogFactory.getLog(EncryptionUtil.class);
    
    public static void main(String[] args) {
//    	genRSAKeyPair();
    	String src = "thend";
    	String pubKey = "30819f300d06092a864886f70d010101050003818d0030818902818100a1e5d4d493a750e211e46070e99cf62c9345852d4ba44aa7e241d3052a7b688eece644412aa2bb02f539a6ef3d884ea893673ee58ccf01018a64c3e1cdf9880b05d9441bbb79016b56ac447bced63d32309ffb9071b813f698a41975f9ecef63dabccf37b472a76d5ef97c888436f54e70883f36380ebe0641fb43d7d583ac370203010001";
    	String priKey = "30820275020100300d06092a864886f70d01010105000482025f3082025b02010002818100a1e5d4d493a750e211e46070e99cf62c9345852d4ba44aa7e241d3052a7b688eece644412aa2bb02f539a6ef3d884ea893673ee58ccf01018a64c3e1cdf9880b05d9441bbb79016b56ac447bced63d32309ffb9071b813f698a41975f9ecef63dabccf37b472a76d5ef97c888436f54e70883f36380ebe0641fb43d7d583ac3702030100010281800a0c9e1d63dceae45d7ff38c8eb3b38428d013e82fddced484f2d90a650cabfcd32fa7e4f3a48e3f2cc19b5f164eb3b33b1319905e212b4ad13058085824b910fcc7899db824c2d0ecceb840bc40cd69f84afd16ee36e02ce7f4ba0fdfba4a612a5dea6386bd1dcc0a00bca060ad20c559fc50cfd294491f2d5a796e1a36ee01024100d767443ce9a2cbed46af67599f3687cb3564241ea2f4ba9f6eee8549028682e2f4ce8e1548915fc5e8400090d39b7d053d318147d493cb1ca49792891c1ebd77024100c06909dc311534612364818a9e48c291aa9f6f9d662f7042c3e94e69444a59a89f81b719cabcff96dcdfd501ad3d7ca92f34d5d6028f9bd9da33b0c48d4737410240209ef5f997945cf7c91da53430656cb93c93b6d8a0eb191c2e0b9749d04518ab5051bbb8c36da8a86b1cce3920a2ca93318a3ebee7159ecbeead39b6e3f84fcf0240242295eb787c56fdf73ba4e877998a90f8ce4093fae0e5e06412a8db342c12728ca23bd4e8325e7de2b556b79de5724803c9946f40c3dfad264196ed12eb5101024032e7431afa8259a0b3669d23b3df5cd93296b450a29d9a2e968f60c6b833d9bd1b30dfe80b314cd25d45cd9c75a6ed7608cf596a7a8410fae60ff42c96be3ed8";
    	String encryptedSrc = generateSHA1withRSASigature(priKey, src);
    	boolean ret = verifySHA1withRSASigature(pubKey, encryptedSrc, "thend");
    	System.out.println(ret);
    	encryptedSrc = encryptByPublicKey(src, pubKey);
    	System.out.println(encryptedSrc);
    	System.out.println(decryptByPrivateKey(encryptedSrc, priKey));
    }
    
    
    /**
     * 公钥加密
     * @param src
     * @param pubKey
     * @return
     */
    public static String encryptByPublicKey(String src, String pubKey) {
    	try {
		    //取得公钥
    		byte[] pubbyte = hexStrToBytes(pubKey.trim());
		    X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(pubbyte);
		    KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		    PublicKey publicKey=keyFactory.generatePublic(x509KeySpec);
		    //对数据加密
		    Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
		    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		    return bytesToHexStr(cipher.doFinal(src.getBytes()));
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    		return null;
    	}
    }

    /**
     * 私钥解密
     * @param encryptedSrc
     * @param priKey
     * @return
     */
    public static String decryptByPrivateKey(String encryptedSrc, String priKey) {
    	try {
	        //取得私钥
	    	byte[] pribyte = hexStrToBytes(priKey.trim());
	    	PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
	        KeyFactory fac = KeyFactory.getInstance("RSA");
	        RSAPrivateKey privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
	        //对数据解密
	        Cipher cipher=Cipher.getInstance(privateKey.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, privateKey);
	        return new String(cipher.doFinal(hexStrToBytes(encryptedSrc)));
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    		return null;
    	}
    }
    
    /**
     * 本方法使用SHA1withRSA签名算法产生签名
     * @param String priKey 签名时使用的私钥(16进制编码)
     * @param String src    签名的原字符串
     * @return String       签名的返回结果(16进制编码)。当产生签名出错的时候，返回null。
     */
    public static String generateSHA1withRSASigature(String priKey, String src)
    {
        try
        {
            Signature sigEng = Signature.getInstance("SHA1withRSA");
            byte[] pribyte = hexStrToBytes(priKey.trim());

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pribyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");

            RSAPrivateKey privateKey = (RSAPrivateKey) fac.generatePrivate(keySpec);
            sigEng.initSign(privateKey);

            sigEng.update(src.getBytes());

            byte[] signature = sigEng.sign();
            return bytesToHexStr(signature);
        }
        catch (Exception e)
        {
            logger.error(e, e);
            //LogMan.log("[NeteaseSignUtil][generateSHA1withRSASigature]"+e);
            return null;
        }
    }

    /**
     * 本方法使用SHA1withRSA签名算法验证签名
     * @param String pubKey 验证签名时使用的公钥(16进制编码)
     * @param String sign   签名结果(16进制编码)
     * @param String src    签名的原字符串
     * @return String       签名的返回结果(16进制编码)
     */
    public static boolean verifySHA1withRSASigature(String pubKey, String sign, String src)
    {
        try
        {
            Signature sigEng = Signature.getInstance("SHA1withRSA");

            byte[] pubbyte = hexStrToBytes(pubKey.trim());

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbyte);
            KeyFactory fac = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPubKey = (RSAPublicKey) fac.generatePublic(keySpec);

            sigEng.initVerify(rsaPubKey);
            sigEng.update(src.getBytes());

            byte[] sign1 = hexStrToBytes(sign);
            return sigEng.verify(sign1);

        }
        catch (Exception e)
        {
            //LogMan.log("[NeteaseSignUtil][verifySHA1withRSASigature]"+e);
            logger.error(e, e);
            return false;
        }
    }

    /**
     * 本方法用于产生1024位RSA公私钥对。
     * 
     */
    public static void genRSAKeyPair()
    {
        KeyPairGenerator rsaKeyGen = null;
        KeyPair rsaKeyPair = null;
        try
        {
            //System.out.println("Generating a pair of RSA key ... ");
            rsaKeyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            random.setSeed(("" + System.currentTimeMillis() * Math.random() * Math.random()).getBytes());
            rsaKeyGen.initialize(1024, random);
            rsaKeyPair = rsaKeyGen.genKeyPair();
            PublicKey rsaPublic = rsaKeyPair.getPublic();
            PrivateKey rsaPrivate = rsaKeyPair.getPrivate();

            logger.info("公钥：" + bytesToHexStr(rsaPublic.getEncoded()));
            logger.info("私钥：" + bytesToHexStr(rsaPrivate.getEncoded()));
            //System.out.println("1024-bit RSA key GENERATED.");
        }
        catch (Exception e)
        {
            //LoggerUtil.error(e, NetEaseSignUtil.class.getName(), "genRSAKeyPair");
            //System.out.println("genRSAKeyPair：" + e);
        }
    }
    
    public static final String encryptDES(String key, String input){
    	try {
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes("utf-8")));
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			
			byte[] enbytes = cipher.doFinal(input.getBytes("utf-8"));
			return bytesToHexStr(enbytes);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
    }
    
    public static final String decryptDES(String key, String ciphertext){
		try {
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes("utf-8")));
			
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptBytes = cipher.doFinal(hexStrToBytes(ciphertext));
			return new String(decryptBytes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return null;
	}

    /**
     * 将字节数组转换为16进制字符串的形式.
     */
    private static final String bytesToHexStr(byte[] bcd)
    {
        StringBuffer s = new StringBuffer(bcd.length * 2);

        for (int i = 0; i < bcd.length; i++)
        {
            s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
            s.append(bcdLookup[bcd[i] & 0x0f]);
        }

        return s.toString();
    }

    /**
     * 将16进制字符串还原为字节数组.
     */
    private static final byte[] hexStrToBytes(String s)
    {
        byte[] bytes;

        bytes = new byte[s.length() / 2];

        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

    private static final char[] bcdLookup =
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

}
