# sjpp
This Simple Java PreProcessor provides some basic directives used to generate a new tree structure of Java sources from some existing Java sources.

All directives are embedded in Java comments which means that the original unprocessed sources can be compiled by any regular Java compiler.

When running the precessor, some flags may be define. Directives check wether a flag is defined or not. If the flag expected by directive is define, the directive is executed. Otherwise, the directive is simply ignored.

## List of directives:


### Remove a file
  
This directive could be put anywhere in any `.java` file. The syntax is `//::remove file when ` followed by the condition (that is, the flag name)

If the corresponding flag has been defined, the file containing this line will be deleted from the target tree structure.

In addition, every `import` of this file will also be removed from any file of the target tree structure.
  
  
### Remove a single folder

This directive could be put anywhere in any `.java` file. The syntax is `//::remove current folder when ` followed by the condition (that is, the flag name)

If the corresponding flag has been defined, all files from the folder containing this file will be deleted from the target tree structure.

In addition, every `import` of any removed file will also be removed from any file of the target tree structure.


### Remove a folder and its subfolders

This directive could be put anywhere in any `.java` file. The syntax is `//::remove folder when ` followed by the condition (that is, the flag name)

If the corresponding flag has been defined, all files from the folder (or its subfolders) containing this file will be deleted from the target tree structure.

In addition, every `import` of any removed file will also be removed from any file of the target tree structure.


### Comment some code

This directive is used to remove a part of a `.java` file.

`//::comment when ` followed by the condition (that is, the flag name) marks the beginning of the part to be removed.
`//::done` marks the end of the part to be removed.

All lines between those two lines will simply be removed in the result file.


### Uncomment some code

This directive is used to uncomment a part of a `.java` file.

`//::uncomment when ` followed by the condition (that is, the flag name) marks the beginning of the part to be uncommented.
`//::done` marks the end of the part to be removed.

All lines between those two lines will be uncommented in the result file.


### Switch some code

This directive is used to both comment or uncomment a part of a `.java` file.

`//::revert when ` followed by the condition (that is, the flag name) marks the beginning of the part to be switched.
`//::done` marks the end of the part to be switched.

If the directive is activated, all commented lines in the part will be uncommented and all uncommented lines will be commented.

This is an handy alternative of using two consecutive comment and uncomment directives with the same flag.


