/*******************************************************************************
 * (C) Copyright 2014 Teknux.org (http://teknux.org/).
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
 * Contributors:
 *      "Pierre PINON"
 *      "Francois EYL"
 *      "Laurent MARCHAL"
 *  
 *******************************************************************************/
package org.teknux.jettybootstrap.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

  private final static String HASH_ALGORITHM = "MD5";
    
    private Md5Util() {
    }
    
    public static String hash(String string) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        StringBuffer stringBuffer = new StringBuffer();
        for (byte byt : messageDigest.digest(string.getBytes())) {
            stringBuffer.append(String.format("%02x", byt & 0xff));
        }

        return stringBuffer.toString();
    }
}
