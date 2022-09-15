# script that calls multiple SPDX validation tools on every input file from the command line
# note: this needs tools-python installed in the environment

import sys
from subprocess import Popen, PIPE

def callProcessAndGetOutput(args):
    newLine_character = '\n'.encode()

    process = Popen(args, stdout=PIPE, stderr=PIPE)
    scriptOutput = []
    stdout, stderr = process.communicate()
    scriptOutput += stdout.split(newLine_character)
    if stderr != '':
        scriptOutput += stderr.split(newLine_character)

    return [retbyte.decode('UTF-8') for retbyte in scriptOutput if retbyte != b'']


def validateFile(pathOfFileToValidate):
    new_javatools_args = ['java', '-jar',
                          '/home/armin/IdeaProjects/tools-java/target/tools-java-1.1.0-jar-with-dependencies.jar',
                          'Verify', pathOfFileToValidate]
    old_javatools_args = ['java', '-jar',
                          '/home/armin/IdeaProjects/old-java-tools/target/spdx-tools-2.2.9-SNAPSHOT-jar-with-dependencies.jar',
                          'Verify', pathOfFileToValidate]
    pythontools_args = ['python3', '/home/armin/PycharmProjects/tools-python/spdx/cli_tools/parser.py', '--file',
                        pathOfFileToValidate]

    print('\n\n\nStarting validation for file '+pathOfFileToValidate)
    print('\nValidation Result from new java-tools:\n')
    for resultString in callProcessAndGetOutput(new_javatools_args):
        print(resultString)

    print('\nValidation Result from old java-tools:\n')
    for resultString in callProcessAndGetOutput(old_javatools_args):
        print(resultString)
    print('\nParsing Result from python-tools:\n')

    pythonToolsOutput = callProcessAndGetOutput(pythontools_args)
    if pythonToolsOutput[0].startswith('doc comment'):
        print('Document has been successfully parsed.')
    else:
        for resultString in pythonToolsOutput:
            print(resultString)


if __name__ == '__main__':

    for filepath in sys.argv[1:]:
        validateFile(filepath)
