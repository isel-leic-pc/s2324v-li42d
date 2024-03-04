/* Goal: observing addresses of variables in parallel threads.
 *
 * Multiple threads can be executed inside a process.
 *
 * All threads share the same global variables. However, as every
 * thread has an independent execution flow, each of them requires
 * its own stack, where return addresses, arguments of calls and
 * local variables are stored.
 * 
 * Observe here the same function (func) being invoked in parallel
 * by two threads. Their local variables will reside in clearly
 * different address ranges.
 *
 * It is not easy to distinguish the output of each thread, except
 * by the large distance between the addresses of local variables. 
 */

#include <pthread.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

pid_t proc_id;

int globl_prog_var;

void * func(void * pcount) {
	int count = *(int *)pcount;
	int local_func_var = rand();
	
	printf("[%d] &globl_prog_var=%p; value=%d (func)\n",
	       proc_id, &globl_prog_var, globl_prog_var);

	printf("[%d] &local_func_var=%p; value=%d (entry)\n",
	       proc_id, &local_func_var, local_func_var);
	
	if (count > 0) {
		sleep(1);
		count -= 1;
		func(&count);
	}

	printf("[%d] &local_func_var=%p; value=%d (exit)\n",
			proc_id, &local_func_var, local_func_var);

	return NULL;
}

int main(int argc, char * argv[]) {
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
	
	pthread_t t1;
	pthread_t t2;
	
	pthread_create(&t1, NULL, func, &count);  // launch thread t1
	pthread_create(&t2, NULL, func, &count);  // launch thread t2

	pthread_join(t1, NULL);  // wait for thread t1 (the main thread is blocked while waiting)
	pthread_join(t2, NULL);  // wait for thread t2 (the main thread is blocked while waiting)

	printf("[%d] &local_main_var=%p; value=%d (exit)\n",
	       proc_id, &local_main_var, local_main_var);

	printf("[%d] ending\n", proc_id);	
}
