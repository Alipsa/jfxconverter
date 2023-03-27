/*
 MODIFIED BSD LICENSE
 ***** BEGIN LICENSE BLOCK *****

 Copyright (c) 2014 Dassault Aviation
 Copyright (c) 2014 Hervé Girod
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the <organization>.
 4. Neither the name of the <organization> nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 ***** END LICENSE BLOCK *****
 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on http://j661.sourceforge.net
 */
package org.mdiutil.junit;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation for Unit Tests categories.
 *
 * <h1>Example</h1>
 * <pre>
 * &#064;RunWith(CategoryRunner.class)
 * &#064;Category(cat="editor")
 * &#064;Category(cat="server")
 * public class MyTest {
 *
 *   &#064;Test
 *   public void test1() {
 *   ...
 *   }
 *
 *   &#064;Test
 *   public void test2() {
 *   ...
 *   }
 * </pre>
 * In this case, the MyTest class belong to two categories:
 * <ul>
 * <li>"editor"</li>
 * <li>"server"</li>
 * </ul>
 *
 * @since 0.8
 * @see JUnitCategoryWrapper
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Categories.class)
public @interface Category {
   /**
    * Return the category.
    *
    * @return the category
    */
   public String cat();
}
