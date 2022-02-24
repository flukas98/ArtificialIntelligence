using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace Lab1
{
    public class StateSpace
    {
        public string StartState { get; private set; }

        public List<string> EndStates { get; private set; }

        public Dictionary<string, List<Tuple<string, float>>> Transitions { get; private set; }

        public Dictionary<string, float> RealCostToEndState { get; private set; }

        public static StateSpace Create(string path)
        {
            var stateSpace = new StateSpace()
            {
                EndStates = new List<string>(),
                Transitions = new Dictionary<string, List<Tuple<string, float>>>()
            };

            try
            {
                int counter = 0;
                using (var sr = new StreamReader(path))
                {
                    string line;
                    while ((line = sr.ReadLine()) != null)
                    {
                        if (line.StartsWith("#"))
                        {
                            continue;
                        }
                        else if (counter == 0)
                        {
                            stateSpace.StartState = line;
                        }
                        else if (counter == 1)
                        {
                            stateSpace.EndStates.AddRange(line.Split(' '));
                        }
                        else
                        {
                            var split = line.Split(':');
                            var currentState = split[0];
                            if (split.Length > 1)
                            {
                                if (!String.IsNullOrEmpty(split[1]))
                                {
                                    var nextStatesString = split[1].Substring(1).Split(' ').ToList();
                                    var nextStatesTuple = new List<Tuple<string, float>>();

                                    foreach (var nextState in nextStatesString)
                                    {
                                        float.TryParse(nextState.Split(',')[1], out float cijena);
                                        var stanje = nextState.Split(',')[0];
                                        var tuple = new Tuple<string, float>(stanje, cijena);

                                        nextStatesTuple.Add(tuple);
                                    }
                                    stateSpace.Transitions.Add(currentState, nextStatesTuple);
                                }
                                else
                                {
                                    stateSpace.Transitions.Add(currentState, new List<Tuple<string, float>>());
                                }
                            }
                            else
                            {
                                throw new Exception();
                            }
                        }
                        counter++;
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
            }

            stateSpace.CalculateRealCostToEndState();

            return stateSpace;
        }

        public static StateSpace Create(string startState, List<string> endStates, Dictionary<string, List<Tuple<string, float>>> transitions)
        {
            var stateSpace = new StateSpace()
            {
                StartState = startState,
                EndStates = endStates,
                Transitions = transitions
            };

            stateSpace.CalculateRealCostToEndState();

            return stateSpace;
        }

        public void SetStartState(string startState)
        {
            if (String.IsNullOrEmpty(startState))
            {
                throw new ArgumentNullException(nameof(startState), "Start state cannot be null or empty.");
            }

            this.StartState = startState;
        }

        public void SetEndStates(List<string> endStates)
        {
            if (endStates is null || !endStates.Any())
            {
                throw new ArgumentNullException(nameof(endStates), "End states cannot be null or empty.");
            }

            this.EndStates = endStates;

            this.CalculateRealCostToEndState();
        }

        public void PrintStateSpace()
        {
            Console.WriteLine("Start state: {0}", StartState);
            Console.Write("End state(s): ");
            foreach (var endState in EndStates)
            {
                var endStatePrint = string.Concat("['", endState, "']");
                if(endState != EndStates.Last())
                {
                    endStatePrint = string.Concat(endStatePrint, ", ");
                }
                Console.Write(endStatePrint);
            }
            Console.WriteLine();
            Console.WriteLine("State space size: {0}", Transitions.Keys.Count);
            Console.WriteLine("Total transitions: {0}", Transitions.Sum(x => x.Value.Count));
            Console.WriteLine();
        }

        private void CalculateRealCostToEndState()
        {
            var startState = this.StartState;
            var states = this.Transitions.Keys.ToList();
            var realStateCost = new Dictionary<string, float>();
            foreach (var state in states)
            {
                this.SetStartState(state);
                var ucsResult = SearchAlgorithmResult.UCS(this);
                var realCost = ucsResult.TotalCost;
                realStateCost.Add(state, realCost);
            }

            this.SetStartState(startState);
            this.RealCostToEndState = realStateCost;
        }
    }
}
