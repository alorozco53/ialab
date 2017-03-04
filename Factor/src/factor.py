#!/usr/bin/python

import argparse
import re

def parse_arg():
    parser = argparse.ArgumentParser(description='Read variables and probabilities.')
    parser.add_argument('CPT_FILE', help='A file containing a CPT specification.')
    args = parser.parse_args()
    return args.CPT_FILE

def read_cpt(cpt_file):
    '''
    Reads a CPT specification and returns
    a tuple with its contents
    '''
    cpt_string = ''
    with open(cpt_file, 'r') as cfile:
        cpt_string = cfile.read().replace('\n', '').replace(' ', '')
    vars_match = re.search('\[([a-z]|[A-Z]|[0-9]|:|,|\{|\})*\]', cpt_string)\
                   .group(0).replace('[', '').replace(']', '')
    probs_match = cpt_string.replace(vars_match, '').replace('[', '').replace(']', '')
    print(re.search('\{([a-z]|[A-Z]|[0-9]|:|,)*\}', vars_match))


def fresh_var(dep_var, used_vars):
    '''
    Creates a fresh string not contained in used_vars,
    starting from string dep_var
    '''
    i = 0
    fresh = dep_var + str(i)
    while(fresh in used_vars):
        i += 1
        fresh = fresh.replace(str(i - 1), str(i))
    return fresh
    
class Factor():
    '''
    Class that implements a conditional probability table (CPT),
    along with its operations.
    Variables are stored in a dictionary with their names as keys and
    list of values as values. Probabilities are stored in a list of
    3-tuples, where each one has the form (A, B, v) to model P(A | B) = v.
    If B is the empty list, then we model the joint probability of all variables,
    given in A, that is, P(A) = v.
    Each A and B are lists of 2-tuples of the form (VariableName, VariableValue).
    '''
    def __init__(self, vars, probs):
        self.variables = vars
        self.probabilities = probs

    def product(self, cpt):
        '''
        Performs the CPT multiplication of self with cpt
        '''

        # check if there are repeated variables with different values
        repeated = 'foo'
        renamed_var = None
        while repeated != '':
            repeated = ''
            print(cpt.probabilities)
            for var, val in self.variables.items():
                if var in cpt.variables.keys():
                    cond = set(val) <= set(cpt.variables[var])\
                           and set(cpt.variables[var]) <= set(val)
                    if cond:
                        # rename repeated variable
                        all_keys = list(cpt.variables.keys())\
                                   + list(self.variables.keys())
                        renamed_var = fresh_var(var, all_keys)
                        self.variables[renamed_var] = val
                        repeated = var
                        break
            if repeated != '':
                # change all instances of the repeated variable in self
                # rep_index = self.variables.index(repeated)
                self.variables.pop(repeated)
                for prob in self.probabilities:
                    for i, v in enumerate(prob[0] + prob[1]):
                        if v[0] == repeated:
                            aux = v[1]
                            prob.pop(i)
                            prob.insert(i, (renamed_var, aux))

        print(self.probabilities)
        

def main():
    cpt_file = parse_arg()
    # read_cpt(cpt_file)
    # {P(B = 0) = 0.25,P(B = 1) = 0.75},{P(C = 0 | A = 0, B = 0) = 0.25,P(C = 0 | A = 0, B = 1) = 0.15,P(C = 0 | A = 1,B = 0) = 0.35, P(C =
    # 0 | A = 1,B = 1) = 0.1,P(C = 1 | A = 0, B = 0) = 0.65, P(C = 1 | A = 0, B = 1) = 0.80, P(C = 1 | A = 1,B = 0) = 0.60,
    # P(C = 1 | A = 1,B
    # = 1) = 0.8, P (C = 2 | A = 0, B = 0) = 0.10, P (C = 2 | A = 0, B = 1) = 0.05, P (C = 2 | A = 1, B = 0) = 0.05,
    # P (C = 2 | A = 1, B = 1)
    # = 0.1}
    vars0 = {'A' : [0,1], 'B' : [0,1], 'C' :[0,1,2]}
    probs0 = [
        ([('A', 0)], [], 0.1),
        ([('A', 1)], [], 0.9)
    ]
    vars1 = {'A' : [1,2], 'B' : [0,1], 'C' :[0,1,2]}
    cpt0 = Factor(vars0, probs0)
    cpt1 = Factor(vars1, probs0)
    cpt0.product(cpt1)
    
if __name__ == '__main__':
    main()
