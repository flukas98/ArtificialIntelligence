using System;
using System.Collections.Generic;
using System.Linq;

namespace Lab1
{
    public class Node
    {
        public string State { get; private set; }

        public float TotalCost { get; private set; }

        public Node ParentNode { get; private set; }

        public static Node Create(string state, float totalCost, Node parentNode)
        {
            var node = new Node()
            {
                State = state,
                TotalCost = totalCost,
                ParentNode = parentNode
            };

            return node;
        }
    }
}
