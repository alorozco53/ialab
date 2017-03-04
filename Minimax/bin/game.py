#!/usr/bin/python

import copy
import math

"""
This script implements all the necessary functions to build the game tree.
It assumes Max's symbol is 'X' and Min's is 'O'
:author AlOrozco53:
"""

class State:
    """
    Class that abstracts a state of the tic-tac-toe game
    """

    def __init__(self, play, mat=None,):
        """
        By default, initializes an empty state;
        otherwise, assigns the given matrix
        :param mat: new state 3X3 matrix
        :param play: true iff current player is Max
        """
        if mat is None:
            self.matrix = [['-', '-', '-'],
                           ['-', '-', '-'],
                           ['-', '-', '-']]
        else:
            self.matrix = copy.deepcopy(mat)
        self.player = play
        self.successors = None

    def has_max_won(self):
        """
        Decides whether Max wins according to the current state
        :returns int: 0 if it's a tie, 1 if Max wins, -1 if Min wins,
        2 if it cannot be determined
        """
        # check if someone has won
        cond_max = (self.mat[0][0] == self.mat[1][0] == self.mat[2][0] and self.mat[0][0] == 'X')\
               or (self.mat[0][1] == self.mat[1][1] == self.mat[2][1] and self.mat[1][1] == 'X')\
               or (self.mat[0][2] == self.mat[1][2] == self.mat[2][2] and self.mat[2][2] == 'X')\
               or (self.mat[0][0] == self.mat[0][1] == self.mat[0][2] and self.mat[0][0] == 'X')\
               or (self.mat[1][0] == self.mat[1][1] == self.mat[1][2] and self.mat[1][1] == 'X')\
               or (self.mat[2][0] == self.mat[2][1] == self.mat[2][2] and self.mat[2][2] == 'X')\
               or (self.mat[0][0] == self.mat[1][1] == self.mat[2][2] and self.mat[0][0] == 'X')\
               or (self.mat[2][2] == self.mat[1][1] == self.mat[0][2] and self.mat[1][1] == 'X')
        if cond_max:
            return 1
        cond_min = (self.mat[0][0] == self.mat[1][0] == self.mat[2][0] and self.mat[0][0] == 'O')\
                   or (self.mat[0][1] == self.mat[1][1] == self.mat[2][1] and self.mat[1][1] == 'O')\
                   or (self.mat[0][2] == self.mat[1][2] == self.mat[2][2] and self.mat[2][2] == 'O')\
                   or (self.mat[0][0] == self.mat[0][1] == self.mat[0][2] and self.mat[0][0] == 'O')\
                   or (self.mat[1][0] == self.mat[1][1] == self.mat[1][2] and self.mat[1][1] == 'O')\
                   or (self.mat[2][0] == self.mat[2][1] == self.mat[2][2] and self.mat[2][2] == 'O')\
                   or (self.mat[0][0] == self.mat[1][1] == self.mat[2][2] and self.mat[0][0] == 'O')\
                   or (self.mat[2][2] == self.mat[1][1] == self.mat[0][2] and self.mat[1][1] == 'O')
        if cond_min:
            return 2
        else:
            # check if no further movement can be done
            flag = True
            for row in self.matrix:
                if '-' in row:
                    flag = False
            if flag:
                return 0
            else:
                return 2

    def is_terminal(self):
        """
        Decides if the current state is terminal
        :returns boolean: true iff it's the case
        """
        return self.has_max_won in [0, 1, -1]

    def utility(self):
        """
        Returns the utility value for the current state.
        This function should be called only if the current state is terminal!
        :returns int: 1 if Max won, -1 if Min won, 0 if it's a tie
        """
        if not self.is_terminal():
            raise Error("The current state is not terminal!!")
        else:
            return self.has_max_won()

    def compute_successors(self):
        """
        Computes the successors of the given state
        """
        if self.is_terminal():
            self.successors = None
        else:
            next_ch = 'X' if not self.player else 'O'
            next_play = not self.player
            succs = []
            for i in range(3):
                for j in range(3):
                    if self.matrix[i][j] == '-':
                        succ = State(next_play, self.matrix)
                        succ.matrix[i][j] = next_ch
                        succs.append(succ)
            self.successors = succs

    def __str__(self):
        string = ''
        for row in self.matrix:
            for elem in row:
                string += elem + ' '
            string += '\n'
        return string


class TicTacToeTree:
    """
    Class that implements the whole game tree for the
    tic-tac-toe game
    """

    def __init__(self, player, state=None):
        """
        Default constructor
        :player: true if the current player is Max
        :state : 3X3 matrix
        """
        self.node = State(player, state)
        self.value = -1
        self.node.compute_successors()

    def build_tree(self):
        """
        Builds the tree recursively
        """
        if self.node.successors is not None:
            self.children = []
            for succ in self.node.successors:
                tree = TicTacToeTree(succ.player, succ.matrix)
                self.children.append(tree)
                tree.build_tree()
        else:
            self.children = None

    def set_value(self):
        """
        Sets the minimax value for each node in the tree rooted in
        the current node
        """
        if self.node.is_terminal():
            return self.node.utility()
        else:
            # if it's a max state
            if self.node.player:
                self.value = - math.inf
                for child in self.children:
                    self.value = max(self.value, child.set_value())
                return self.value
            # if it's a min state
            else:
                self.value = math.inf
                for child in self.children:
                    self.value = max(self.value, child.set_value())
                return self.value

    def minimax_decision(self):
        """
        Computes the best decision for the current player
        :return matrix: how the state matrix should look like after the next
        (optimal) move
        """
        v = self.set_value()
        optimals = [ch.node for ch in self.children if ch.value == v]
        return optimals[len(optimals) - 1]
