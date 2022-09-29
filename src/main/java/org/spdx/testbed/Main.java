package org.spdx.testbed;

public class Main
{
    public static void main(String[] args) {
        if (args.length > 2) {
            var testCase = args[1];
            switch(testcase) {
                case "generationTest":
                    GenerationTestCase.test(ArrayUtils.remove(ArrayUtils.remove(args, 0),0));
            }
        }
    }
}
