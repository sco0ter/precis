/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.precis;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Christian Schudt
 */
public class CaseFoldingTest {

    @Test
    public void testCaseFolding() {
        InputStream inputStream = CaseFoldingTest.class.getResourceAsStream("CaseFolding-8.0.0.txt");

        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] split = line.split(";");
                    String code = split[0].trim();
                    String status = split[1].trim();
                    String mapping = split[2].trim();
                    // Only test the default case mapping
                    if ("C".equals(status)) {
                        int codePoint = Integer.parseInt(code, 16);
                        int codePointMapped = Integer.parseInt(mapping, 16);
                        String sCode = new String(new int[]{codePoint}, 0, 1);
                        String sMapped = new String(new int[]{codePointMapped}, 0, 1);
                        if (Character.isDefined(codePoint) && !PrecisProfile.caseFold(sCode).equals(sMapped)) {
                            Assert.fail("Case mapping failed.");
                        }
                    }
                }
            }
        }
    }
}
