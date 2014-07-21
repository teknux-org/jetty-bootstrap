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
package org.teknux.jettybootstrap.test.utils;

import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.utils.Md5Util;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Md5UtilTest {

    @Test
    public void test01Md5Sum() throws NoSuchAlgorithmException {
       Assert.assertEquals("5d41402abc4b2a76b9719d911017c592", Md5Util.hash("hello"));
       Assert.assertEquals("7d793037a0760186574b0282f2f435e7", Md5Util.hash("world"));
       Assert.assertEquals("5eb63bbbe01eeed093cb22bb8f5acdc3", Md5Util.hash("hello world"));
    }
}
