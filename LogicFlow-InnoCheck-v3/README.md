# How to install
In this section, we explain how to install the prototype comprising the running example at LogicFlow AG. Follow the next steps in order to install/execute the prototype: 
1. First, you need an Eclipse IDE distribution. You can download one [here](https://www.eclipse.org/downloads/).
2. Download the prototype's source code. 
3. Extract the .rar/.zip file containing the source code. 
4. Import the project in your Eclipse IDE workspace. 
5. Update the maven dependencies. 
6. Run the class "```src/main/java/ontology/view/TraceView.java```" 
7. Now, you should see the prototype user interface as follows: 
![image](https://user-images.githubusercontent.com/18057711/144012198-472974a4-a9d0-447b-bac7-a8924b3c2f52.png)

# How to use
Now you have access to the prototype. Do you wan to "play" with it? That is nice! we prepared some instructions below with images for guiding you through the prototype. 

## What are the rectangles with text? 
The rectangles represent the individual instances of the context-dependent artifacts. The white rectangles with black letters ![image](https://user-images.githubusercontent.com/18057711/144013380-23b4fcd9-90db-457c-8f54-799a3bd6dbb7.png) represent the source artifacts. In running example at LogicFlow AG these are functional and non-functional requirements. On the other hand, the black rectangles with white letters ![image](https://user-images.githubusercontent.com/18057711/144013433-b1633e52-7017-4556-a522-a9881cfef2b7.png) represent the target artifacts. In the running example at LogicFlow AG these are Selenium Script commands and detected UI changes.

## How can I use the controls at the top of the interface? 
At the top of the user interface you will find 3 radio buttons: Show traces, Show untraced sources and Show untraced targets.

![image](https://user-images.githubusercontent.com/18057711/144013844-0bad85f1-1534-4792-a7b7-a026626f2cc2.png)

Initially, all of them are unselected. Now, in the following points we explain how each of them works: 
1. **"Show traces"**: as soon as it is active, the traces between the artifacts are displayed. Initially, the artifacts have no traces. Later in this guide we will show how to trace them. Note that when adding a new trace, you must deselect and select again "Show traces" radio button to update the user interface. This functionality uses the SPARQL query in: [```WhatAreTheTracesBetweenMyArtifacts.txt```](https://github.com/DavidMosquera/TraceabilityOntology/blob/main/SPARQL-Queries/WhatAreTheTracesBetweenMyArtifacts.txt) file. We show how a trace looks like in the following image: 

![image](https://user-images.githubusercontent.com/18057711/144014323-67d07b9d-f4ae-4710-ab2b-9a9c28275048.png)

2. **"Show not traced source artifacts"**: as soon as it is active, the source artifacts that are not yet traced get a red rectangle inside them. Note that wen adding a new trace, you must select and deselect again "Show not traced source artifacts" radio button to update the user interface. This functionality uses the SPARQL query in: [```isThereAnyUntracedSourceArtifact.txt```](https://github.com/DavidMosquera/TraceabilityOntology/blob/main/SPARQL-Queries/isThereAnyUntracedSourceArtifact.txt). We show how a untraced source artifact looks like in the following image: 

![image](https://user-images.githubusercontent.com/18057711/144015306-28cc9332-3ae0-4493-b71e-fe6d412e3bee.png)

3. **"Show not traced target artifacts"**: as soon as it is active, the source artifacts that are not yet traced get a red rectangle inside them. Note that wen adding a new trace, you must select and deselect again "Show not traced target artifacts" radio button to update the user interface. This functionality uses the SPARQL query in: [```isThereAnyUntracedTargetArtifact.txt```](https://github.com/DavidMosquera/TraceabilityOntology/blob/main/SPARQL-Queries/isThereAnyUntracedTargetArtifact.txt). We show how a untraced target artifact looks like in the following image: 

![image](https://user-images.githubusercontent.com/18057711/144015550-80529416-e116-4561-be46-fcbee6b5e9cd.png)

## How can I access to trace suggestions between source/target artifacts?
One of the most interesting features of our prototype is that it suggests possible traces between artifacts based on automatic reasoning. To access these suggestions, simply click on the artifact for which you would like to know the suggestions. You can access the source or target artifact suggestions by simply clicking on the rectangle representing the artifact in the user interface. We show an example in the following images: 

![image](https://user-images.githubusercontent.com/18057711/144016428-ea5326ef-fabb-4b75-bff3-f496950f1b9b.png) ![image](https://user-images.githubusercontent.com/18057711/144016473-982825fd-a84f-4dbc-8ce6-759b9ee649ab.png)

As you may have noticed, our prototype hides artifacts that are not within the suggestions. When you want to return to see all artifacts, simply click outside the rectangles. All artifacts will automatically reappear and the suggestions will disappear. The suggestion system uses SPARQL queries located in: [```WhatAreThePossibleTraceableSourcesGivenATargetArtifact.txt```](https://github.com/DavidMosquera/TraceabilityOntology/blob/main/SPARQL-Queries/WhatAreThePossibleTraceableSourcesGivenATargetArtifact.txt) and [```WhatAreThePossibleTraceableTargetsGivenASourceArtifact.txt```](https://github.com/DavidMosquera/TraceabilityOntology/blob/main/SPARQL-Queries/WhatAreThePossibleTraceableTargetsGivenASourceArtifact.txt) files.

**IMPORTANT!:** By default, when you select the "Show traces" radio button, the suggestion function is disabled to allow you to interact with the artifacts at the user interface. 

## How can I trace the artifacts? 
So far we have reviewed the entire automatic reasoning queries for supporting the trace generation. Now, to trace the artifacts we will use the menu at the bottom of the user interface. 

![image](https://user-images.githubusercontent.com/18057711/144017653-4fb3a827-e7e4-4b69-baf5-be51cbed841d.png)

To do so, you need to select the source and target artifacts from the drop-down menus. Then you must press the "Trace!" button to store the trace in the ontology. Remember that to display the trace you will need to activate or reactivate the "Show traces." radio button. 



