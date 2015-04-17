package com.thend.home.sweethome.practice;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Test {
	
	private int[] heap;
	private int size;
	
	public int getPivotPos(int[] arr, int left, int right) {
		if(left > right) {
			return -1;
		}
		int pivot = (left + right) / 2;
		int pivotValue = arr[pivot];
		while(left < right) {
			while((left < right) && (pivot < right) && (pivotValue <= arr[right]))
				right--;
			arr[pivot] = arr[right];
			while((left < right) && (pivotValue >= arr[left]))
				left++;
			arr[right] = arr[left];
			pivot = left;
		}
		arr[left] = pivotValue;
		return pivot;
	}
	
	public void doQuick(int[] arr, int left, int right) {
		if(left >= right) {
			return;
		}
		int pivot = getPivotPos(arr, left, right);
		doQuick(arr, left, pivot-1);
		doQuick(arr, pivot+1, right);
	}
	
	public void doHeap(int[] arr, int top) {
		heap = new int[top];
		size = 0;
		for(int item : arr) {
			if(size < top) {
				size++;
				heap[size - 1] = item;
				build();
			} else {
				if(heap[0] < item) {
					heap[0] = item;
					adjust();
				}
			}
		}
		while(size > 0) {
			System.out.println(heap[0]);
			heap[0] = heap[size - 1];
			size--;
			adjust();
		}
	}
	
	public void build() {
		int i = size - 1;
		int j = (i - 1) >>> 1;
		int newValue = heap[i];
		while((i > 0) && newValue < heap[j]) {
			heap[i] = heap[j];
			i = j;
			j = (i - 1) >>> 1;
		}
		heap[i] = newValue;
	}
	
	public void adjust() {
		int newValue = heap[0];
		int i = 0;
		int j = (i<<1) + 1;
		int k = j + 1;
		if(k < size && heap[j] > heap[k]) {
			j = k;
		}
		while(j < size && heap[j] < newValue) {
			heap[i] = heap[j];
			i = j;
			j = (i<<1) + 1;
			k = j + 1;
			if(k < size && heap[j] > heap[k]) {
				j = k;
			}
		}
		heap[i] = newValue;
	}
	
	public void doStack() {
		Stack<Integer> stack = new Stack<Integer>();
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.push(4);
		System.out.println(stack.peek());
		System.out.println(stack.pop());
		System.out.println(stack.peek());
	}
	
	public void doQueue() {
		ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
		queue.add(1);
		queue.add(2);
		queue.add(3);
		queue.add(4);
		System.out.println(queue.peek());
		System.out.println(queue.poll());
		System.out.println(queue.peek());
	}
	
	public static void main(String[] args) {
		Test test = new Test();
		int[] arr = new int[100];
		Random ran = new Random();
		for(int i=0;i<100;i++) {
			arr[i] = ran.nextInt(1000);
		}
		//快排
//		test.doQuick(arr, 0, 99);
//		test.doStack();
//		test.doQueue();
		test.doHeap(arr, 10);
		
		//打印
		for(int i : arr) {
			System.out.print(i + " ");
		}
	}

}
