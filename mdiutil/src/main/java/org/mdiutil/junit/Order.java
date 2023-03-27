/*
 MODIFIED BSD LICENSE
 ***** BEGIN LICENSE BLOCK *****

 Copyright (c) 2013 Dassault Aviation
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
 */
package org.mdiutil.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation used with the custom {@link OrderedRunner} JUnit runner. This allows to control the
 * ordering of tests in a test class.
 * <br/>
 * For Java &le; 6.0, the Oracle JVM execute the tests methods in the test class in the order they are declared
 * in the source. It is not the case anymore for Java &ge; 7.0, in this case it is not possible to rely on the
 * JVM for the execution in the test methods, creating specific test failures on Java 7 which would work
 * well for Java 6. The solution is to use a custom Test Runner where the ordering of test methods is defined
 * through an annotation.
 * <br/>
 * In many cases, the ordering of tests cases is not imporetant, but in some specific
 * cases the state at the start of the next test case depends on the state at the end of the previous one.
 * The OrderedRunner class is a Test runner which will order the test methods according to the
 * value of the &#064;Order annotation.
 *
 * <h1>Example</h1>
 * If a test class has the two following methods:
 * <pre>
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
 * nothing prevents the tests to be executed in the order test2() <b>then</b> test1(). To ensure that
 * the order will be test1() <b>then</b> test2(), you can use:
 * <pre>
 * &#064;RunWith(OrderedRunner.class)
 * public class MyTest {
 *
 *   &#064;Test
 *   &#064;Order(order=1)
 *   public void test1() {
 *   ...
 *   }
 *
 *   &#064;Test
 *   &#064;Order(order=2)
 *   public void test2() {
 *   ...
 *   }
 * </pre>
 *
 * @version 0.8
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
   /**
    * The order in which the method must be run.
    *
    * @return the order in which the method must be run
    */
   public int order();
}
