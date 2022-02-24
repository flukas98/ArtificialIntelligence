using System;
using System.Collections.Generic;
using System.Linq;

namespace Lab1
{
    public class SearchAlgorithmResult
    {
        public int StatesVisited { get; private set; }

        public float TotalCost { get; private set; }

        public List<string> Path { get; private set; }

        public SearchAlgorithmType Type { get; private set; }

        public static SearchAlgorithmResult BFS(StateSpace stateSpace)
        {
            var statesVisited = 0;
            var totalCost = 0f;
            var path = new List<string>();

            var open = new List<Node>() { Node.Create(stateSpace.StartState, totalCost, null) };
            var visitedStates = new List<string>();
            Node endNode = null;

            while (open.Any())
            {
                var currentNode = open[0];
                var currentState = currentNode.State;
                open.RemoveAt(0);

                if (!visitedStates.Contains(currentState))
                {
                    statesVisited++;
                    if (stateSpace.EndStates.Contains(currentState))
                    {
                        endNode = currentNode;
                        break;
                    }
                    else
                    {
                        visitedStates.Add(currentState);
                    }
                }
                else
                {
                    continue;
                }

                var successors = new List<Node>(stateSpace.Transitions[currentNode.State].Select(x => Node.Create(state: x.Item1, totalCost: currentNode.TotalCost + 1, parentNode: currentNode)));
                open.AddRange(successors);
            }

            var currentPathNode = endNode;
            while (currentPathNode.State != stateSpace.StartState)
            {
                path.Add(currentPathNode.State);
                currentPathNode = currentPathNode.ParentNode;
            }
            path.Add(stateSpace.StartState);
            path.Reverse();

            return new SearchAlgorithmResult()
            {
                StatesVisited = statesVisited,
                TotalCost = endNode.TotalCost,
                Path = path,
                Type = SearchAlgorithmType.BFS
            };
        }

        public static SearchAlgorithmResult UCS(StateSpace stateSpace)
        {
            var statesVisited = 0;
            var totalCost = 0f;
            var path = new List<string>();

            var open = new List<Node>() { Node.Create(stateSpace.StartState, totalCost, null) };
            var visitedStates = new List<string>();
            Node endNode = null;

            while (open.Any())
            {
                var currentNode = open[0];
                var currentState = currentNode.State;
                open.RemoveAt(0);

                if (!visitedStates.Contains(currentState))
                {
                    statesVisited++;
                    if (stateSpace.EndStates.Contains(currentState))
                    {
                        endNode = currentNode;
                        break;
                    }
                    else
                    {
                        visitedStates.Add(currentState);
                    }
                }
                else
                {
                    continue;
                }

                var successors = new List<Node>(stateSpace.Transitions[currentNode.State].Select(x => Node.Create(state: x.Item1, totalCost: currentNode.TotalCost + x.Item2, parentNode: currentNode)));
                foreach (var succesor in successors)
                {
                    var isAdded = false;
                    foreach (var node in open)
                    {
                        if (node.TotalCost > succesor.TotalCost)
                        {
                            open.Insert(open.IndexOf(node), succesor);
                            isAdded = true;
                            break;
                        }
                    }
                    if (!isAdded)
                    {
                        open.Add(succesor);
                    }
                }
            }

            var currentPathNode = endNode;
            while (currentPathNode.State != stateSpace.StartState)
            {
                path.Add(currentPathNode.State);
                currentPathNode = currentPathNode.ParentNode;
            }
            path.Add(stateSpace.StartState);
            path.Reverse();

            return new SearchAlgorithmResult()
            {
                StatesVisited = statesVisited,
                TotalCost = endNode.TotalCost,
                Path = path,
                Type = SearchAlgorithmType.UCS
            };
        }

        public static SearchAlgorithmResult A_star(StateSpace stateSpace, Heuristic heuristic)
        {
            var statesVisited = 0;
            var totalCost = 0f;
            var path = new List<string>();

            var open = new List<Node>() { Node.Create(stateSpace.StartState, totalCost, null) };
            var visitedStates = new List<string>();
            Node endNode = null;

            while (open.Any())
            {
                var currentNode = open[0];
                var currentState = currentNode.State;
                open.RemoveAt(0);

                if (!visitedStates.Contains(currentState))
                {
                    statesVisited++;
                    if (stateSpace.EndStates.Contains(currentState))
                    {
                        endNode = currentNode;
                        break;
                    }
                    else
                    {
                        visitedStates.Add(currentState);
                    }
                }
                else
                {
                    continue;
                }

                var successors = new List<Node>(stateSpace.Transitions[currentNode.State].Select(x => Node.Create(state: x.Item1, totalCost: currentNode.TotalCost + x.Item2, parentNode: currentNode)));
                foreach (var succesor in successors)
                {
                    var isAdded = false;
                    foreach (var node in open)
                    {
                        if (node.TotalCost + heuristic.StateHeuristics[node.State] > succesor.TotalCost + heuristic.StateHeuristics[succesor.State])
                        {
                            open.Insert(open.IndexOf(node), succesor);
                            isAdded = true;
                            break;
                        }
                    }
                    if (!isAdded)
                    {
                        open.Add(succesor);

                    }
                }
            }

            var currentPathNode = endNode;
            while (currentPathNode.State != stateSpace.StartState)
            {
                path.Add(currentPathNode.State);
                currentPathNode = currentPathNode.ParentNode;
            }
            path.Add(stateSpace.StartState);
            path.Reverse();

            return new SearchAlgorithmResult()
            {
                StatesVisited = statesVisited,
                TotalCost = endNode.TotalCost,
                Path = path,
                Type = SearchAlgorithmType.A_star
            };
        }

        public void PrintResult()
        {
            switch (Type)
            {
                case SearchAlgorithmType.BFS:
                    Console.WriteLine("Running BFS:");
                    break;
                case SearchAlgorithmType.UCS:
                    Console.WriteLine("Running UCS:");
                    break;
                case SearchAlgorithmType.A_star:
                    Console.WriteLine("Running A*:");
                    break;
                default:
                    throw new Exception("Invalid search algorithm.");
            }
            Console.WriteLine("States visited = {0}", StatesVisited);
            Console.WriteLine("Found path of length {0} with total cost {1}:", Path.Count, TotalCost);
            foreach (var state in Path)
            {
                Console.Write(state);
                if (state != Path.Last())
                {
                    Console.WriteLine(" =>");
                }
                else
                {
                    Console.WriteLine();
                }
            }
            Console.WriteLine();
        }
    }
}
