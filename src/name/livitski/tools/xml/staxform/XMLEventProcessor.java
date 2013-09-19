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

package name.livitski.tools.xml.staxform;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

import name.livitski.tools.xml.staxform.helpers.IdentityEventProcessor;

/**
 * A simple contract for user-defined transformations of
 * {@link XMLEvent XML events}. XML input is pushed into
 * the processor via the {@link #add(XMLEvent)} method.
 * Once the processor
 * has output ready, it returns <code>true</code> from
 * {@link #hasNext()} and furnishes its results via
 * {@link #next()}. The callers may post zero, one, or more
 * {@link XMLEvent XML events} to the processor before
 * polling it for output. Processors do not have to yield
 * any output until they receive the required input. They
 * MUST, however, retain all the output they create in its
 * original sequence until polled or {@link #reset() reset},
 * regardless of any extra input they may receive before
 * being polled. Processors
 * MAY throw an {@link XMLStreamException} from the
 * {@link #add(XMLEvent)} method if they run out of resources
 * needed to store the input.
 * <br/>Implementors are NOT expected to support the
 * {@link #remove()} method which currently has no meaning
 * when applied to this interface.
 * <br />Implementors may extend, or delegate to, an instance
 * of {@link IdentityEventProcessor} to store the events
 * they produce until polled.
 */
public interface XMLEventProcessor
	extends XMLEventConsumer, Iterator<XMLEvent>
{
 /**
  * Frees resources associated with this processor and returns
  * it to the initial state.
  * @throws XMLStreamException if there is a problem resetting the
  * processor  
  */
 public void reset() throws XMLStreamException;


 /**
  * Set the <code>debug</code> system property to <code>true</code> to see
  * the complete stack trace of an error when it occurs.
  */
 public static final String DEBUG_PROPERTY = "debug";
}
