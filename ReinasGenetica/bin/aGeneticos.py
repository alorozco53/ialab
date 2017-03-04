#!/usr/bin/python

import argparse
import random
import copy

"""
Implementation of the n-queen problem using a
genetic algorithm
:author AlOrozco53:
"""


class Phenotype:
    """
    Class that abstracts a phenotype (chess grid)
    """

    def __init__(self, n, gen_array):
        """
        Initializes the class with the given genotype array;
        :param gen_array: array of length n that represents
        the position of the n queens
        :param n: size of the chess grid
        """
        self.grid_size = n
        self.genotype = gen_array

    def attack_number(self):
        """
        Computes the number of queen attacks that are involved in this
        genotype. It assumes that there are no column attacks, given
        the chosen representation.
        :returns int: the number of attacks
        """
        attacks = 0

        # compute row attacks
        for i in range(self.grid_size):
            reps = [x for x in self.genotype if x == i + 1]
            if len(reps) > 1:
                attacks += len(reps) - 1

        # compute diagonal attacks
        for col, queen in enumerate(self.genotype):
            if col < self.grid_size - 1:
                index = col + 1
                up = queen + 1
                down = queen + 1
                while index < self.grid_size - 1:
                    if self.genotype[index] == up or\
                       self.genotype[index] == down:
                        attacks += 1
                    index += 1
                    up -= 1
                    down += 1

        return attacks

    def fitness(self):
        """
        Computes the fitness for the phenotype.
        :returns float: the fitness
        """
        an = self.attack_number()
        if an > 0:
            return (1.0 / self.attack_number()) * 100.0
        else:
            return 101.0

    def recombine(self, phen):
        """
        Performs a recombination with the given phenotype,
        and returns a new one
        :param phen:
        :returns recomb_phen: recombined phenotype
        """
        cut = random.randint(0, self.grid_size - 1)
        recomb_gen = self.genotype[0:cut] + phen.genotype[cut:phen.grid_size]
        return Phenotype(self.grid_size, recomb_gen)

    def mutate(self):
        """
        Randomly performs a mutation. Each queen's position
        has a probability of 0.2 of changing
        """
        for queen in self.genotype:
            if random.random() <= 0.2:
                old_val = queen
                new_val = random.randint(1, self.grid_size)
                while new_val == old_val:
                    new_val = random.randint(1, self.grid_size)
                queen = new_val

    def __str__(self):
        return str(self.genotype)


class Population:
    """
    This class performs most of the most important aspects of
    the genetic algorithm
    """

    def __init__(self, psize, gsize):
        """
        Initializes the class with a given population size
        and grid size. It also generates the population randomly.
        :param psize:
        :param gsize:
        """
        self.population_size = psize
        self.grid_size = gsize
        self.population = []
        for _ in range(psize):
            chromosome = []
            for _ in range(gsize):
                chromosome.append(random.randint(1, gsize))
            self.population.append(Phenotype(gsize, chromosome))

    def comp_fitnesses(self):
        """
        Assigns normalized fitnesses to the population, in a
        sorted order
        """
        fitnesses = [p.fitness() for p in self.population]
        total_fitness = sum(fitnesses)
        self.norm_fitnesses = []
        for i, pop in enumerate(self.population):
            fit = fitnesses[i]
            self.norm_fitnesses.append([pop, float(fit) / float(total_fitness)])
        self.norm_fitnesses.sort(key=lambda x: x[1])

    def nat_selection(self):
        """
        Performs a step in natural selection. Selects a fit individual
        randomly
        :return ind: the two most fit phenotypes in the current
        population
        """
        nf_copy = copy.deepcopy(self.norm_fitnesses)

        # compute the cumulative distribution
        cum = 0
        for elem in nf_copy:
            cum += elem[1]
            elem[1] = cum

        # chose a sufficiently fit individual randomly
        rvar = random.random()
        ind = nf_copy[0][0]
        for i in range(len(nf_copy)):
            if nf_copy[i][1] <= rvar <= nf_copy[i+1][1]:
                ind = nf_copy[i+1][0]
                break

        return ind

    def elite(self, k):
        """
        Returns the k best solutions so far
        :returns [Phenotype]:
        """
        elites = []
        pop_copy = copy.deepcopy(self.population)
        for _ in range(k):
            elite = pop_copy[0]
            for pop in pop_copy:
                if pop.attack_number() < elite.attack_number():
                    elite = pop
            pop_copy.remove(elite)
            elites.append(elite)
        return elites

    def best_solution(self):
        """
        Returns the best solution so far and its fitness
        :returns Phenotype:
        :returns float: fitness
        """
        best = self.population[0]
        for pop in self.population:
            if pop.attack_number() < best.attack_number():
                best = pop
        return best, best.fitness()

    def gen_algorithm(self, limit):
        """
        Runs the algorithm until an optimal solution is found or
        until the limit of generations has been reached
        :param limit: limit of generations
        """
        gen_counter = 1
        solution_found = False
        while gen_counter < limit and not solution_found:
            self.comp_fitnesses()
            new_population = self.elite(1)
            new_psize = 1
            while new_psize < self.population_size:
                ind1 = self.nat_selection()
                ind2 = self.nat_selection()
                offspring = ind1.recombine(ind2)
                offspring.mutate()
                new_population.append(offspring)
                new_psize += 1
            self.population = new_population
            sol, fit = self.best_solution()
            if gen_counter % 50 == 0:
                print('current best solution at the ' + str(gen_counter) + 'th iteration is',
                      str(sol), '| fitness:', str(fit))
            solution_found = sol.attack_number() == 0
            gen_counter += 1
        print('\n', 'program ended at the ' + str(gen_counter) + 'th iteration', '\n')
        sol, fit = self.best_solution()
        print('overall best solution is',
              str(sol), '| fitness:', str(fit))


def parse_args():
    """
    Parses the program arguments
    """
    parser = argparse.ArgumentParser(description='Parse inputs.')
    parser.add_argument('-n', help='Size of the chess grid.', default=8,
                        type=int)
    parser.add_argument('-i', help='Maximum number of iterations.', default=500,
                        type=int)
    args = parser.parse_args()
    return args.n, args.i


def main():
    gsize, iters = parse_args()
    print('solving the ' + str(gsize) + '-queen problem')
    print('in', str(iters), 'iterations...')
    population = Population(50, gsize)
    population.gen_algorithm(iters)

if __name__ == '__main__':
    main()
