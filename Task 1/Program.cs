using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;

namespace Lab1
{
    class Program
    {
        static void Main(string[] args)
        {
            var stateSpacePath = @"PATH\lab1_state_spaces_and_heuristics\istra.txt";                                    //ENTER PATH
            var stateSpace = StateSpace.Create(stateSpacePath);

            var heuristicPath = @"PATH\lab1_state_spaces_and_heuristics\istra_heuristic.txt";                           //ENTER PATH
            var heuristic = Heuristic.Create(heuristicPath);

            var pessimisticHeuristicPath = @"PATH\lab1_state_spaces_and_heuristics\istra_pessimistic_heuristic.txt";    //ENTER PATH
            var pessimisticHeuristic = Heuristic.Create(pessimisticHeuristicPath);

            stateSpace.PrintStateSpace();

            var bfsResult = SearchAlgorithmResult.BFS(stateSpace);
            bfsResult.PrintResult();

            var ucsResult = SearchAlgorithmResult.UCS(stateSpace);
            ucsResult.PrintResult();

            var a_star1Result = SearchAlgorithmResult.A_star(stateSpace, heuristic);
            a_star1Result.PrintResult();

            var a_star2Result = SearchAlgorithmResult.A_star(stateSpace, pessimisticHeuristic);
            a_star2Result.PrintResult();

            heuristic.CheckHeuristicOptimisticness(stateSpace);
            heuristic.CheckHeuristicConsistency(stateSpace);

            pessimisticHeuristic.CheckHeuristicOptimisticness(stateSpace);
            pessimisticHeuristic.CheckHeuristicConsistency(stateSpace);
        }
    }
}
