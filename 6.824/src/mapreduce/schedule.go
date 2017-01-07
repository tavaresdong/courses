package mapreduce

import (
	"fmt"
	"sync"
)

// schedule starts and waits for all tasks in the given phase (Map or Reduce).
func (mr *Master) schedule(phase jobPhase) {
	var ntasks int
	var nios int // number of inputs (for reduce) or outputs (for map)
	switch phase {
	case mapPhase:
		ntasks = len(mr.files)
		nios = mr.nReduce
	case reducePhase:
		ntasks = mr.nReduce
		nios = len(mr.files)
	}

	fmt.Printf("Schedule: %v %v tasks (%d I/Os)\n", ntasks, phase, nios)

	// All ntasks tasks have to be scheduled on workers, and only once all of
	// them have been completed successfully should the function return.
	// Remember that workers may fail, and that any given worker may finish
	// multiple tasks.
	//
	// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
	//

	// Use WaitGroup to wait for all tasks to finish
	var wg sync.WaitGroup
	for i := 0; i < ntasks; i++ {
		wg.Add(1)
		go func(taskNum int) {
			defer wg.Done()
			dtarg := DoTaskArgs{}
			dtarg.JobName = mr.jobName
			dtarg.File = mr.files[taskNum]
			dtarg.Phase = phase
			dtarg.TaskNumber = taskNum
			dtarg.NumOtherPhase = nios
			for {
				availWorker := <- mr.registerChannel

				// RPC call worker to run synchronously
				ok := call(availWorker, "Worker.DoTask", &dtarg, new(struct{}))
				if ok {
					go func() {
						mr.registerChannel <- availWorker
					}()
					break;
				}
			}
		}(i)
	}

	// Wait for all tasks to complete
	wg.Wait()
	fmt.Printf("Schedule: %v phase done\n", phase)
}
