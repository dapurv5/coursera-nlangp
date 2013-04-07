#!/usr/bin/env python

__author__="Apurv Verma <dapurv5@gmail.com>"
__date__ ="$Apr 6, 2013"

import sys
import json

from types import *

"""
Reads an input training file and computes frequency for each
word. Then in a second run replaces each infrequent word i.e.
words with frequencies less than 5 by a common word _RARE_
"""
THRESHOLD_FREQUENCY = 5
RARE_SYMBOL = "_RARE_"

def compute_frequencies(filename):
    """
    str -> {str:int}
    Precondition: filename != None
    Computes frequencies for each word in the input file.
    """
    freq = {}
    file = open(filename)
    for line in file:
        if line == None:
            continue
        
        str  = line.strip()
        tree = json.loads(str)
        traverse_tree(tree, freq)
    file.close()
    return freq


def traverse_tree(tree, freq):
    """
    Traverses the given tree and updates the frequencies for words on the fringes.
    """
    if length(tree) == 1:
        return
    elif length(tree) == 2:
        if is_fringe(tree):
           #update frequencies here.
           update_frequencies(tree[1], freq)
        else:
           traverse_tree(tree[0], freq)
           traverse_tree(tree[1], freq)
    else:
        traverse_tree(tree[1], freq)
        traverse_tree(tree[2], freq)
        
        

def is_fringe(tree):
    return length(tree) == 2 and (not (isinstance(tree[0], ListType))) and (not (isinstance(tree[1], ListType)))

def update_frequencies(word, freq):
    if (word not in freq):
        freq[word] = 0
    freq[word] = freq[word] + 1
    
def length(tree):
    if (isinstance(tree, ListType)):
        return len(tree)
    else:
        return 1


def tweak(tree, freq):
    """
    Traverses the given tree and replaces infrequent words with the symbol _RARE_
    Returns the modified tree.
    """
    if length(tree) == 1:
        return str(tree)
    elif length(tree) == 2:
        if is_fringe(tree):
            word = RARE_SYMBOL if freq[tree[1]] < THRESHOLD_FREQUENCY else tree[1]
            return [str(tree[0]), str(word)]
        else:
            return [tweak(tree[0], freq), tweak(tree[1], freq)]
    else:
       return [str(tree[0]), tweak(tree[1], freq), tweak(tree[2], freq)]


def rewrite_file(writer, filename):
    freq = compute_frequencies(filename)
    file = open(filename)
    for line in file:
        if line == None:
            continue                
        str = line.strip()
        tree = json.loads(str)
        tree_ = json.dumps(tweak(tree, freq))#new str
        writer.write("%s\n" %tree_)
    file.close()


def usage():
    print """
    python replace_infreq.py [input_file] > [output_file]
    Read in a gene tagged training input file and produce another
    gene tagged training input file where infrequent words have been
    replaced by _RARE_
    """


if __name__ == "__main__":

    if len(sys.argv) !=2: #Expect exactly one argument the training file
        usage()
        sys.exit(2)
    rewrite_file(sys.stdout, sys.argv[1])