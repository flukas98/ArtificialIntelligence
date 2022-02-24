using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace Lab1
{
    public class Heuristic
    {
        public string Name { get; private set; }

        public Dictionary<string, float> StateHeuristics { get; private set; }

        public static Heuristic Create(string path)
        {
            var heuristic = new Heuristic()
            {
                StateHeuristics = new Dictionary<string, float>()
            };

            try
            {
                using (var sr = new StreamReader(path))
                {
                    heuristic.Name = path.Split('\\').Last();
                    string line;
                    while ((line = sr.ReadLine()) != null)
                    {
                        if (line.StartsWith("#"))
                        {
                            continue;
                        }
                        else
                        {

                            var state = line.Split(':')[0];
                            float.TryParse(line.Split(':')[1].Substring(1), out float stateHeuristic);

                            heuristic.StateHeuristics.Add(state, stateHeuristic);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
            }

            return heuristic;
        }

        public void CheckHeuristicOptimisticness(StateSpace stateSpace)
        {
            Console.WriteLine("Checking if heuristic '{0}' is optimistic.", this.Name);
            var isOptimistic = true;
            foreach (var state in this.StateHeuristics)
            {
                if (state.Value > stateSpace.RealCostToEndState[state.Key])
                {
                    Console.WriteLine("\t[ERR] h({0}) > h*: {1} > {2}", state.Key, state.Value, stateSpace.RealCostToEndState[state.Key]);
                    isOptimistic = false;
                }
            }
            if (isOptimistic)
            {
                Console.WriteLine("Heuristic '{0}' is optimistic.", this.Name);
            }
            else
            {
                Console.WriteLine("Heuristic '{0}' is not optimistic.", this.Name);
            }
            Console.WriteLine();
        }

        public void CheckHeuristicConsistency(StateSpace stateSpace)
        {
            Console.WriteLine("Checking if heuristic '{0}' is consistent.", this.Name);
            var isConsistent = true;
            foreach (var state in this.StateHeuristics)
            {
                foreach (var successor in stateSpace.Transitions[state.Key])
                {
                    if (state.Value > this.StateHeuristics[successor.Item1] + successor.Item2)
                    {
                        Console.WriteLine("\t[ERR] h({0}) > h({1}) + c: {2} > {3} + {4}", state.Key, successor.Item1, state.Value, this.StateHeuristics[successor.Item1], successor.Item2);
                        isConsistent = false;
                    }
                }
            }
            if (isConsistent)
            {
                Console.WriteLine("Heuristic '{0}' is consistent.", this.Name);
            }
            else
            {
                Console.WriteLine("Heuristic '{0}' is not consistent.", this.Name);
            }
            Console.WriteLine();
        }

        public void PrintHeuristic()
        {
            foreach (var state in StateHeuristics)
            {
                var heuristic = state.Key + ", " + state.Value;
                Console.WriteLine(heuristic);
            }
        }
    }
}
