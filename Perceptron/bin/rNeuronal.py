#!/usr/bin/python

import numpy as np
import argparse

"""
This script implements a perceptron that is able to
learn to compute AND and OR gates with two inputs
:author AlOrozco53:
"""

class Perceptron:
    """
    Class that abstracts a perceptron
    """
    def __init__(self, initial_W):
        """
        Initializes a perceptron by copying the given weights
        :param initial_W:
        """
        self.W = np.copy(initial_W)

    def step_eval(self, X, threshold):
        """
        Evaluates the perceptron using the step function
        :param X: vector to evaluate
        :param threshold: used in the step function
        :returns int: -1 or 1
        """
        return 1 if np.dot(self.W, X.T) > threshold else 0

    def train(self, y, X, alpha, yd):
        """
        Performs a step in the training by updating the weights
        :param y: the perceptron's output at the nth iteration
        :param X: the nth iteration input training vector
        :param alpha: learning rate
        :param yd: desired output for the current input
        :returns boolean: true iff the weights were not updated
        """
        err = -1 if yd == 0 else 1
        if y != yd:
            self.W += err * alpha * X
            return False
        else:
            return True


def parse_args():
    """
    Parses the training set stored in the file whose name
    is indicated as argument
    :return array: a numpy representation of the training set
    """
    parser = argparse.ArgumentParser(description='Parse inputs.')
    parser.add_argument('-t', help='Training set file path.')
    args = parser.parse_args()

    # parse the training set
    ts = []
    with open(args.t, 'r') as ts_file:
        for line in ts_file:
           ts.append([int(t.strip()) for t in line.split(' ')])

    return np.array(ts)

def main():
    """
    Main method
    """
    ts = parse_args()
    print('training set to be used:')
    print(ts)

    # perform the training until convergence
    alpha = 3
    print('training a perceptron...')
    weights = np.random.rand(ts.shape[1] - 1) - 0.5
    threshold = np.random.rand(1)[0]
    perc = Perceptron(weights)
    answers = [False]
    while not all(answers):
        answers = []
        for i in range(ts.shape[0]):
            X = ts[i:i+1, 0:3][0]
            y = perc.step_eval(X, threshold)
            ans = perc.train(y, X, alpha, ts[i:i+1, 3:4][0])
            answers.append(ans)

    try:
        while True:
            query = input('Make a query! Enter two numbers separated by a ,\n')
            parsed_query = [1] + [int(q.strip()) for q in query.strip().split(',')]
            print(perc.step_eval(np.array(parsed_query), threshold))
    except:
        print('BYE!!')

if __name__ == '__main__':
    main()
