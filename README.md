# TagCloudGenerator

A Java program that generates a tag cloud from a given input text.

The program will ask the user for the name of an input file, for the name of an output file, and for the number of words to be included in the generated tag cloud (a positive integer, say N and less than or equal to the number of words in the input file). The program shall assume the user input as being the complete relative or absolute path as the name of the input file, or the name of the output file, and will not augment the given path in any way and will look for files outside the source folder only if absolute path is provided., e.g., it will not supply its own filename extension. 

The input file can be any arbitrary text file including an empty file(which will simply generate an empty tag cloud).

The output shall be a single well-formed HTML file in the same project folder holding the source code unless a specific path is given when asking for user input. The HTML file will display the name of the input file in a heading followed by a tag cloud of the N words with the highest count in the input. The words shall appear in alphabetical order. The font size of each word in the tag cloud shall be proportional to the number of occurrences of the word in the input text (i.e., more frequent words will be displayed in a larger font than less frequent ones). If no path is provided for the output files, they will be created in the project folder.

The project has a single file named TagCloudGeneratorJavaIO. To run the project, import this .java file into a seperate src folder inside a new project folder. The input files are recommended to be put in a seperate folder inside the same project folder (eg.data folder).This way the path to the input files is much easier to access, i.e. data/input.txt. A seperate folder can be made for outputs(i.e. outputs/tagcloud.html) or can simply be made in the project folder.  

The project was developed in Eclipse IDE but can be run in any IDE/terminal that supports .java files.  
