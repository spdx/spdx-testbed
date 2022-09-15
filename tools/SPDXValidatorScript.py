# script that calls multiple SPDX validation tools on every input file from the command line
# note: this needs tools-python installed in the environment
import os
import sys
from subprocess import Popen, PIPE


def callProcessAndGetOutput(args):
    newLineCharacter = '\n'.encode()

    process = Popen(args, stdout=PIPE, stderr=PIPE)
    scriptOutput = []
    stdout, stderr = process.communicate()
    scriptOutput += stdout.split(newLineCharacter)
    if stderr != '':
        scriptOutput += stderr.split(newLineCharacter)

    return [byte.decode('UTF-8') for byte in scriptOutput if byte != b'']


def validateFile(pathOfFileToValidate, toolsWithArguments):
    print('\nStarting validation for file ' + pathOfFileToValidate)

    for tool, args in toolsWithArguments.items():
        validationOutput = callProcessAndGetOutput (args + [pathOfFileToValidate])
        print('\nValidation Result from ' + tool + ':\n')

        # prevent the tools-python parser from printing out the whole document
        if validationOutput[0].startswith('doc comment'):
            print('Document has been successfully parsed.')
        else:
            for resultString in validationOutput:
                print(resultString)


if __name__ == '__main__':

    scriptPath = os.path.dirname(__file__)

    toolsWithArguments = {}
    toolsWithArguments['new java-tools'] = [os.path.join(scriptPath, 'spdx-tools-java.sh'), 'Verify']
    toolsWithArguments['old java-tools'] = [os.path.join(scriptPath, 'spdx-tools.sh'), 'Verify']
#     toolsWithArguments['python-tools'] = ['python3', '-m' 'tools-python.parser']

    for filepath in sys.argv[1:]:
        validateFile(filepath, toolsWithArguments)
