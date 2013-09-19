/**
 *    Copyright © 2013 Konstantin Livitski
 * 
 *    This file is part of StAXform. StAXform is
 *    licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package name.livitski.tools.xml.staxform.helpers;

import java.util.LinkedList;
import java.util.Queue;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import name.livitski.tools.xml.staxform.XMLEventProcessor;

/**
 * A trivial {@link XMLEventProcessor} that outputs the
 * same sequence of events that it is fed. 
 */
public class IdentityEventProcessor implements XMLEventProcessor
{
 public IdentityEventProcessor()
 {
 }

 public void reset() throws XMLStreamException
 {
  queue.clear();
 }

 public void add(XMLEvent event) throws XMLStreamException
 {
  if(!queue.offer(event))
   throw new XMLStreamException(this
     + " has run out of space on event queue with "
     + queue.size() + " event(s)");
 }

 public boolean hasNext()
 {
  return !queue.isEmpty();
 }

 public XMLEvent next()
 {
  return queue.remove();
 }

 public void remove()
 {
  throw new UnsupportedOperationException();
 }

 private Queue<XMLEvent> queue = new LinkedList<XMLEvent>();
}
