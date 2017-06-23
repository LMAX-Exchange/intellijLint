/*
 *    Copyright 2017 LMAX Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lmax.intellijLint.UnitsInspectionTests;

public class Methods extends Base {
    public void testReturnVariable() throws Exception {
        expectNoInspections();
    }

    public void testReturnUntyped() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testReturnThis() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testReturnParam() throws Exception {
        expectInspection("Returning null when expecting foo");
    }

    public void testCorrectReturnThis() throws Exception {
        expectNoInspections();
    }

    public void testLambda() throws Exception {
        expectInspection("Passing null when expecting a parameter of type foo", 173);
    }

    public void testCorrectLambda() throws Exception {
        expectNoInspections();
    }

    public void testCorrectReturn() throws Exception {
        expectNoInspections();
    }

    public void testCall() throws Exception
    {
        expectInspection("Passing null when expecting a parameter of type foo");
    }

    public void testCorrectCall() throws Exception
    {
        expectNoInspections();
    }

    public void testImplementingAnnotated() throws Exception {
        expectInspection("Implementation subtype (null) should match interface (foo)");
    }
}
