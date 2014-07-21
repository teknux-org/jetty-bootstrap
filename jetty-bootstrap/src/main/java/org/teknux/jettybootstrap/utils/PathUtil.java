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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathUtil {
    private static final String CHARSET_UTF8 = "UTF-8";
    
    /**
     * Get Jar location
     * 
     * @return Jar directory
     */
    public static String getJarDir() {
        return getJarDir(PathUtil.class);
    }
    
    /**
     * Get Jar location
     * 
     * @param clazz Based on class location
     * @return String
     */
    public static String getJarDir(Class<?> clazz) {
        return decodeUrl(new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent());
    }
    
    /**
     * Decode Url
     * 
     * @param url
     * @return
     */
    private static String decodeUrl(String url) {
        if (url == null) {
            return null;
        }

        try {
            return URLDecoder.decode(url, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }
}
