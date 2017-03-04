#!/usr/bin/python

import argparse
from game import *

"""
This is the main script that executes the program
:author AlOrozco53:
"""

def parse_args():
    """
    Parses the tic-tac-toe state stored in the file whose name
    is indicated as argument
    :return state_matrix: a matrix representation of the tic-tac-toe state
    :return player: true iff current player to make a move is Max
    """
    parser = argparse.ArgumentParser(description='Parse inputs.')
    parser.add_argument('-state', help='State of the game to be read.')
    parser.add_argument('-player',
                        help='Player to make the move in the current grid.',
                        default='max')
    args = parser.parse_args()

    # build the matrix
    state_matrix = []
    ignore = ['\t', '\n', ' ']
    with open(args.state, 'r') as state_file:
        for line in state_file:
            state_matrix.append([ch for ch in line if ch not in ignore])

    return state_matrix, args.player != 'max'

def main():
    """
    Main method
    """
    state_to_check, player = parse_args()
    print('state matrix read:')
    print(str(State(player, state_to_check)))
    print('the next player to move is:', ('Max' if not player else 'Min'))
    tree = TicTacToeTree(player, state_to_check)
    print('building the tree...')
    tree.build_tree()
    print('computing the minimax decision...')
    print(tree.minimax_decision())


if __name__ == '__main__':
    main()
