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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

/**
 * The default ErrorListener that reports all warnings and errors to
 * {@link System#err} and does not throw any exceptions.
 */
public class DefaultErrorListener implements ErrorListener
{
 public void error(TransformerException exception) throws TransformerException
 {
  System.err.printf("Transformation error in %s: %s%n", transformer, exception.getMessageAndLocation());
 }

 public void fatalError(TransformerException exception)
   throws TransformerException
 {
  System.err.printf("Transformation failed by %s: %s%n", transformer, exception.getMessageAndLocation());
 }

 public void warning(TransformerException exception)
   throws TransformerException
 {
  System.err.printf("Transformation warning by %s: %s%n", transformer, exception.getMessageAndLocation());
 }

 public DefaultErrorListener(Transformer transformer)
 {
  this.transformer = transformer;
 }

 private Transformer transformer;
}
