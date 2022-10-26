# SPDX-FileCopyrightText: TNG Technology Consulting GmbH
#
# SPDX-License-Identifier: Apache-2.0

# script that calls multiple SPDX validation tools on every input file from the command line
import os
import sys
from pathlib import Path
from subprocess import Popen, PIPE


def call_process_and_get_output(args):
  new_line_character = '\n'.encode()

  process = Popen(args, stdout=PIPE, stderr=PIPE)
  script_output = []
  stdout, stderr = process.communicate()
  script_output += stdout.split(new_line_character)
  if stderr != '':
    script_output += stderr.split(new_line_character)

  return [byte.decode('UTF-8') for byte in script_output if byte != b'']


# Is this warning something we should fix? See Issue#5
def erase_unnecessary_warnings_in_output(output):
  try:
    output.remove(
      "WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.")
  except ValueError:
    pass


def validate_file(path_of_file_to_validate, tools_with_arguments_intern):
  print('\nStarting validation for file ' + path_of_file_to_validate)

  for tool, args in tools_with_arguments_intern.items():
    validation_output = call_process_and_get_output(
      args + [path_of_file_to_validate])
    print('\n### ' + tool + ':\n')

    erase_unnecessary_warnings_in_output(validation_output)

    for result_string in validation_output:
      print(result_string)


if __name__ == '__main__':

  scriptPath = os.path.dirname(__file__)

  tools_with_arguments = {
    'new java-tools': [os.path.join(scriptPath, 'spdx-tools-java.sh'),
                       'Verify'],
    'old java-tools': [os.path.join(scriptPath, 'spdx-tools.sh'), 'Verify']}

  for filepath in sys.argv[1:]:
    if Path(filepath).is_file():
      validate_file(filepath, tools_with_arguments)
    else:
      print(filepath + " is not a file.")
