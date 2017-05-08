#Topology Mining Strategy Implementation
This package implementates Robillard's topology (1) analysis. The main source code could be found at [Suade:http://www.cs.mcgill.ca/~swevo/suade/](http://www.cs.mcgill.ca/~swevo/suade/) plugin. The Suade Plugin dependences on [ConcernMapper:http://cs.mcgill.ca/~martin/cm/](http://cs.mcgill.ca/~martin/cm/) plugin (2). We have integrated the 
these two plugins' source code into our application, therefore no need to install these plugins to run our application.


The topology mining strategy is a static-mining strategy, which mainly based on extracting call information from program. In our customized implementation, basic information including call information, control flow information, AST(abstract syntax tree) information are all shared in all mining strategies. 



##Reference
1. Robillard, Martin P. "Topology analysis of software dependencies." ACM Transactions on Software Engineering and Methodology (TOSEM) 17.4 (2008): 18.
2. Robillard, Martin P., and Frédéric Weigand-Warr. "ConcernMapper: simple view-based separation of scattered concerns." Proceedings of the 2005 OOPSLA workshop on Eclipse technology eXchange. ACM, 2005.
