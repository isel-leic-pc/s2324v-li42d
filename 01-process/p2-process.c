/* Goal: observing addresses of variables in parallel processes.
 *
 * By calling fork(), a new process is created as a copy of the
 * invoking process. Therefore, after a fork() we have two
 * executions of the same program running in parallel.
 *
 * This allows us to observe that multiple processes, even if
 * holding variables in the same exact memory addresses, are 
 * in fact using different physical memory locations for those 
 * variables, as they can hold different values at the same
 * time for each process.
 *
 * Look at globl_prog_var. It has the same address in both
 * processes, but it holds different values in each of them.
 *
 * This is possible by using virtual addresses instead of
 * physical addresses when executing a process. In runtime,
 * the CPU memory management unit (MMU) translates virtual
 * to physical addresses using a table preconfigured by the
 * operating system for each process.
 * 
 * [More details about this are discussed in TVS.] 
 */

#include <pthread.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

pid_t proc_id;

int globl_prog_var;

void func(int count) {
	int local_func_var = rand();
	
	printf("[%d] &globl_prog_var=%p; value=%d (func)\n",
	       proc_id, &globl_prog_var, globl_prog_var);

	printf("[%d] &local_func_var=%p; value=%d (entry)\n",
	       proc_id, &local_func_var, local_func_var);
	
	if (count > 0) {
		// When running two parallel processes, we may observe
		// one of those processes executing from start to ending
        // before the other one has a chance to start running.
        // By "sleeping" here, we increase the likelihood of
		// having both processes interleaving their executions.
		sleep(1);
		func(count - 1);
	}

	printf("[%d] &local_func_var=%p; value=%d (exit)\n",
			proc_id, &local_func_var, local_func_var);
}

int main(int argc, char * argv[]) {
	// Not the typical way to use fork().
	// [More details in TVS.]
	fork();
	
	proc_id = getpid();

	srand(time(NULL) * proc_id);
	globl_prog_var = rand();
	
	int local_main_var = rand();

	printf("[%d] running\n", proc_id);

	printf("[%d] &globl_prog_var=%p; value=%d (main)\n",
	       proc_id, &globl_prog_var, globl_prog_var);

	printf("[%d] &local_main_var=%p; value=%d (entry)\n",
	       proc_id, &local_main_var, local_main_var);
	
	int count = argc > 1 ? atoi(argv[1]) : 3;
	
	func(count);

	printf("[%d] &local_main_var=%p; value=%d (exit)\n",
	       proc_id, &local_main_var, local_main_var);

	printf("[%d] ending\n", proc_id);	
}
