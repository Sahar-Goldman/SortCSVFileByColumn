/*
	********************
	*program work plan:*
	********************
	
	|--------------||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
	|#Program input|   (1.)key - column to be sorted by, the column number (columns number increase from left ro right																					|
	|			   |			 starting with 0 for first column like an array).	 														 															|
	|			   |																														 															|
	|			   |   (2.)max_records_in_memory - Number of records program can save at the same time in memory during calculations.		 															|
	|			   |																														 															|
	|			   |   (3.)Full file name - file in CSV format, path included.																 															|
	|			   |		 																												 															|
	|--------------||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|						 
	#Program output||  (1.)Sorted new file according to given key parameter - "SortFile\Sorted.csv" 										 															|
	|--------------||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
	#Program flow  ||  (1.)Check parameters:																								 															|
	|			   ||		* Check 3 parameters accepted.								 											 	     															|
	|			   ||																														 															|
	|			   ||		* key:  	 																									 															|
	|			   ||				-	check if column number in range of file's record length.			 								 															|
	|			   ||		* max_records_in_memory:																						 															|
	|			   ||				-	check max_records_in_memory not smaller then 2 (min value for comparison sort).						 															|
	|			   ||																														 															|
	|			   ||		* Full file name:																				    			 															|
	|			   ||				-	check if file emax_records_in_memoryists, check premissions - read option valid.					 															|
	|			   ||																														 															|
	|			   ||	(2.)Iterate on input file:																							 															|
	|			   ||				-	Read max_records_in_memory records (maybe emax_records_in_memorycept last iteration that has less).	 															|
	|			   ||				-	Sort records by key column.																			 															|
	|			   ||				note: comparison is according to lemax_records_in_memoryicographic order, ascending diraction.			 															|	
	|			   ||				-	Save sorted records on temp file.																	 															|
	|			   ||				note: case key value is empty, record will be considered smaller then all other value.			 		 															|
	|			   ||																														 															|
	|			   ||	(3.)Iterate on directory files until 1 file pointer remains:														 															|
	|			   ||				-	Merge every 2 files into one sorted file. 		   													 															|
	|			   ||																														 															|
	|--------------||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|															|
	#Complemax_records_in_memoryity    ||	Step 1 :	O(n) (n - number of total records)													 															|
	|			   ||	Step 2 : 																																										|
	|			   ||				-	sort max_records_in_memory sized record takes: 	max_records_in_memory*log(max_records_in_memory)	 															|
	|			   ||				-	how many max_records_in_memory sized records? 	n/max_records_in_memory								 															|
	|			   ||				=>								 																		 															|
	|			   ||					max_records_in_memory*log(max_records_in_memory)*(n/max_records_in_memory) = log(max_records_in_memory)*n 						 								|
	|			   ||				=>  O(n)																								 															|
	|			   ||																																													|
	|			   ||	Step 3 : 																										    															|
	|			   ||				- iteration 1:									 																													|
	|			   ||								- have((n/max_records_in_memory)%2 == 0)? n/max_records_in_memory : [n/max_records_in_memory]+1; files to merge							    		|
	|			   ||								- merge 2 at a time																		 															|
	|			   ||								=> results in((n/max_records_in_memory)%2 == 0)? n/((2^1)*max_records_in_memory) : [n/((2^1)*max_records_in_memory)] + 1; new files 				|
	|			   ||				- iteration 2:																							 															|
	|			   ||								- ((n/(2*max_records_in_memory))%2 == 0)? n/(2*max_records_in_memory) : [n/(2*max_records_in_memory)] + 1; files to merge							|
	|			   ||								- merge 2 at a time																		 															|
	|			   ||								=> results in((n/(2*max_records_in_memory))%2 == 0)? n/((2^2)*max_records_in_memory) : [n/((2^2)*max_records_in_memory)] + 1; new files     		|	
	|			   ||				- iteration 3:																							 															|
	|			   ||								- ((n/(4*max_records_in_memory))%2 == 0)? n/(4*max_records_in_memory) : [n/(4*max_records_in_memory)] + 1; files to merge							|
	|			   ||								- merge 2 at a time																																	|
	|			   ||								=> results in ((n/(4*max_records_in_memory))%2 == 0)? n/((2^3)*max_records_in_memory) : [n/((2^3)*max_records_in_memory)] + 1; new files    		|
	|			   ||				...																										 															|
	|			   ||				- iteration Z:																							 															|
	|			   ||								- ((n/((2^Z)*max_records_in_memory)%2 == 0)? 1 : 2; files to merge		 									 										|
	|			   ||								=> results in 1 new file or 1 more iteration if files number is 2 					     															|
	|			   ||								- n/((2^Z)*max_records_in_memory = 1  => n/max_records_in_memory = 2^Z =>  	Z = log(n/max_records_in_memory) = log(n) - log(max_records_in_memory) 	|
	|			   ||								=> number of iterations is : O(log(n))													 															|
	|			   ||								- on each iteration we merge all records 												 															|
	|			   ||								=> O(n*log(n)) for this step															 															|
	|			   ||																														 															|
	|			   ||				******** runtime in total of O(n*log(n)) complemax_records_in_memoryity	********											 										|
	|--------------||-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
	

*/