# QM-Java
##Overview: 
This Java program implements the Quine-McCluskey (QM) algorithm, aka the method of prime implicants. This is used to simplify boolean expressions by reducing the number of product terms required to represent a given logic function. Using binary representation of the terms, they are grouped based on the number of 1's in their binary form. The code then uses iterative programming to reduce the terms down to their prime implicants. This program reads input from a PLA file, processes the data with the QM method, and then outputs the simplified terms into a new PLA file.
##Challenges and Solutions:
###Combining Terms Efficiently
Ensuring that no prime implicants were ignored when handling binary strings presented issues when implementing the QM method. As the number of inputs grew, it was harder to correctly identify and combine each of the terms that differed by only one bit. 

A solution was found by creating smaller helper methods that checked for single-bit differences and marked terms that can't be further combined into other groups of terms. 
###Parsing in PLA File
Correctly processing the information given in PLA format was a bit of a challenge initially. Having had no previous experience with having a PLA file as an input and output, I did not know how to get the code to differentiate between the logic terms and the number of inputs, outputs, terms, and end of the file.

I had found a way to implement this part of the program by comparing it with similar examples I had found on online resources such as geeksforgeeks. The program parses in the data by reading the file line by line and makes a clear distinction between metadata lines (ie. .i, .o, .p, and .e) and the actual logic terms.
###Verification of Functionality
Some test cases were created to test the functionality of the program. 
Test case 1 was made to simply test if the program could handle a simple PLA file with some terms that included that it should ignore. The terms were 000 1 001 0 010 1 011 0 100 1 101 1. Here, the ignored terms are labeled with 0's next to them, 001 and 011. Based on the remaining terms, 000, 010, 100, and 101, the minimized terms should result in 0-- and -10. When ran, the code outputted .i 3 .o 1 .p 3 0-- 1 -10 1 .e which should be the correct output.
Test case 3 was made and did not include ignored terms, making sure the program processed the terms correctly and did not mislabel any as to be ignored. The terms were 000 1 001 1 010 1 100 1. Here, the minimized terms should result in 0-- only. When ran, the code outputted .i 3 .o 1 .p 1 0-- 1 .e as expected.
Test case 1 was made to test how the program handled multiple groupings and reductions. The terms were 0000 1 0001 1 0101 1 0110 1 1000 1 1011 1 1100 1 1110 1. Here, the ignored terms are labeled with 0's next to them, 001 and 011. When calculated, the terms 0000 and 0001 can be combined into -00-. The remaining terms can then be combined into 1-10. When ran, the code outputted .i 4 .o 1 .p 2 -00- 1 1-10 1 .e ensuring the code could handle implementing the Quine-McCluskey on a bigger scale as well as properly process the terms from the file.
###Conclusion
In conclusion, the Quine-McCluskey was implemented in Java mainly through the use of arrays, lists, and string manipulation. Data structures such as ArrayList helped in dynamically managing minterms, ignored terms, and the prime implicants. String operations allowed for an easier conversion for decimal to binary representations and aided in the processing of data from the PLA file. Overall, this code provided a deeper understanding the Quine-McCluskey method to find prime implicants of boolean functions. 
