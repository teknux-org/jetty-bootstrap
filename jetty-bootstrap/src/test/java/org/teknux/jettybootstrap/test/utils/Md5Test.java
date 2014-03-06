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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.utils.Md5;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Md5Test {

	@Test
	public void do01Md5Test() {
		Map<String, String> stringToMd5 = new HashMap<String, String>();
		stringToMd5.put("JyFWTMoTjEQ72", "9d1addc8a8281edb0b1f5a671332bb82");
		stringToMd5.put("78TGRU5kF7XCo", "97360ee4fb0e66c218f7008d87268ac0");
		stringToMd5.put("Mt7eY4gQvFzSQ", "bf6c0c20394bc0fd7518e32f3c51be9e");
		stringToMd5.put("OSH7KONCcaM0YhWydHSFWzJIEguo5nVEtj92tlY", "278cd9e53c3310bf29112253de5cd107");
		stringToMd5.put("RKkimEAXQaIqwi3swQXjQOuGEIX49imQxD8n7zMHW7n2X0MU9WwgZqKghftg5a32U", "8b0795a30a2e798ad533c353ccdfd389");

		for (Entry<String, String> entry : stringToMd5.entrySet()) {
			Assert.assertEquals(entry.getValue(), Md5.hash(entry.getKey()));
		}

	}
}
