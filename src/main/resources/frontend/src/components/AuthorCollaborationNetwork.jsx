import React, { useState, useEffect } from 'react';
import { ForceGraph2D } from 'react-force-graph';
import { Card, CardHeader, CardTitle, CardContent } from "./ui/card";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Alert, AlertDescription } from "./ui/alert";
import * as XLSX from 'xlsx';
import { ZoomIn, ZoomOut, UserSquare2 } from 'lucide-react';
import path from 'path';

 const createBST = () => {
   class TreeNode {
     constructor(author) {
       this.author = author;
       this.left = null;
       this.right = null;
     }
   }

   class BST {
     constructor() {
       this.root = null;
     }

     insert(author) {
       const node = new TreeNode(author);
       if (!this.root) {
         this.root = node;
         return;
       }

       let current = this.root;
       while (true) {
         if (author.id < current.author.id) {
           if (!current.left) {
             current.left = node;
             break;
           }
           current = current.left;
         } else {
           if (!current.right) {
             current.right = node;
             break;
           }
           current = current.right;
         }
       }
     }

     inorderTraversal(node = this.root, result = []) {
       if (node) {
         this.inorderTraversal(node.left, result);
         result.push(node.author);
         this.inorderTraversal(node.right, result);
       }
       return result;
     }
   }

   // Seçili yazarın işbirlikçilerinden BST oluştur
   const collaborators = graphData.links
     .filter(link => link.source === operationPanel.author1 || link.target === operationPanel.author1)
     .map(link => {
       const collaborator = link.source === operationPanel.author1 ? link.target : link.source;
       const node = graphData.nodes.find(n => n.id === collaborator);
       return node;
     });

   const bst = new BST();
   collaborators.forEach(author => bst.insert(author));

   setOperationPanel(prev => ({
     ...prev,
     result: bst.inorderTraversal()
   }));
 };

 const createCollaboratorQueue = () => {
   class PriorityQueue {
     constructor() {
       this.values = [];
     }

     enqueue(author, priority) {
       this.values.push({author, priority});
       this.sort();
     }

     dequeue() {
       return this.values.shift();
     }

     sort() {
       this.values.sort((a, b) => b.priority - a.priority);
     }
   }

   const queue = new PriorityQueue();
   const author = operationPanel.author1;

   // İşbirlikçileri makale sayılarına göre kuyruğa ekle
   graphData.links
     .filter(link => link.source === author || link.target === author)
     .forEach(link => {
       const collaborator = link.source === author ? link.target : link.source;
       const node = graphData.nodes.find(n => n.id === collaborator);
       queue.enqueue(node, node.articles.length);
     });

   // Kuyruk içeriğini sonuç olarak göster
   setOperationPanel(prev => ({
     ...prev,
     result: queue.values
   }));
 };

 const AuthorCollaborationNetwork = () => {
 const [graphData, setGraphData] = useState({ nodes: [], links: [] });
 const [selectedNode, setSelectedNode] = useState(null);
 const [zoomLevel, setZoomLevel] = useState(1);
 const [operationPanel, setOperationPanel] = useState({
   type: null,
   author1: '',
   author2: '',
   result: null
 });

 useEffect(() => {
   loadExcelData();
 }, []);

 const loadExcelData = async () => {
   try {
     const data = await (await fetch('/PROLAB 3 - GÜNCEL DATASET.xlsx')).arrayBuffer();
     const workbook = XLSX.read(new Uint8Array(data));
     const sheetName = workbook.SheetNames[0];
     const jsonData = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);
     console.log('Data loaded:', jsonData[0]);
     processData(jsonData);
   } catch (error) {
     console.error('Error:', error);
   }
 };

 const processData = (data) => {
   try {
     const nodes = new Map();
     const links = new Map();

     console.log("Raw data sample:", data.slice(0, 3));

     data.forEach(row => {
       const mainAuthor = row['Author Name']?.trim();
       const coAuthors = row['Co-authors']?.split(',').map(a => a.trim()).filter(Boolean) || [];
       const doi = row['DOI'];
       const title = row['Paper Title'];

       if (!mainAuthor) return;

       if (!nodes.has(mainAuthor)) {
         nodes.set(mainAuthor, {
           id: mainAuthor,
           articles: [{ doi, title }],
           size: 10,
           color: '#1e40af'
         });
       } else {
         nodes.get(mainAuthor).articles.push({ doi, title });
       }

       coAuthors.forEach(coAuthor => {
         if (!coAuthor) return;

         if (!nodes.has(coAuthor)) {
           nodes.set(coAuthor, {
             id: coAuthor,
             articles: [{ doi, title }],
             size: 10,
             color: '#1e40af'
           });
         } else {
           nodes.get(coAuthor).articles.push({ doi, title });
         }

         const linkId = [mainAuthor, coAuthor].sort().join('--');
         if (!links.has(linkId)) {
           links.set(linkId, {
             source: mainAuthor,
             target: coAuthor,
             weight: 1
           });
         } else {
           links.get(linkId).weight++;
         }
       });
     });

     setGraphData({
       nodes: Array.from(nodes.values()),
       links: Array.from(links.values())
     });
   } catch (error) {
     console.error("Data processing error:", error);
   }
 };

 const findShortestPath = (author1, author2) => {
   const distances = new Map();
   const previous = new Map();
   const nodes = graphData.nodes.map(n => n.id);
   nodes.forEach(node => distances.set(node, Infinity));
   distances.set(author1, 0);

   const unvisited = new Set(nodes);

   while (unvisited.size > 0) {
     const current = Array.from(unvisited)
       .reduce((min, node) =>
         distances.get(node) < distances.get(min) ? node : min
       );

     if (current === author2) break;
     unvisited.delete(current);

     const neighbors = graphData.links
       .filter(link => link.source === current || link.target === current)
       .map(link => link.source === current ? link.target : link.source);

     neighbors.forEach(neighbor => {
       if (!unvisited.has(neighbor)) return;
       const link = graphData.links.find(l =>
         (l.source === current && l.target === neighbor) ||
         (l.target === current && l.source === neighbor)
       );
       const distance = distances.get(current) + 1/link.weight;
       if (distance < distances.get(neighbor)) {
         distances.set(neighbor, distance);
         previous.set(neighbor, current);
       }
     });
   }

   const path = [];
   let current = author2;
   while (current) {
     path.unshift(current);
     current = previous.get(current);
   }

   setOperationPanel(prev => ({
     ...prev,
     result: path
   }));
 };

 const findLongestPath = (author) => {
   const visited = new Set();
   const currentPath = [];
   let longestPath = [];

   const dfs = (currentNode) => {
     visited.add(currentNode);
     currentPath.push(currentNode);

     const neighbors = graphData.links
       .filter(link => link.source === currentNode || link.target === currentNode)
       .map(link => link.source === currentNode ? link.target : link.source);

     let foundPath = false;
     neighbors.forEach(neighbor => {
       if (!visited.has(neighbor)) {
         foundPath = true;
         dfs(neighbor);
       }
     });

     if (!foundPath && currentPath.length > longestPath.length) {
       longestPath = [...currentPath];
     }

     visited.delete(currentNode);
     currentPath.pop();
   };

   dfs(author);
   return longestPath;
 };

 const findCollaborators = (author) => {
   const collaborators = graphData.links
     .filter(link => link.source === author || link.target === author)
     .map(link => ({
       name: link.source === author ? link.target : link.source,
       collaborationCount: link.weight
     }));

   setOperationPanel(prev => ({
     ...prev,
     result: collaborators
   }));
 };

 const handleOperation = (type) => {
   setOperationPanel(prev => ({ ...prev, type }));
   switch (type) {
     case 'CREATE_BST':
       if (operationPanel.author1) {
         createBST();
       }
       break;
     case 'CREATE_QUEUE':
       if (operationPanel.author1) {
         createCollaboratorQueue();
       }
       break;
     case 'SHORTEST_PATH':
       if (operationPanel.author1 && operationPanel.author2) {
         findShortestPath(operationPanel.author1, operationPanel.author2);
       }
       break;
     case 'LONGEST_PATH':
       if (operationPanel.author1) {
         const path = findLongestPath(operationPanel.author1);
         setOperationPanel(prev => ({
           ...prev,
           result: path
         }));
       }
       break;
     case 'COLLABORATORS':
       if (operationPanel.author1) {
         findCollaborators(operationPanel.author1);
       }
       break;
   }
 };

 return (
   <div className="flex h-screen bg-gray-50">
     {/* Sol Panel - Çıktı Ekranı */}
     <div className="w-1/4 p-4 border-r">
       <Card>
         <CardHeader>
           <CardTitle>Results</CardTitle>
         </CardHeader>
         <CardContent>
           {operationPanel.result && (
             <pre className="bg-gray-100 p-4 rounded-lg overflow-auto">
               {JSON.stringify(operationPanel.result, null, 2)}
             </pre>
           )}
         </CardContent>
       </Card>
     </div>

     {/* Orta Panel - Graf */}
     <div className="w-1/2 relative">
       <div className="absolute top-4 right-4 z-10 flex gap-2">
         <Button
           variant="outline"
           size="icon"
           onClick={() => setZoomLevel(z => Math.min(z * 1.2, 5))}
         >
           <ZoomIn className="h-4 w-4" />
         </Button>
         <Button
           variant="outline"
           size="icon"
           onClick={() => setZoomLevel(z => Math.max(z * 0.8, 0.1))}
         >
           <ZoomOut className="h-4 w-4" />
         </Button>
       </div>

       <ForceGraph2D
         graphData={graphData}
         nodeLabel={node => `${node.id}\nArticles: ${node.articles.length}`}
         nodeColor={node => node.color}
         nodeSize={node => node.size}
         linkWidth={link => Math.sqrt(link.weight)}
         onNodeClick={setSelectedNode}
         zoom={zoomLevel}
       />
     </div>

     {/* Sağ Panel - İşlemler */}
     <div className="w-1/4 p-4 space-y-4">
       <Card>
         <CardHeader>
           <CardTitle>Operations</CardTitle>
         </CardHeader>
         <CardContent className="space-y-4">
           <div className="space-y-2">
             <Input
               placeholder="First Author"
               value={operationPanel.author1}
               onChange={e => setOperationPanel(prev => ({
                 ...prev,
                 author1: e.target.value
               }))}
             />
             <Input
               placeholder="Second Author"
               value={operationPanel.author2}
               onChange={e => setOperationPanel(prev => ({
                 ...prev,
                 author2: e.target.value
               }))}
             />
           </div>

           <div className="space-y-2">
             <Button
               className="w-full"
               onClick={() => handleOperation('SHORTEST_PATH')}
             >
               Find Shortest Path
             </Button>
             <Button
               className="w-full"
               onClick={() => handleOperation('LONGEST_PATH')}
             >
               Find Longest Path
             </Button>
             <Button
               className="w-full"
               onClick={() => handleOperation('COLLABORATORS')}
             >
               Show Collaborators
             </Button>
           </div>
         </CardContent>
       </Card>

       {selectedNode && (
         <Card>
           <CardHeader>
             <CardTitle>Selected Author</CardTitle>
           </CardHeader>
           <CardContent>
             <div className="space-y-2">
               <p className="font-medium">{selectedNode.id}</p>
               <p>Total Articles: {selectedNode.articles.length}</p>
               <div className="mt-2">
                 <p className="font-medium">Recent Articles:</p>
                 <ul className="list-disc pl-4 mt-1">
                   {selectedNode.titles.slice(0, 3).map((title, i) => (
                     <li key={i} className="text-sm text-gray-600">{title}</li>
                   ))}
                 </ul>
               </div>
             </div>
           </CardContent>
         </Card>
       )}
     </div>
   </div>
 );
};

export default AuthorCollaborationNetwork;