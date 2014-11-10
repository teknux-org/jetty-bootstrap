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
package org.teknux.jettybootstrap.keystore;

public class JettyKeystoreException extends Exception {

    private static final long serialVersionUID = 1L;

    public static final int ERROR_READ_PRIVATE_KEY = 0;
    public static final int ERROR_READ_CERTIFICATE = 1;
    public static final int ERROR_LOAD_PRIVATE_KEY = 2;
    public static final int ERROR_LOAD_CERTIFICATE = 3;
    public static final int ERROR_CREATE_KEYS = 4;
    public static final int ERROR_CREATE_CERTIFICATE = 5;
    public static final int ERROR_CREATE_KEYSTORE = 6;
    public static final int ERROR_SAVE_KEYSTORE = 7;

    private final int type;

    public JettyKeystoreException(int type, String s) {
        super(s);

        this.type = type;
    }

    public JettyKeystoreException(int type, Throwable throwable) {
        super(throwable);

        this.type = type;
    }

    public JettyKeystoreException(int type, String s, Throwable throwable) {
        super(s, throwable);

        this.type = type;
    }

    public int getType() {
        return type;
    }
}
