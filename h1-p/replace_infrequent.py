#!/usr/bin/env python

__author__="Apurv Verma <dapurv5@gmail.com>"
__date__ ="$Mar 20, 2013"

import sys


"""
Reads an input training file and computes frequency for each
word. Then in a second run replaces each infrequent word i.e.
words with frequencies less than 5 by a common word _RARE_
"""

def compute_frequencies(filename):
    freq = {}
    file = open(filename)
    for line in file:
        str  = line.strip().split(" ")
        word = str[0]
        if(word in freq):
            freq[word] = freq[word] + 1
        else:
            freq[word] = 0
    file.close()
    return freq

def rewrite_file(writer, filename):
    freq = compute_frequencies(filename)
    file = open(filename)

    for line in file:
        str = line.strip().split(" ")
        word = str[0]
        if line == None:
            continue

        if freq[word] < 5:
            writer.write("%s %s\n" %("_RARE_", str[1]))
        else:
            writer.write("%s" %line)
    file.close()

def usage():
    print """
    python replace_infrequent.py [input_file] > [output_file]
    Read in a gene tagged training input file and produce another
    gene tagged training input file where infrequence words have been
    replaced by _RARE_
    """

if __name__ == "__main__":

    if len(sys.argv) !=2: #Expect exactly one argument the training file
        usage()
        sys.exit(2)
    rewrite_file(sys.stdout, sys.argv[1])
