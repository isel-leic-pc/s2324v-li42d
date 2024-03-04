/* Goal: observing addresses of variables in a process.
 *
 * Global variables exist from start to end with a constant address.
 *
 * Local variables are allocated in the execution stack every time
 * their function is invoked. Each function invocation creates a new
 * set of local variables for that function. This is easily observable
 * for recursive functions, where successive recursive calls, create
 * new instances of the same local variables, each time with lower
 * addresses. Then, at every return, we can observe that the previous
 * set of local variables as been preserved and becomes active again.
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
		func(count - 1);
	}

	printf("[%d] &local_func_var=%p; value=%d (exit)\n",
			proc_id, &local_func_var, local_func_var);
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
	
	func(count);

	printf("[%d] &local_main_var=%p; value=%d (exit)\n",
	       proc_id, &local_main_var, local_main_var);

	printf("[%d] ending\n", proc_id);	
}
