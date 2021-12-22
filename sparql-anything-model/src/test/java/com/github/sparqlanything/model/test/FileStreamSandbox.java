/*
 * Copyright (c) 2021 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.sparqlanything.model.test;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class FileStreamSandbox {

	@Ignore
	@Test
	public void test(){
		Queue buffer = new Queue();
		Thread t = new Thread(new Producer(buffer));
		t.start();
		//new Consumer(buffer).run();
		Iterator it = new IteratorConsumer(buffer).getIterator();
		while(it.hasNext()){
			System.out.println("Next is: " + it.next());
		}
	}

	class Queue extends LinkedBlockingQueue<Object> {
		boolean finished = false;
		public synchronized void setFinished(){
			finished = true;
		}
		public synchronized boolean hasFinished(){
			return finished;
		}
	}

	class Producer implements Runnable {
		int c = 0;
		int max = 10;
		Queue queue = null;
		Producer(Queue queue){
			this.queue = queue;
		}

		@Override
		public void run() {
			while(c < max){
				try {
					Thread.sleep(1000);
					c = c+1;
					Object o = c;
					System.out.println("Put " + o);
					queue.put(o);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			queue.setFinished();
		}
	}

	class Consumer implements Runnable {
		Queue queue = null;
		Consumer(Queue queue){
			this.queue = queue;
		}

		@Override
		public void run() {
			while(!queue.hasFinished()){
				try {
					Thread.sleep(1000);
					if(!queue.isEmpty()){
						Object o = queue.take();
						System.out.println("take " + o);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class IteratorConsumer {
		Queue queue = null;
		IteratorConsumer(Queue queue){
			this.queue = queue;
		}

		public Iterator<Object> getIterator() {
			return new Iterator<Object> (){
				private Object next = null;
				@Override
				public boolean hasNext() {
					while(!queue.hasFinished()){
						try {
							if(!queue.isEmpty()){
								Object o = queue.take();
								System.out.println("take " + o);
								next = o;
								return true;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					return false;
				}

				@Override
				public Object next() {
					return next;
				}
			};

		}
	}
}
