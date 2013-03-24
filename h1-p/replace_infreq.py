#!/usr/bin/env python

__author__="Apurv Verma <dapurv5@gmail.com>"
__date__ ="$Mar 24, 2013"

import sys

"""
Reads an input training file and computes frequency for each
word. Then in a second run replaces each infrequent word i.e.
words with frequencies less than 5 by one of the following
_NUMERIC_ if the string is a number
_ALLCAPITALS_ if the string has all capital letters
_LASTCAPITAL_ if the last character is capital
_RARE_ if none of the above holds.
"""
THRESHOLD_FREQUENCY = 5
NUMERIC = "_NUMERIC_"
ALLCAPITALS = "_ALLCAPITALS_"
LASTCAPITAL = "_LASTCAPITAL"
RARE = "_RARE_"

def compute_frequencies(filename):
    """
    Computes frequencies for each word in the input file.
    """
    freq = {}
    file = open(filename)
    for line in file:
        str  = line.strip().split(" ")
        word = str[0]
        if(word not in freq):
            freq[word] = 0
        freq[word] = freq[word] + 1
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

        if freq[word] < THRESHOLD_FREQUENCY:
            writer.write("%s %s\n" %(get_symbol(word), str[1]))
        else:
            writer.write("%s" %line)
    file.close()


def get_symbol(word):
    if word.isdigit():
        return NUMERIC
    elif word.isupper():
        return ALLCAPITALS
    elif word[len(word)-1].isupper():
        return LASTCAPITAL
    else:
        return RARE


def usage():
    print """
    python replace_infreq.py [input_file] > [output_file]
    Read in a gene tagged training input file and produce another
    gene tagged training input file where infrequent words have been
    replaced by symbols representing that class
    """
    
    
if __name__ == "__main__":

    if len(sys.argv) !=2: #Expect exactly one argument the training file
        usage()
        sys.exit(2)
    rewrite_file(sys.stdout, sys.argv[1])